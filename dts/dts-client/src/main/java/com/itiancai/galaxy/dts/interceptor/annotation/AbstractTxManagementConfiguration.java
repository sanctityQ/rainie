package com.itiancai.galaxy.dts.interceptor.annotation;


import com.itiancai.galaxy.dts.interceptor.config.TxComponentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Configuration
public class AbstractTxManagementConfiguration implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, EnvironmentAware {



    private Logger logger  = LoggerFactory.getLogger(AbstractTxManagementConfiguration.class);

    private static final String BASE_PACKAGES = "basePackages";

    protected AnnotationAttributes enableTx;

    private Environment environment;

    private ResourceLoader resourceLoader;

    private AnnotationMetadata metadata;


    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Assert.notNull(annotationMetadata, "AnnotationMetadata must not be null!");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");

        this.metadata = annotationMetadata;

        this.enableTx = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(getAnnotation().getName(), false));

        if (this.enableTx == null) {
            throw new IllegalArgumentException(
                    "@EnableTxManagement is not present on importing class " + annotationMetadata.getClassName());
        }

        TxComponentProvider txComponentProvider = new TxComponentProvider(environment);

        for(String packageName : getBasePackages()){
            txComponentProvider.findCandidateComponents(packageName);
        }


        //register all bean

        logger.info("111111111111111111111 ");

    }


//    @Bean
//    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//    public BeanFactoryTransactionAttributeSourceAdvisor transactionAdvisor(){
//        BeanFactoryTransactionAttributeSourceAdvisor transactionAdvisor = new BeanFactoryTransactionAttributeSourceAdvisor();
//        transactionAdvisor.setAdvice();
//    }
//
//    @Bean
//    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//    public TransactionAttributeSource transactionAttributeSource() {
//        return new AnnotationTransactionAttributeSource();
//    }
//
//    @Bean
//    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//    public TransactionInterceptor transactionInterceptor() {
//        TransactionInterceptor interceptor = new TransactionInterceptor(transactionAttributeSource());
//        if (this.txManager != null) {
//            interceptor.setTransactionManager(this.txManager);
//        }
//        return interceptor;
//    }

    private Iterable<String> getBasePackages() {

        String[] value = enableTx.getStringArray("value");
        String[] basePackages = enableTx.getStringArray(BASE_PACKAGES);

        // Default configuration - return package of annotated class
        if (value.length == 0 && basePackages.length == 0) {
            String className = metadata.getClassName();
            return Collections.singleton(ClassUtils.getPackageName(className));
        }

        Set<String> packages = new HashSet<String>();
        packages.addAll(Arrays.asList(value));
        packages.addAll(Arrays.asList(basePackages));

        return packages;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    protected Class<? extends Annotation> getAnnotation() {
        return EnableTxManagement.class;
    }
}
