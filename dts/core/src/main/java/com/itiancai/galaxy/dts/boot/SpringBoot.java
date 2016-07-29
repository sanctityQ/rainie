package com.itiancai.galaxy.dts.boot;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.hibernate3.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Created by bao on 15/11/17.
 */

@Component
public class SpringBoot {

  private static AnnotationConfigApplicationContext context;

  protected static void init() {
    context = new AnnotationConfigApplicationContext();
    context.register(SpringBoot.class);
    context.refresh();
  }

  public static final AnnotationConfigApplicationContext context() {
    if (context == null) {
      init();
    }
    return context;
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Value("${dts.db.driver}")
  private String driver;
  @Value("${dts.db.url}")
  private String url;
  @Value("${dts.db.userName}")
  private String userName;
  @Value("${dts.db.password}")
  private String password;

  @Bean
  public DataSource dataSource() {
    BasicDataSource datasource = new BasicDataSource();
    datasource.setDriverClassName(driver);
    datasource.setUsername(userName);
    datasource.setPassword(password);
    datasource.setUrl(url);
    datasource.setInitialSize(20); // 初始的连接数；
    datasource.setMaxTotal(100);
    datasource.setMaxIdle(30);
    datasource.setMaxWaitMillis(10000);
    datasource.setMinIdle(1);
    return datasource;
  }

  @Value("${hibernate.sql.show}")
  private boolean show;
  @Value("${hibernate.sql.ddl}")
  private boolean ddl;

  @Bean
  public EntityManagerFactory entityManagerFactory() {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(ddl);
    vendorAdapter.setShowSql(show);

    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setJpaVendorAdapter(vendorAdapter);
    factory.setPackagesToScan("com.itiancai.dts.common.entities");
    factory.setDataSource(dataSource());
    factory.afterPropertiesSet();
    return factory.getObject();
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(entityManagerFactory());
    return txManager;
  }

  @Bean
  public HibernateExceptionTranslator hibernateExceptionTranslator(){
    return new HibernateExceptionTranslator();
  }
}
