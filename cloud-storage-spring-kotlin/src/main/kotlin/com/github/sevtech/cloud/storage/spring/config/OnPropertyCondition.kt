package com.github.sevtech.cloud.storage.spring.config

import org.springframework.context.annotation.ConditionContext
import org.springframework.context.annotation.ConfigurationCondition
import org.springframework.context.annotation.ConfigurationCondition.ConfigurationPhase
import org.springframework.core.type.AnnotatedTypeMetadata
import java.util.*

internal class OnPropertyCondition : ConfigurationCondition {
    override fun matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean {
        val attributes = Optional.ofNullable<Map<String, Any>?>(metadata.getAnnotationAttributes(ConditionalOnCloudStorageProperty::class.java.name))
                .orElse(HashMap())
        val propertyName = attributes!!["value"].toString()
        val propertyDesiredValue = attributes["on"] as Boolean
        val condition = context.environment.getProperty(propertyName, Boolean::class.java)
        return condition != null && condition == propertyDesiredValue
    }

    override fun getConfigurationPhase(): ConfigurationPhase {
        return ConfigurationPhase.REGISTER_BEAN
    }
}