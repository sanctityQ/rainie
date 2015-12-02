package com.itiancai.galaxy.inject.tests

import com.itiancai.galaxy.inject._

class InternalAppIntegrationTest extends com.itiancai.galaxy.inject.Test {

  //  "start app" in {
  //    val app = new EmbeddedApp(
  //      new SampleGuiceApp {
  //        addFrameworkModule(FooModule)
  //
  //        override def appMain(): Unit = {
  //          super.appMain()
  //          assert(injector.instance[Foo].name == "bar")
  //        }
  //      },
  //      waitForWarmup = true,
  //      skipAppMain = true,
  //      verbose = false)
  //
  //    app.start()
  //    app.appMain()
  //
  //    Await.result(
  //      app.mainResult)
  //
  //    app.close()
  //  }
  //
  //  "call injector before main" in {
  //    val e = intercept[Exception] {
  //      new SampleGuiceApp {
  //        addFrameworkModules(FooModule)
  //        injector.instance[Foo]
  //      }
  //    }
  //    e.getMessage should startWith("injector is not available")
  //  }
  //
  //  "error in appMain" in {
  //    val app = new SampleGuiceApp {
  //      override def appMain(): Unit = {
  //        super.appMain()
  //        throw new scala.Exception("oops")
  //      }
  //    }
  //
  //    val e = intercept[Exception] {
  //      app.main()
  //    }
  //
  //    app.close()
  //    e.getMessage should startWith("oops")
  //  }

  "two apps starting" in {
    val a = new EmbeddedApp(new App {
      addAnnotationClass[Configure]
      //override protected def configureSpring = TestContextConfig
    })
    a.start()
    a.close()

//    val b = new EmbeddedApp(new App {
//      override protected def configureSpring = TestContextConfig
//    })
//    b.start()
//    b.close()
  }


}





object FooModule extends AbstractModule {
  override def configure(binder: Binder): Unit = {
    binder bind new Foo("bar")
  }
}

case class Foo(name: String)

object TestContextConfig extends ContextConfig{

    //override def registerClass (): Seq[Class[_]] = Seq (classOf[Configure] )
  addAnnotationClass[Configure]
}