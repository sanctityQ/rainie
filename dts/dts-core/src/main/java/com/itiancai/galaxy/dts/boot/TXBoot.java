package com.itiancai.galaxy.dts.boot;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class TXBoot {

  @Value("${dts.db.driver}")
  private String driver;
  @Value("${dts.db.url}")
  private String url;
  @Value("${dts.db.userName}")
  private String userName;
  @Value("${dts.db.password}")
  private String password;

  @Bean(name="dtsDataSource")
  public DataSource dtsDataSource() {
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

  @Bean(name="dtsJdbcTemplate")
  public JdbcTemplate dtsJdbcTemplate() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dtsDataSource());
    return jdbcTemplate;
  }

  @Bean(name = "dtsTransactionManager")
  public PlatformTransactionManager dtsTransactionManager() {
    DataSourceTransactionManager txManager = new DataSourceTransactionManager();
    txManager.setDataSource(dtsDataSource());
    return txManager;
  }
}
