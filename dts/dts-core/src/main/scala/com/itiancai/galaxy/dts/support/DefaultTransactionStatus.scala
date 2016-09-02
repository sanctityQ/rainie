package com.itiancai.galaxy.dts.support

import com.itiancai.galaxy.dts.TransactionStatus

import scala.collection.mutable.ArrayBuffer

class DefaultTransactionStatus(xId_ : DtsXid) extends TransactionStatus {

  val resoueceXids: ArrayBuffer[DtsXid] = ArrayBuffer()

  override def resouceXids(): Seq[DtsXid] = resoueceXids

  override def xId(): DtsXid = xId_

  def addResourceXid(xid: DtsXid): Unit ={
    resoueceXids += xid
  }

}
