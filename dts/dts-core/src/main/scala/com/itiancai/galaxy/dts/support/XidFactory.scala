package com.itiancai.galaxy.dts.support

import org.apache.commons.lang.RandomStringUtils


object XidFactory {

  val noBranchQualifier: String = "DTS_NO_BRANCH_ID"

  def newXid(serviceName: ServiceName): DtsXid = {
    new DtsXid(serviceName.toString + randomHex, noBranchQualifier)
  }

  def newBranch(dtsXid: DtsXid, serviceName: ServiceName): DtsXid = {
    new DtsXid(dtsXid.getGlobalTransactionId, serviceName.toString + randomHex)
  }

  private[this] def randomHex: String = {
    val times: Long = System.currentTimeMillis
    val num: String = RandomStringUtils.randomNumeric(10)
    return s"${times}${num}"
  }


}
