package com.github.sevtech.cloud.storage.spring.config

import org.springframework.context.annotation.Conditional

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Conditional(OnPropertyCondition::class)
internal annotation class ConditionalOnCloudStorageProperty(val value: String, val on: Boolean = true)