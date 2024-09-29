package io.github.md2java.service.endpoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.github.md2java.model.NodeInfo;
import io.github.md2java.service.lock.LockInfoDataService;

@Component
@Endpoint(id = "clusterlockinfo")
@ConditionalOnProperty(name = "md2java.util.cluser-lock.enabled",havingValue = "true",matchIfMissing = false)
public class ClusterlockinfoEndpoint {

	@Autowired
	private LockInfoDataService lockInfoDataService;

	@ReadOperation
	public Map<String, NodeInfo> clusterInfo() {
		Map<String, NodeInfo> ret = new ConcurrentHashMap<>();
		ret.put("responser", lockInfoDataService.findNodeInfo());
		ret.putAll(lockInfoDataService.findClusterInfo());
		return ret;
	}
}