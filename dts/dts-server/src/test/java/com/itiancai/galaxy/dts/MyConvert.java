package com.itiancai.galaxy.dts;

import org.apache.commons.beanutils.Converter;

public class MyConvert implements Converter {
  @Override
  public Object convert(Class type, Object value) {
    if(value instanceof scala.collection.Map) {
      scala.collection.Map map = (scala.collection.Map) value;
      return scala.collection.JavaConversions.mapAsJavaMap(map);
    }
    return value;
  }
}