namespace java com.itiancai.galaxy.dts.javathrift
#@namespace scala com.itiancai.galaxy.dts.thrift

service DTSServerApi {

    /**
     * 通过sysName和moduleName获取servicePath
     * @param sysName
     * @param moduleName
     * @return
     */
    string servicePath(1:required string sysName, 2:required string moduleName)

}