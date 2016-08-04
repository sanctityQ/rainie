import scala.beans.BeanProperty
import scala.collection.immutable.HashMap

/**
  * Created by lsp on 16/8/8.
  */
case class TestBean(@BeanProperty name:String,
                    @BeanProperty m:HashMap[String,String],
                    @BeanProperty num:Seq[Int])
