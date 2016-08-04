package com.itiancai.galaxy.dts;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.hibernate3.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;


@ComponentScan
public class Datasource {

    private static AnnotationConfigApplicationContext context;

    protected static void init() {
        context = new AnnotationConfigApplicationContext();
        context.register(Datasource.class);
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
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setInitialSize(20);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(30);
        dataSource.setMaxWait(10000);
        dataSource.setPoolPreparedStatements(false);
        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(10000);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        //配置一个连接在池中最小生存的时间，单位是毫秒 -->
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestWhileIdle(true);
        return dataSource;
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
        factory.setPackagesToScan("com.itiancai.galaxy.dts.dao");
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
