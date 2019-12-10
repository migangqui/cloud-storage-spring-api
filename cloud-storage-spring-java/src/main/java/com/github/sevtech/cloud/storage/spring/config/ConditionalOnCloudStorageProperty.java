package com.github.sevtech.cloud.storage.spring.config;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnPropertyCondition.class)
@interface ConditionalOnCloudStorageProperty {

    String value();

    boolean on() default true;
}

class OnPropertyCondition implements ConfigurationCondition {

    @Override
    public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        final Map<String, Object> attributes = Optional.ofNullable(metadata.getAnnotationAttributes(ConditionalOnCloudStorageProperty.class.getName()))
                .orElse(new HashMap<>());
        final String propertyName = String.valueOf(attributes.get("value"));
        final boolean propertyDesiredValue = (boolean) attributes.get("on");
        Boolean condition = context.getEnvironment().getProperty(propertyName, Boolean.class);

        return condition != null && condition == propertyDesiredValue;
    }

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return ConfigurationPhase.REGISTER_BEAN;
    }
}