package io.github.md2java.service;

import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
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
	
	public static String findRootCauseMessage(Exception e) {
		StackTraceElement[] stackTraceElements = e.getStackTrace();
		AtomicInteger lastCause = new AtomicInteger(0);
		while (true && lastCause.getAndIncrement() < stackTraceElements.length) {
			String className = stackTraceElements[lastCause.get()].getClassName();
			if (StringUtils.startsWith(className, "io.github.md2java")) {
				int lineNumber = stackTraceElements[lastCause.get()].getLineNumber();
				String methodName = stackTraceElements[lastCause.get()].getMethodName();
				return String.format("%s:%s:%s", className, methodName, lineNumber);
			}
		}
		return "";
	}

}