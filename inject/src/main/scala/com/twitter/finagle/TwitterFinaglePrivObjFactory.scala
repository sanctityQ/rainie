package com.twitter.finagle

import com.twitter.finagle.client.ClientRegistry

object TwitterFinaglePrivObjFactory {
  def clientRegistry() = {ClientRegistry}

  def finagleInit() = {Init}
}
