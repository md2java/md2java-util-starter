package com.github.md2java.anno;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.github.md2java.M2javaUtilAutoConfiguration;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import(M2javaUtilAutoConfiguration.class)
public @interface EnableM2JavaUtil {

}
