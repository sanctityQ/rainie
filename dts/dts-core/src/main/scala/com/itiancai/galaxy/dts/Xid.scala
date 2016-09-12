package com.itiancai.galaxy.dts


trait Xid {

  def getGlobalTransactionId: String

  def getBranchId: String
}
