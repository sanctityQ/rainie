package com.itiancai.galaxy.dts

import com.itiancai.galaxy.dts.support.DtsXid

trait TransactionStatus {

  def xId(): DtsXid

  def resouceXids(): Seq[DtsXid]
}
