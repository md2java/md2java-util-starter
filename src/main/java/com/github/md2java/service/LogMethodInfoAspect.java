package com.github.md2java.service;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.github.md2java.anno.LogMethodInfo;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LogMethodInfoAspect {

	@Pointcut("@annotation(com.github.md2java.anno.LogMethodInfo)")
	public void logMethodInfo() {

	}
	
	@Around("logMethodInfo()")
	public Object handlelLogMethodInfo(ProceedingJoinPoint pjp) throws Throwable {
		LogMethodInfo logMethodInfo = findLogMethodInfo(pjp);
		long startTime = System.currentTimeMillis();
		if (logMethodInfo.logRequest()) {
			log.info("==> Method:{} request:{}", pjp.getSignature().toShortString(), Arrays.toString(pjp.getArgs()));
		}
		Object ret = pjp.proceed();
		if (logMethodInfo.logResponse()) {
			log.info("==< Method:{} response:{}", pjp.getSignature().toShortString(), ret);
		}
		if (logMethodInfo.logTime()) {
			log.info("##method={}##execution-time={}ms", pjp.getSignature().toShortString(),(System.currentTimeMillis() - startTime));
		}

		return ret;

	}


	private LogMethodInfo findLogMethodInfo(ProceedingJoinPoint pjp) {
		    Method method = findMethod(pjp);
		    LogMethodInfo myAnnotation = method.getAnnotation(LogMethodInfo.class);
		return myAnnotation;
	}


	private Method findMethod(ProceedingJoinPoint pjp) {
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod();
		return method;
	}

}
