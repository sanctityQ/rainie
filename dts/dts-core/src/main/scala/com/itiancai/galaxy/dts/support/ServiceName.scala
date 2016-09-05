package com.itiancai.galaxy.dts.support

object ServiceName{

  def apply(name: String): ServiceName = {
    val array = name.split(":")
    if (array.length != 3) {
      throw new RuntimeException(s"resolve name:[${name}] error")
    }
    ServiceName(array(0), array(1), array(2))
  }

}

case class ServiceName(val systemName: String, val moduleName: String, val serviceName: String) {


  def canEqual(other: Any): Boolean = other.isInstanceOf[ServiceName]

  override def equals(other: Any): Boolean = other match {
    case that: ServiceName =>
      (that canEqual this) &&
        systemName == that.systemName &&
        moduleName == that.moduleName
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(systemName, moduleName)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString: String = {
    s"${systemName}:${moduleName}:${serviceName}"
  }

}
