package io.github.md2java.service.lock;

import java.util.Map;

import io.github.md2java.model.NodeInfo;

public interface LockInfoDataService {
	/**
	 * @return
	 * return false if not found or found with false, else true
	 */
	boolean isLockHeld();
	/**
	 * 
	 * @return lockinfo details created at start 
	 */
	Map<String, NodeInfo> findLockInfo();
	NodeInfo findNodeInfo();
	Map<String, NodeInfo> findClusterInfo();

}
