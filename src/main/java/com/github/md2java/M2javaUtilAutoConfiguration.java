package com.github.md2java;

import javax.annotation.PostConstruct;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import lombok.extern.slf4j.Slf4j;

@Configuration
@ConditionalOnClass(value = Aspect.class)
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.github.md2java")
@Slf4j
public class M2javaUtilAutoConfiguration {

	@PostConstruct
	public void init() {
		log.info("m2java-util-starter intialized!!");
	}

}
