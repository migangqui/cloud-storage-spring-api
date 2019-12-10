package com.github.sevtech.cloud.storage.spring.config;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnPropertyCondition.class)
@interface ConditionalOnCloudStorageProperty {
    String value();

    boolean on() default true;
}