package io.github.md2java.service.lock;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.github.md2java.model.NodeInfo;
import io.github.md2java.service.NodeInfoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author md2java
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "md2java.util.cluser-lock.enabled", havingValue = "true", matchIfMissing = false)
public class LockInfoDataServiceImpl implements LockInfoDataService {

	private Map<String, NodeInfo> lockInfo = new ConcurrentHashMap<>();
	private Map<String, NodeInfo> clusterLockInfo = new ConcurrentHashMap<>();

	private final NodeInfoUtil nodeInfoUtil;

	@Value("${md2java-util.lock.lock-at-most.min:10}")
	private long lockAtMost;

	@Value("${app.host:http://localhost:8080}")
	private String applicationHost;

	@Value("${md2java-util.cluster.size:2}")
	private int clusterSize;

	private RestTemplate restTemplate;

	@PostConstruct
	public void init() {
		restTemplate = buildTemplate(60 * 1000, 60 * 1000);
		NodeInfo nodeInfo = NodeInfo.builder().lockActive(false).lockAtMost(lockAtMost).nodeId(nodeInfoUtil.getNodeId())
				.build();
		lockInfo.put(nodeInfoUtil.getNodeId(), nodeInfo);
		log.debug("lockinfo setup at start: {} ", lockInfo);
		ScheduledExecutorService workers = Executors.newScheduledThreadPool(2,
				new CustomizableThreadFactory("node_sync_"));
		String syncUrl = String.format("%s/actuator/nodelockinfo", applicationHost);
		workers.scheduleAtFixedRate(() -> {
			try {
				Map<String, NodeInfo> info = this.getInfo(syncUrl);
				log.debug("received: {} ", info);
				if (Objects.isNull(info)) {
					updateNodeInfo(true);
					return;
				}

				info.remove("responser");
				clusterLockInfo.putAll(info);
				info.remove(nodeInfoUtil.getNodeId());
				Optional<NodeInfo> otherActiveNode = info.values().stream().filter(s -> s.isLockActive()).findAny();
				if (otherActiveNode.isPresent()) {
					updateNodeInfo(false);
					log.debug("lockinfo: {} ", lockInfo);
					return;
				}
				updateNodeInfo(true);
				log.debug("lockinfo: {} ", lockInfo);

			} catch (Exception e) {
				log.error("went wrong: {} cause:{}", e.getMessage(),NodeInfoUtil.findRootCauseMessage(e));
			}

		}, 0, 10, TimeUnit.SECONDS);
	}

	private void updateNodeInfo(boolean activeFlag) {

		NodeInfo findNodeInfo = this.findNodeInfo();
		if (activeFlag && findNodeInfo.isLockActive()) {
			switchNodeIfNeeded(findNodeInfo);
			return;
		}
		findNodeInfo.setLockActive(activeFlag);
		if (activeFlag) {
			findNodeInfo.setLockAtSince(LocalDateTime.now());
			return;
		}
		findNodeInfo.setLockAtSince(null);
	}

	private void switchNodeIfNeeded(NodeInfo findNodeInfo) {
		LocalDateTime lockAtSince = findNodeInfo.getLockAtSince();
		if (Objects.isNull(lockAtSince)) {
			return;
		}
		if (lockAtSince.plusMinutes(findNodeInfo.getLockAtMost()).isBefore(LocalDateTime.now())) {
			findNodeInfo.setLockActive(false);
			findNodeInfo.setLockAtSince(null);
		}
	}

	private RestTemplate buildTemplate(int connTimeout, int readTimeout) {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(connTimeout);
		factory.setReadTimeout(readTimeout);
		return new RestTemplate(factory);
	}

	@Override
	public boolean isLockHeld() {
		NodeInfo nodeInfo = lockInfo.get(nodeInfoUtil.getNodeId());
		if (Objects.isNull(nodeInfo)) {
			log.debug("lockinfo data: {} and looking key: {} ", lockInfo, nodeInfoUtil.getNodeId());
			return false;
		}
		return nodeInfo.isLockActive();
	}

	@Override
	public Map<String, NodeInfo> findLockInfo() {
		return lockInfo;
	}

	@Override
	public NodeInfo findNodeInfo() {
		return lockInfo.get(nodeInfoUtil.getNodeId());
	}

	@Override
	public Map<String, NodeInfo> findClusterInfo() {
		return clusterLockInfo;
	}

	private Map<String, NodeInfo> getInfo(String syncUrl) {
		Map<String, NodeInfo> ret = new ConcurrentHashMap<>();
		AtomicInteger atomicInteger = new AtomicInteger(clusterSize + 5);
		while (atomicInteger.getAndDecrement() > 0) {
			try {
				ResponseEntity<Map<String, NodeInfo>> response = restTemplate.exchange(syncUrl, HttpMethod.GET, null,
						new ParameterizedTypeReference<Map<String, NodeInfo>>() {
						});
				Map<String, NodeInfo> immediateRes = response.getBody();
				if (Objects.nonNull(immediateRes)) {
					ret.putAll(immediateRes);
				}
				if (ret.size() > clusterSize) {
					break;
				}

			} catch (Exception e) {
				String rootCause = NodeInfoUtil.findRootCauseMessage(e);
				log.error("went wrong: {} rootcause: {} ", e.getMessage(),rootCause);
			}
		}
		if (BooleanUtils.isFalse(ret.size() > clusterSize)) {
			clusterLockInfo.clear();
			clusterLockInfo.putAll(ret);
		}
		return ret;
	}
}
