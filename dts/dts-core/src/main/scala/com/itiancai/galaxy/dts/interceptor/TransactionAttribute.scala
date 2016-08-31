package com.itiancai.galaxy.dts.interceptor

trait TransactionAttribute {

  def name(): String

  def paramValue(): String

}
