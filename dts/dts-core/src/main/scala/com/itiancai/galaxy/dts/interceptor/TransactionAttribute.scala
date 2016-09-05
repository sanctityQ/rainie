package com.itiancai.galaxy.dts.interceptor

import com.itiancai.galaxy.dts.support.ServiceName

trait TransactionAttribute {

  def name(): ServiceName

  def paramValue(): String

  def timeOut(): Int

}
