package io.github.md2java.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class LogMethodInfoAspect {

	private final MethodLogger methodLogger;

	@Pointcut("@annotation(io.github.md2java.anno.LogMethodInfo)")
	public void logMethodInfo() {

	}

	@Around("logMethodInfo()")
	public Object handlelLogMethodInfo(ProceedingJoinPoint pjp) throws Throwable {
		Object ret = methodLogger.logMethod(pjp);
		return ret;

	}

}
