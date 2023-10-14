package io.github.md2java.service;

import org.aspectj.lang.ProceedingJoinPoint;

public interface MethodLogger {

	Object logMethod(ProceedingJoinPoint pjp) throws Throwable;

}
