package com.itiancai.galaxy.dts

import com.itiancai.galaxy.dts.interceptor.TransactionAttribute
import com.twitter.util.Future


trait XAResource {

  def begin(xid: Xid, attribute: TransactionAttribute)

  def prepare(xid: Xid)

  def commit(xid: Xid): Future[Unit]

  def rollback(xid: Xid): Future[Unit]

}
