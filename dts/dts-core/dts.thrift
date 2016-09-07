#@namespace scala com.itiancai.galaxy.dts.server

exception ServiceNotFindException {

}

service RecoveryService {

    /**
     * 通过sysName和moduleName获取servicePath
     * @param sysName
     * @param moduleName
     * @return
     */
    string servicePath(1:required string sysName, 2:required string moduleName) throws (1:ServiceNotFindException e)

}