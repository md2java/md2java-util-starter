package io.github.md2java.service.lock;

import org.aspectj.lang.ProceedingJoinPoint;

public interface ClusterSingletonService {
	Object allowToClusterLock(ProceedingJoinPoint pjp);

}
