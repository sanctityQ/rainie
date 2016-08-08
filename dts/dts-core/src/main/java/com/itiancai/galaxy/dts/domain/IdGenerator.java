package com.itiancai.galaxy.dts.domain;


import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {

    private static final String TX = "tx:";
    private static final String ACTION = "ac:";

    public String getTxId(String servceName){
        return TX+servceName+":"+randomHex();
    }
    public String getActionId(String servceName){
        return ACTION+servceName+":"+randomHex();
    }

    private  String randomHex(){
        long times = System.currentTimeMillis();
        String num = RandomStringUtils.randomNumeric(10);
        return Long.toHexString(times) + Long.toHexString(Long.parseLong(num));
    }
    public static void main(String []args){
        IdGenerator idGenerator = new IdGenerator();
        System.out.println(idGenerator.getTxId("p2p.lending.name"));
        System.out.println(idGenerator.getActionId("p2p.lending.name"));

    }
}
