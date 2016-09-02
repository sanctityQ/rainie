package com.itiancai.galaxy.dts.config;

import com.itiancai.galaxy.dts.annotation.Activity;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.type.MethodMetadata;

import java.util.Map;
import java.util.Set;

public class TxComponentProvider extends ClassPathScanningCandidateComponentProvider {

    public TxComponentProvider(Environment environment) {
        super(true, environment);
    }


    @Override
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<BeanDefinition> candidates = super.findCandidateComponents(basePackage);
        for (BeanDefinition candidate : candidates) {
            if (candidate instanceof ScannedGenericBeanDefinition) {
                initActivityConfig(((ScannedGenericBeanDefinition) candidate).getMetadata().
                        getAnnotatedMethods(Activity.class.getName()));
            }
        }
        return candidates;
    }

    private void initActivityConfig(Set<MethodMetadata> methodMetadatas) {
        for (MethodMetadata methodMetadata : methodMetadatas) {

            Map<String, Object> attributes = methodMetadata.getAnnotationAttributes(Activity.class.getName());
            if (attributes == null) {
                return;
            }

//            String businessType = (String)attributes.get("businessType");
//
//            logger.info("businessType=");
//            attributes.get()
        }
    }

}
