package com.itiancai.galaxy.dts.utils

class CollectException(message: String, cause: Throwable) extends Exception(message, cause){
  def this() = this(null, null)
  def this(cause: Throwable) = this(null, cause)
  def this(message: String) = this(message, null)
  override def getStackTrace = if (cause != null) cause.getStackTrace else super.getStackTrace
}

class CollectOutException extends Exception {

}

class NameResolveException extends Exception

class SynchroException extends Exception
