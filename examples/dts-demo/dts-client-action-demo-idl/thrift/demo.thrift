namespace java com.itiancai.galaxy.dts.action.demo.javathrift
#@namespace scala com.itiancai.galaxy.dts.action.demo.thrift

service DTSActionDemoServerApi {
    string demoAction(1:required string id)
}