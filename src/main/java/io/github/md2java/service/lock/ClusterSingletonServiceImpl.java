package io.github.md2java.service.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "md2java.util.cluser-lock.enabled",havingValue = "true",matchIfMissing = false)
public class ClusterSingletonServiceImpl implements ClusterSingletonService {

	private final LockInfoDataService lockInfoDataService;

	@Override
	public Object allowToClusterLock(ProceedingJoinPoint pjp) {
		Object ret = null;
		if (lockInfoDataService.isLockHeld()) {
			try {
				ret = pjp.proceed();
			} catch (Throwable e) {
				log.error("went wrong: ", e);
			}
		}
		return ret;
	}

}
