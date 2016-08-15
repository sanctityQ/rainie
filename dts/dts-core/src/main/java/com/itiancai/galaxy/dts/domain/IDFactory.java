package com.itiancai.galaxy.dts.domain;


import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class IDFactory {

    private static final String TX = "tx:";
    private static final String ACTION = "ac:";

    public String generateTxId(String serviceName) {
        return TX + serviceName + ":" + randomHex();
    }

    public String getActionId(String serviceName) {
        return ACTION + serviceName + ":" + randomHex();
    }

    private String randomHex() {
        long times = System.currentTimeMillis();
        String num = RandomStringUtils.randomNumeric(10);
        return Long.toHexString(times) + Long.toHexString(Long.parseLong(num));
    }

    public static void main(String[] args) {
        IDFactory idGenerator = new IDFactory();
        System.out.println(idGenerator.generateTxId("p2p.lending.name"));
        System.out.println(idGenerator.getActionId("p2p.lending.name"));

    }
}
