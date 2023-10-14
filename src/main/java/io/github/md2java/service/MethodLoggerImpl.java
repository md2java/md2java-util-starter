package io.github.md2java.service;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

import io.github.md2java.anno.LogMethodInfo;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MethodLoggerImpl implements MethodLogger {
	
	@Override
	public Object logMethod(ProceedingJoinPoint pjp) throws Throwable {
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
