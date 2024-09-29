package io.github.md2java.service.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "md2java.util.cluser-lock.enabled",havingValue = "true",matchIfMissing = false)
public class ClusterLockAspect {

	private final ClusterSingletonService clusterSingletonService;

	@Pointcut("@annotation(io.github.md2java.anno.ClusterLock)")
	public void clusterLock() {

	}

	@Around("clusterLock()")
	public Object handlelLogMethodInfo(ProceedingJoinPoint pjp) throws Throwable {
		Object ret = clusterSingletonService.allowToClusterLock(pjp);
		return ret;

	}

}
