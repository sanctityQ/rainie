package com.itiancai.galaxy.dts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.util.Pool;

/**
 * Created by bao on 15/11/17.
 */

@Configuration
//@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.itiancai.galaxy.dts")
//@PropertySource({"classpath:/config/${galaxias.env}/server.properties"})
@Import({DTSDatasource.class})
public class SpringBoot {

  @Value("${redis.sentinel}")
  private boolean redis_sentinel;
  @Value("${redis.masterName}")
  private String redis_masterName;
  @Value("${redis.maxIdle}")
  private int redis_maxIdle;
  @Value("${redis.maxTotal}")
  private int redis_maxTotal;
  @Value("${redis.maxWaitMillis}")
  private int redis_maxWaitMillis;
  @Value("${redis.hosts}")
  private String redis_hosts;

  @Bean
  public Pool<Jedis> jedisPool() {
    if(redis_sentinel) {
      JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
      jedisPoolConfig.setMaxIdle(redis_maxIdle);
      jedisPoolConfig.setMaxTotal(redis_maxTotal);
      jedisPoolConfig.setMaxWaitMillis(redis_maxWaitMillis);
      Set<String> sentinels = new HashSet();
      String[] hostConfigs = redis_hosts.split(",");

      for(int i = 0; i < hostConfigs.length; ++i) {
        String[] hostConfig = hostConfigs[i].split(":");
        sentinels.add((new HostAndPort(hostConfig[0], Integer.parseInt(hostConfig[1]))).toString());
      }
      return new JedisSentinelPool(redis_masterName, sentinels, jedisPoolConfig);
    } else {
      //初始化jedis
      JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
      jedisPoolConfig.setMaxIdle(redis_maxIdle);
      jedisPoolConfig.setMaxTotal(redis_maxTotal);
      jedisPoolConfig.setMaxWaitMillis(redis_maxWaitMillis);
      String[] hostConfig = redis_hosts.split(":");
      return new JedisPool(jedisPoolConfig, hostConfig[0], Integer.parseInt(hostConfig[1]));
    }
  }

}
