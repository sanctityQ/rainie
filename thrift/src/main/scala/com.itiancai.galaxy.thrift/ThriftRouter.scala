package com.itiancai.galaxy.thrift

import com.itiancai.galaxy.inject.{Logging, Injector}
import com.twitter.finagle.stats.StatsReceiver
import com.twitter.scrooge.{ToThriftService, ThriftService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class ThriftRouter @Autowired()(injector: Injector, statsReceiver: StatsReceiver) extends Logging{

  private[galaxy] var filterChain = ThriftFilter.Identity

  private[galaxy] var name: String = ""

  private var done = false

  private[galaxy] var filteredService: ThriftService = _


  /** Add global filter used for all requests */
  def filter[FilterType <: ThriftFilter : Manifest]: ThriftRouter = {
    filter(injector.instance[FilterType])
  }

  /** Add global filter used for all requests */
  def filter(clazz: Class[_ <: ThriftFilter]): ThriftRouter = {
    filter(injector.instance(clazz))
  }

  /** Add global filter used for all requests */
  def filter(filter: ThriftFilter): ThriftRouter = {
    assert(filteredService == null, "'filter' must be called before 'add'.")
    filterChain = filterChain andThen filter
    this
  }


  def add[C <: Controller with ToThriftService : Manifest]: ThriftRouter = {

    val controller = injector.instance[C]
//    controller.getClass.getMethods.foreach(method=>method.getName)
    for (m <- controller.methods) {
      m.setFilter(filterChain)
    }
    info("Adding methods\n" + (controller.methods.map(method => s"${controller.getClass.getSimpleName}.${method.name}") mkString "\n"))
    if (controller.methods.isEmpty) error(s"${controller.getClass.getCanonicalName} contains no methods!")
    filteredService = controller.toThriftService

    assert(!done, "ThriftRouter#add cannot be called multiple times, as we don't currently support serving multiple thrift services.")
    done = true
    this
  }

  private[galaxy] def serviceName(name: String) = {
    this.name = name
    this
  }

  private def addFilteredService[T <: ThriftService](thriftService: ThriftService): Unit = {
    assert(!done, "ThriftRouter#Add cannot be called multiple times, as we don't currently support serving multiple thrift services.")
    done = true
    filteredService = thriftService
  }


}
