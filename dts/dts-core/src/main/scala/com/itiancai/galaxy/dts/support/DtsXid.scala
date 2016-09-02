package com.itiancai.galaxy.dts.support

import com.itiancai.galaxy.dts.Xid


class DtsXid(globalTransactionId: String, branchId: String) extends Xid {

  override def getGlobalTransactionId: String = globalTransactionId


  override def getBranchId: String = branchId


  override def toString = s"DtsXid($getGlobalTransactionId, $getBranchId)"
}
