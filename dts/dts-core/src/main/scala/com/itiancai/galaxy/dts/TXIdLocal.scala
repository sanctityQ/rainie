package com.itiancai.galaxy.dts

import com.twitter.finagle.context.Contexts


private[dts] object TXIdLocal {
  private val tx_key = new Contexts.local.Key[String]
  def let_txId[R](ctx: String)(f: => R): R = Contexts.local.let(tx_key, ctx)(f)
  def current_txId(): String = Contexts.local.get(tx_key).get
  def clear_txId = Contexts.local.letClear(tx_key){}
}
