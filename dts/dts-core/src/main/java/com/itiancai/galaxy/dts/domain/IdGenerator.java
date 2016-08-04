package com.itiancai.galaxy.dts.domain;


import com.itiancai.galaxy.dts.DTSException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IdGenerator {
    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long twepoch = 1288834974657L; //Thu, 04 Nov 2010 01:42:54 GMT
    private long workerIdBits = 5L; //节点ID长度
    private long datacenterIdBits = 5L; //数据中心ID长度
    private long maxWorkerId = -1L ^ (-1L << workerIdBits); //最大支持机器节点数0~31，一共32个
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits); //最大支持数据中心节点数0~31，一共32个
    private long sequenceBits = 12L; //序列号12位
    private long workerIdShift = sequenceBits; //机器节点左移12位
    private long datacenterIdShift = sequenceBits + workerIdBits; //数据中心节点左移17位
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits; //时间毫秒数左移22位
    private long sequenceMask = -1L ^ (-1L << sequenceBits); //4095
    private long lastTimestamp = -1L;

    private Lock lock = new ReentrantLock();

    /**
     * 主事务id
     * @return
     */
    public static long genTXId(){
        return generatorId();
    }

    /**
     * 子事务id
     * @return
     */
    public static long genActionId(){
        return generatorId();
    }

    /**
     * id生成器,生成规则为:时间加+随机数(?位数)
     * @return
     */
    private static long generatorId(){
        return IdGenerator.get().nextId();
    }


    private static class IdGenHolder {
        private static final IdGenerator instance = new IdGenerator();
    }
    public static IdGenerator get(){
        return IdGenHolder.instance;
    }

    public IdGenerator() {
        this(0L, 0L);
    }

    public IdGenerator(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public long nextId() {
        lock.lock();
        //获取当前毫秒数
        long timestamp = timeGen();
        try {
            //如果服务器时间有问题(时钟后退)报错。
            if (timestamp < lastTimestamp) {
                throw new RuntimeException(String.format(
                        "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
            }
            //如果上次生成时间和当前时间相同,在同一毫秒内
            if (lastTimestamp == timestamp) {
                //sequence自增，因为sequence只有12bit，所以和sequenceMask相与一下，去掉高位
                sequence = (sequence + 1) & sequenceMask;
                //判断是否溢出,也就是每毫秒内超过4095，当为4096时，与sequenceMask相与，sequence就等于0
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp); //自旋等待到下一毫秒
                }
            } else {
                sequence = 0L; //如果和上次生成时间不同,重置sequence，就是下一毫秒开始，sequence计数重新从0开始累加
            }
            lastTimestamp = timestamp;
            return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift)
                    | (workerId << workerIdShift) | sequence;
        }catch (Throwable e){
            throw new DTSException(e.getMessage());
        }finally {
            lock.unlock();
        }

    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String []args){
        int num = 8;
        System.out.println(num >> 9);

    }
}
