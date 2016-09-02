package com.itiancai.galaxy.dts.support

import com.itiancai.galaxy.dts.recovery.RecoverServiceName
import org.apache.commons.lang.RandomStringUtils


object XidFactory {

  val noBranchQualifier: String = "DTS_NO_BRANCH_ID"

  def newXid(serviceName: RecoverServiceName): DtsXid = {
    new DtsXid(serviceName.toString + randomHex, noBranchQualifier)
  }

  def newBranch(dtsXid: DtsXid, serviceName: RecoverServiceName): DtsXid = {
    new DtsXid(dtsXid.getGlobalTransactionId, serviceName.toString + randomHex)
  }

  private[this] def randomHex: String = {
    val times: Long = System.currentTimeMillis
    val num: String = RandomStringUtils.randomNumeric(10)
    return s"${times}${num}"
  }


}
