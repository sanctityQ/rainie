package com.itiancai.galaxy.dts

import com.itiancai.dts.client.SpringBoot

/**
  * Created by ChengQi on 8/8/16.
  */
object TestHttpServer extends TransactionHttpServer{
  addAnnotationClass[SpringBoot]
}
