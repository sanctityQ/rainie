package com.itiancai.dts.client

import com.itiancai.galaxy.dts.TransactionHttpServer


object TestHttpServer extends TransactionHttpServer{
  addAnnotationClass[SpringBoot]
}
