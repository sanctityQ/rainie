namespace java com.itiancai.galaxy.dts.demo.javathrift
#@namespace scala com.itiancai.galaxy.dts.demo.thrift

service DTSDemoServerApi {
    string demoActivity(1:required string id)
}