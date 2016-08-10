package com.itiancai.galaxy.dts.utils;

import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.util.Pool;

/**
 * Created by pansen on 16/6/21.
 */
@Service
public class RedisService {

  @Autowired
  Pool<Jedis> jedisPool = null;

  Logger log = LoggerFactory.getLogger(RedisService.class);

  /**
   * key增加value值,原子性操作
   * @param key
   * @param value
   * @return
     */
  public Integer incrby(String key, int value){
    Jedis jedis = jedisPool.getResource();
    try{
      return jedis.incrBy(key, value).intValue();
    }catch (Throwable t){
      log.error("redisService-put", t);
      throw new RuntimeException("redisService-put-error");
    }finally {
      releaseJedis(jedis);
    }
  }

  /**
   * key减去value值,原子性操作
   * @param key
   * @param value
   * @return
     */
  public Integer decrby(String key, int value){
    Jedis jedis = jedisPool.getResource();
    try{
      return jedis.decrBy(key, value).intValue();
    }catch (Throwable t){
      log.error("redisService-put", t);
      throw new RuntimeException("redisService-put-error");
    }finally {
      releaseJedis(jedis);
    }
  }

  public void put(String key, String value) {
    //long startTime = System.currentTimeMillis();
    Jedis jedis = jedisPool.getResource();
    try {
      jedis.set(key, value);
    } catch (Throwable t) {
      log.error("redisService-put", t);
      throw new RuntimeException("redisService-put-error");
    } finally {
      releaseJedis(jedis);
    }
    //log.info(String.format("redis put time cost %1$dms", new Object[]{Long.valueOf(System.currentTimeMillis() - startTime)}));
  }

  public void putex(String key, int second, String value) {
    //long startTime = System.currentTimeMillis();
    Jedis jedis = jedisPool.getResource();
    Throwable var6 = null;

    try {
      jedis.setex(key, second, value);
    } catch (Throwable var15) {
      var6 = var15;
      throw var15;
    } finally {
      if (jedis != null) {
        if (var6 != null) {
          try {
            jedis.close();
          } catch (Throwable var14) {
            var6.addSuppressed(var14);
          }
        } else {
          jedis.close();
        }
      }

    }
  }

  public void putex(String key, int second, Object object) {
    this.putex(key, second, JSON.toJSONString(object));
  }

  public void put(String key, Object object) {
    this.put(key, JSON.toJSONString(object));
  }

  public void put(String key, byte[] value) {
    //long startTime = System.currentTimeMillis();
    Jedis jedis = jedisPool.getResource();

    try {
      jedis.set(key.getBytes(), value);
    } catch (Throwable t) {
      log.error("redisService-put", t);
      throw new RuntimeException("redisService-put-error");
    } finally {
      releaseJedis(jedis);
    }

    //log.info(String.format("redis put time cost %1$dms", new Object[]{Long.valueOf(System.currentTimeMillis() - startTime)}));
  }

  public void put(String key, Serializable value) {
    this.put(key, SerializationUtils.serialize(value));
  }

  public String getString(String key) {
    //long startTime = System.currentTimeMillis();
    Jedis jedis = jedisPool.getResource();
    String result = null;
    try {
      result = jedis.get(key);
    } catch (Throwable t) {
      log.error("redisService-getString", t);
      throw new RuntimeException("redisService-getString-error", t);
    } finally {
      releaseJedis(jedis);
    }

    //log.info(String.format("redis put time cost %1$dms", new Object[]{Long.valueOf(System.currentTimeMillis() - startTime)}));
    return result;
  }

  public byte[] getBytes(String key) {
    //long startTime = System.currentTimeMillis();
    Jedis jedis = jedisPool.getResource();
    byte[] binary = new byte[0];
    try {
      binary = jedis.get(key.getBytes());
    } catch (Throwable t) {
      log.error("redisService-getBytes", t);
      throw new RuntimeException("redisService-getBytes-error");
    } finally {
      releaseJedis(jedis);
    }

    //log.info(String.format("redis put time cost %1$dms", new Object[]{Long.valueOf(System.currentTimeMillis() - startTime)}));
    return binary;
  }

  public Object getSerializable(String key) {
    byte[] binary = this.getBytes(key);
    return binary != null && binary.length != 0 ? SerializationUtils.deserialize(binary) : null;
  }

  public <T> T get(String key, Class<T> classOfT) {
    String json = this.getString(key);
    return json == null ? null : JSON.parseObject(json, classOfT);
  }

  public <T> T get(String key, Type typeOfT) {
    String json = this.getString(key);
    return json == null ? null : (T) JSON.parseObject(json, typeOfT);
  }

  public boolean exist(String key) {
    //long startTime = System.currentTimeMillis();
    Jedis jedis = jedisPool.getResource();
    boolean result = false;
    try {
      result = jedis.exists(key).booleanValue();
    } catch (Throwable t) {
      log.error("redisService-exist", t);
      throw new RuntimeException("redisService-exist-error");
    } finally {
      releaseJedis(jedis);
    }

    //log.info(String.format("redis put time cost %1$dms", new Object[]{Long.valueOf(System.currentTimeMillis() - startTime)}));
    return result;
  }

  public long increment(String key) {
    Jedis jedis = jedisPool.getResource();
    long var4 = 0;
    try {
      var4 = jedis.incr(key).longValue();
    } catch (Throwable t) {
      log.error("redisService-increment", t);
      throw new RuntimeException("redisService-increment-error");
    } finally {
      releaseJedis(jedis);
    }
    return var4;
  }

  public long incrementBy(String key, long value) {
    Jedis jedis = jedisPool.getResource();
    long var6 = 0;
    try {
      var6 = jedis.incrBy(key, value).longValue();
    } catch (Throwable t) {
      log.error("redisService-incrementBy", t);
      throw new RuntimeException("redisService-incrementBy-error");
    } finally {
      releaseJedis(jedis);
    }

    return var6;
  }

  public long decrement(String key) {
    Jedis jedis = jedisPool.getResource();
    long var4 = 0;
    try {
      var4 = jedis.decr(key).longValue();
    } catch (Throwable t) {
      log.error("redisService-decrement", t);
      throw new RuntimeException("redisService-decrement-error");
    } finally {
      releaseJedis(jedis);
    }

    return var4;
  }

  public long decrementBy(String key, long value) {
    Jedis jedis = jedisPool.getResource();
    long var6 = 0;
    try {
      var6 = jedis.decrBy(key, value).longValue();
    } catch (Throwable t) {
      log.error("redisService-decrementBy", t);
      throw new RuntimeException("redisService-decrementBy-error");
    } finally {
      releaseJedis(jedis);
    }
    return var6;
  }

  public void subscribe(JedisPubSub listener, String... keys) {
    Jedis jedis = jedisPool.getResource();
    try {
      jedis.subscribe(listener, keys);
    } catch (Throwable t) {
      log.error("redisService-subscribe", t);
      throw new RuntimeException("redisService-subscribe-error");
    } finally {
      releaseJedis(jedis);
    }

  }

  public void publish(String channel, String message) {
    Jedis jedis = jedisPool.getResource();
    try {
      jedis.publish(channel, message);
    } catch (Throwable t) {
      log.error("redisService-publish", t);
      throw new RuntimeException("redisService-publish-error");
    } finally {
      releaseJedis(jedis);
    }

  }

  public void expire(String key, int second) {
    Jedis jedis = jedisPool.getResource();
    try {
      jedis.expire(key, second);
    } catch (Throwable t) {
      log.error("redisService-expire", t);
      throw new RuntimeException("redisService-expire-error");
    } finally {
      releaseJedis(jedis);
    }

  }

  public String getSet(String key, String value) {
    Jedis jedis = jedisPool.getResource();
    String var5 = null;
    try {
      var5 = jedis.getSet(key, value);
    } catch (Throwable t) {
      log.error("redisService-getSet", t);
      throw new RuntimeException("redisService-getSet-error");
    } finally {
      releaseJedis(jedis);
    }

    return var5;
  }

  public long rpush(String key, String... values) {
    Jedis jedis = jedisPool.getResource();

    long count = 0;
    try {
      count = jedis.rpush(key, values).longValue();
    } catch (Throwable t) {
      log.error("redisService-rpush", t);
      throw new RuntimeException("redisService-rpush-error");
    } finally {
      releaseJedis(jedis);
    }

    return count;
  }

  public String rpop(String key) {
    Jedis jedis = jedisPool.getResource();
    String reStr = null;
    try {
      reStr = jedis.rpop(key);
    } catch (Throwable t) {
      log.error("redis rpop error", t);
      throw new RuntimeException("redisService-rpop-error");
    } finally {
      try {
        jedis.close();
      } catch (Throwable t) {
        log.error("redis close link error", t);
      }
    }
    return reStr;
  }

  public long llen(String key) {
    Jedis jedis = jedisPool.getResource();
    long reInt = 0;
    try {
      reInt = jedis.llen(key);
    } catch (Throwable t) {
      log.error("redis llen error", t);
    } finally {
      try {
        jedis.close();
      } catch (Throwable t) {
        log.error("redis close link error", t);
      }
    }
    return reInt;
  }

  public long lpush(String key, String... values) {
    Jedis jedis = jedisPool.getResource();
    long count = 0;
    try {
      count = jedis.lpush(key, values).longValue();
    } catch (Throwable t) {
      log.error("redisService-lpush", t);
      throw new RuntimeException("redisService-lpush-error");
    } finally {
      releaseJedis(jedis);
    }
    return count;
  }


  public String lpop(String key) {
    Jedis jedis = jedisPool.getResource();
    String str = null;
    try {
      str = jedis.lpop(key);
    } catch (Throwable t) {
      log.error("redisService-lpop", t);
      throw new RuntimeException("redisService-lpop-error");
    } finally {
      releaseJedis(jedis);
    }
    return str;
  }

  public List<String> range(String key, long start, long end) {
    Jedis jedis = jedisPool.getResource();
    List list = null;
    try {
      list = jedis.lrange(key, start, end);
    } catch (Throwable t) {
      log.error("redisService-range", t);
      throw new RuntimeException("redisService-range-error");
    } finally {
      releaseJedis(jedis);
    }
    return list;
  }

  public long count(String key) {
    Jedis jedis = jedisPool.getResource();
    long var4 = 0;
    try {
      var4 = jedis.llen(key).longValue();
    } catch (Throwable t) {
      log.error("redisService-count", t);
      throw new RuntimeException("redisService-count-error");
    } finally {
      releaseJedis(jedis);
    }
    return var4;
  }

  public long remove(String key, long count, String value) {
    Jedis jedis = jedisPool.getResource();
    long var7 = 0;
    try {
      var7 = jedis.lrem(key, count, key).longValue();
    } catch (Throwable t) {
      log.error("redisService-remove", t);
      throw new RuntimeException("redisService-remove-error");
    } finally {
      releaseJedis(jedis);
    }
    return var7;
  }

  public long delete(String... keys) {
    Jedis jedis = jedisPool.getResource();
    long var4 = 0;
    try {
      var4 = jedis.del(keys).longValue();
    } catch (Throwable t) {
      log.error("redisService-delete", t);
      throw new RuntimeException("redisService-delete-error");
    } finally {
      releaseJedis(jedis);
    }

    return var4;
  }

  public long ttl(String key) {
    Jedis jedis = jedisPool.getResource();
    long var4 = 0;
    try {
      var4 = jedis.ttl(key).longValue();
    } catch (Throwable t) {
      log.error("redisService-ttl", t);
      throw new RuntimeException("redisService-ttl-error");
    } finally {
      releaseJedis(jedis);
    }

    return var4;
  }


  public boolean lock(String key, int ttl) {
    Jedis jedis = jedisPool.getResource();
    try {
      Long rs = jedis.setnx(key, "1");
      if (rs == 1) {
        jedis.expire(key, ttl);
        return true;
      }
    } catch (Throwable t) {
      log.error("redisService-lock", t);
      throw new RuntimeException("redisService-lock-error");
    } finally {
      releaseJedis(jedis);
    }
    return false;
  }


  public void release(String key) {
    Jedis jedis = jedisPool.getResource();
    try {
      jedis.del(key);
    } catch (Throwable t) {
      log.error("redisService-release", t);
      throw new RuntimeException("redisService-release-error");
    } finally {
      releaseJedis(jedis);
    }
  }

  private void releaseJedis(Jedis jedis) {
    if (jedis != null) {
      try {
        jedis.close();
      } catch (Throwable t) {
        log.error("releaseJedis-error", t);
      }
    }
  }
}
