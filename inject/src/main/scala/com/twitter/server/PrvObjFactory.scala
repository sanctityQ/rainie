package com.twitter.server

import com.twitter.server.Lifecycle.PromoteToOldGen;

object PrvObjFactory {

  //val promoteToOldGen:PromoteToOldGen = get[PromoteToOldGen]

  def promoteToOldGen() = {
    new PromoteToOldGen()
  }

  def get[T]():T = {
    asInstanceOf[T]
  }
}
