namespace java com.itiancai.passport.thrift
#@namespace scala com.itiancai.passport.thrift

include "finatra-thrift/finatra_thrift_exceptions.thrift"

struct user{
    1: string mobile,
    2: string password,
    3: string loginName
}

/**
* 渠道来源
**/
enum Source {
    WEB = 0 //PC主站
    WEB_APP = 1 //H5
    APP = 2 //移动APP
    BACK =3 //系统后台|接口
}

/**
* 系统代码
**/
enum SysCode {
    P2P = 0 //P2P
    FINANCE = 1 //投顾
}


/**
 * 用户信息
 **/
struct PassportUser {
    1: required i64 id
    2: required string mobile //手机号
    3: required string loginName //登录名
    4: required Source source//注册渠道
    5: required SysCode syscode//系统代码
    6: required i64 registerDate//注册时间
    7: optional i64 lastLoginDate//最后登录时间
}

/**
* 注册返回数据
**/
struct RegResponse {
    1: required PassportUser user //passport用户信息
    2: optional string token //自动登录token
}




/**
* 用户注册
**/
struct RegRequest {
    1: required string mobile //手机号
    2: optional string password //密码
    3: optional string loginName //登录名
    4: optional bool autoLogin = true
}

service PassportService {

    //用户信息验证接口
    bool userValidate(1:string name,2:string value)

    //注册接口
    RegResponse reg(1:RegRequest request)

    bool userInfo()

}
