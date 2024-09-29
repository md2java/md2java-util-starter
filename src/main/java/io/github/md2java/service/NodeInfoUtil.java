package io.github.md2java.service;

import java.net.InetAddress;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class NodeInfoUtil {
	private String nodeHost;
	@Value("${server.port:8080}")
	private String port;
	private String nodeId;

	@PostConstruct
	public void init() {
		this.nodeHost = getHostname();
		this.nodeId = String.format("%s-%s", nodeHost, port);

	}

	private String getHostname() {
		String hostName = null;
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			hostName = localHost.getHostName();
		} catch (Exception e) {
			hostName = UUID.randomUUID().toString();
		}
		return hostName;

	}
}