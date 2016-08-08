import org.apache.commons.beanutils.BeanUtils

import scala.collection.JavaConversions.{mapAsScalaMap, _}
import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer

object TestBeanUtils {

//  case class Bean(@BeanProperty name:String,@BeanProperty testMap:Map[String,String])

  def main(args: Array[String]) {
    var map = HashMap[String, String]();
    map.+=("lsp" -> "sxj");
    val num =  ArrayBuffer(1,2)
    val bean:TestBean = new TestBean("lsp",map,num)
    mapAsScalaMap(map)
    val name:String = BeanUtils.getProperty(bean,"name")
//    val utils:PropertyUtilsBean = new PropertyUtilsBean();
//    val mapvalue= utils.describe(bean);
//    val

//    println(name)

    print(BeanUtils.getProperty(bean,"m"));
    print(BeanUtils.getIndexedProperty(bean,"num"));



//    println(Future.isInstanceOf[Bean])
  }

}
