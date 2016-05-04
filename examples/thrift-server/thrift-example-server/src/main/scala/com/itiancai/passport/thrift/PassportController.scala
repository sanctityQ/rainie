package com.itiancai.passport.thrift

import javax.annotation.PostConstruct
import javax.inject.{Singleton, Inject}

import com.itiancai.galaxy.thrift.Controller
import com.itiancai.passport.dao.ExecuteDao
import com.itiancai.passport.thrift.PassportService.{UserInfo, Reg, UserValidate}
import com.twitter.util.Future
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class PassportController @Inject()(excu: ExecuteDao)
  extends Controller
    with PassportService.BaseServiceIface{

  val log = LoggerFactory.getLogger(getClass)


  override val reg = handle(Reg){ args: Reg.Args =>

      val passportResult = PassportResult.apply("001", "",
        Option.apply(User.apply(args.request.mobile, "1111", "qicheng"))
      )
      log.info("--111---" + excu.msg2("a"))
      val s = args.request.loginName

      val passportUser = PassportUser(1, args.request.mobile, s.getOrElse("b"), Source.Web, SysCode.P2p, DateTime.now().toDate.getTime)
      log.info("--222---")
      Future.value(RegResponse.apply(passportUser, Option("111")))
  }

  override val userValidate = handle(UserValidate){ args: UserValidate.Args =>
      Future.value(true)
  }

  override val userInfo = handle(UserInfo){args: UserInfo.Args =>
     Future.value(true)
  }
}
