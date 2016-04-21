/**
 * Generated by Scrooge
 *   version: 4.1.0
 *   rev: 87b84f89477a4737c8d57580a1e8bdaeac529b19
 *   built at: 20150928-114741
 */
package com.itiancai.passport.thrift

import com.twitter.scrooge.{
  LazyTProtocol,
  TFieldBlob, ThriftException, ThriftStruct, ThriftStructCodec3, ThriftStructFieldInfo,
  ThriftStructMetaData, ThriftUtil}
import org.apache.thrift.protocol._
import org.apache.thrift.transport.{TMemoryBuffer, TTransport}
import java.nio.ByteBuffer
import java.util.Arrays
import scala.collection.immutable.{Map => immutable$Map}
import scala.collection.mutable.Builder
import scala.collection.mutable.{
  ArrayBuffer => mutable$ArrayBuffer, Buffer => mutable$Buffer,
  HashMap => mutable$HashMap, HashSet => mutable$HashSet}
import scala.collection.{Map, Set}

/**
* 注册返回数据
**/
object RegResponse extends ThriftStructCodec3[RegResponse] {
  private val NoPassthroughFields = immutable$Map.empty[Short, TFieldBlob]
  val Struct = new TStruct("RegResponse")
  val UserField = new TField("user", TType.STRUCT, 1)
  val UserFieldManifest = implicitly[Manifest[com.itiancai.passport.thrift.PassportUser]]
  val TokenField = new TField("token", TType.STRING, 2)
  val TokenFieldManifest = implicitly[Manifest[String]]

  /**
   * Field information in declaration order.
   */
  lazy val fieldInfos: scala.List[ThriftStructFieldInfo] = scala.List[ThriftStructFieldInfo](
    new ThriftStructFieldInfo(
      UserField,
      false,
      true,
      UserFieldManifest,
      _root_.scala.None,
      _root_.scala.None,
      immutable$Map.empty[String, String],
      immutable$Map.empty[String, String]
    ),
    new ThriftStructFieldInfo(
      TokenField,
      true,
      false,
      TokenFieldManifest,
      _root_.scala.None,
      _root_.scala.None,
      immutable$Map.empty[String, String],
      immutable$Map.empty[String, String]
    )
  )

  lazy val structAnnotations: immutable$Map[String, String] =
    immutable$Map.empty[String, String]

  /**
   * Checks that all required fields are non-null.
   */
  def validate(_item: RegResponse): Unit = {
    if (_item.user == null) throw new TProtocolException("Required field user cannot be null")
  }

  def withoutPassthroughFields(original: RegResponse): RegResponse =
    new Immutable(
      user =
        {
          val field = original.user
          com.itiancai.passport.thrift.PassportUser.withoutPassthroughFields(field)
        },
      token =
        {
          val field = original.token
          field.map { field =>
            field
          }
        }
    )

  override def encode(_item: RegResponse, _oproto: TProtocol): Unit = {
    _item.write(_oproto)
  }

  private[this] def lazyDecode(_iprot: LazyTProtocol): RegResponse = {

    var user: com.itiancai.passport.thrift.PassportUser = null
    var _got_user = false
    var tokenOffset: Int = -1

    var _passthroughFields: Builder[(Short, TFieldBlob), immutable$Map[Short, TFieldBlob]] = null
    var _done = false
    val _start_offset = _iprot.offset

    _iprot.readStructBegin()
    while (!_done) {
      val _field = _iprot.readFieldBegin()
      if (_field.`type` == TType.STOP) {
        _done = true
      } else {
        _field.id match {
          case 1 =>
            _field.`type` match {
              case TType.STRUCT =>
    
                user = readUserValue(_iprot)
                _got_user = true
              case _actualType =>
                val _expectedType = TType.STRUCT
                throw new TProtocolException(
                  "Received wrong type for field 'user' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 2 =>
            _field.`type` match {
              case TType.STRING =>
                tokenOffset = _iprot.offsetSkipString
    
              case _actualType =>
                val _expectedType = TType.STRING
                throw new TProtocolException(
                  "Received wrong type for field 'token' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case _ =>
            if (_passthroughFields == null)
              _passthroughFields = immutable$Map.newBuilder[Short, TFieldBlob]
            _passthroughFields += (_field.id -> TFieldBlob.read(_field, _iprot))
        }
        _iprot.readFieldEnd()
      }
    }
    _iprot.readStructEnd()

    if (!_got_user) throw new TProtocolException("Required field 'user' was not found in serialized data for struct RegResponse")
    new LazyImmutable(
      _iprot,
      _iprot.buffer,
      _start_offset,
      _iprot.offset,
      user,
      tokenOffset,
      if (_passthroughFields == null)
        NoPassthroughFields
      else
        _passthroughFields.result()
    )
  }

  override def decode(_iprot: TProtocol): RegResponse =
    _iprot match {
      case i: LazyTProtocol => lazyDecode(i)
      case i => eagerDecode(i)
    }

  private[this] def eagerDecode(_iprot: TProtocol): RegResponse = {
    var user: com.itiancai.passport.thrift.PassportUser = null
    var _got_user = false
    var token: _root_.scala.Option[String] = _root_.scala.None
    var _passthroughFields: Builder[(Short, TFieldBlob), immutable$Map[Short, TFieldBlob]] = null
    var _done = false

    _iprot.readStructBegin()
    while (!_done) {
      val _field = _iprot.readFieldBegin()
      if (_field.`type` == TType.STOP) {
        _done = true
      } else {
        _field.id match {
          case 1 =>
            _field.`type` match {
              case TType.STRUCT =>
                user = readUserValue(_iprot)
                _got_user = true
              case _actualType =>
                val _expectedType = TType.STRUCT
                throw new TProtocolException(
                  "Received wrong type for field 'user' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 2 =>
            _field.`type` match {
              case TType.STRING =>
                token = _root_.scala.Some(readTokenValue(_iprot))
              case _actualType =>
                val _expectedType = TType.STRING
                throw new TProtocolException(
                  "Received wrong type for field 'token' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case _ =>
            if (_passthroughFields == null)
              _passthroughFields = immutable$Map.newBuilder[Short, TFieldBlob]
            _passthroughFields += (_field.id -> TFieldBlob.read(_field, _iprot))
        }
        _iprot.readFieldEnd()
      }
    }
    _iprot.readStructEnd()

    if (!_got_user) throw new TProtocolException("Required field 'user' was not found in serialized data for struct RegResponse")
    new Immutable(
      user,
      token,
      if (_passthroughFields == null)
        NoPassthroughFields
      else
        _passthroughFields.result()
    )
  }

  def apply(
    user: com.itiancai.passport.thrift.PassportUser,
    token: _root_.scala.Option[String] = _root_.scala.None
  ): RegResponse =
    new Immutable(
      user,
      token
    )

  def unapply(_item: RegResponse): _root_.scala.Option[scala.Product2[com.itiancai.passport.thrift.PassportUser, Option[String]]] = _root_.scala.Some(_item)


  @inline private def readUserValue(_iprot: TProtocol): com.itiancai.passport.thrift.PassportUser = {
    com.itiancai.passport.thrift.PassportUser.decode(_iprot)
  }

  @inline private def writeUserField(user_item: com.itiancai.passport.thrift.PassportUser, _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(UserField)
    writeUserValue(user_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeUserValue(user_item: com.itiancai.passport.thrift.PassportUser, _oprot: TProtocol): Unit = {
    user_item.write(_oprot)
  }

  @inline private def readTokenValue(_iprot: TProtocol): String = {
    _iprot.readString()
  }

  @inline private def writeTokenField(token_item: String, _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(TokenField)
    writeTokenValue(token_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeTokenValue(token_item: String, _oprot: TProtocol): Unit = {
    _oprot.writeString(token_item)
  }


  object Immutable extends ThriftStructCodec3[RegResponse] {
    override def encode(_item: RegResponse, _oproto: TProtocol): Unit = { _item.write(_oproto) }
    override def decode(_iprot: TProtocol): RegResponse = RegResponse.decode(_iprot)
    override lazy val metaData: ThriftStructMetaData[RegResponse] = RegResponse.metaData
  }

  /**
   * The default read-only implementation of RegResponse.  You typically should not need to
   * directly reference this class; instead, use the RegResponse.apply method to construct
   * new instances.
   */
  class Immutable(
      val user: com.itiancai.passport.thrift.PassportUser,
      val token: _root_.scala.Option[String],
      override val _passthroughFields: immutable$Map[Short, TFieldBlob])
    extends RegResponse {
    def this(
      user: com.itiancai.passport.thrift.PassportUser,
      token: _root_.scala.Option[String] = _root_.scala.None
    ) = this(
      user,
      token,
      Map.empty
    )
  }

  /**
   * This is another Immutable, this however keeps strings as lazy values that are lazily decoded from the backing
   * array byte on read.
   */
  private[this] class LazyImmutable(
      _proto: LazyTProtocol,
      _buf: Array[Byte],
      _start_offset: Int,
      _end_offset: Int,
      val user: com.itiancai.passport.thrift.PassportUser,
      tokenOffset: Int,
      override val _passthroughFields: immutable$Map[Short, TFieldBlob])
    extends RegResponse {

    override def write(_oprot: TProtocol): Unit = {
      _oprot match {
        case i: LazyTProtocol => i.writeRaw(_buf, _start_offset, _end_offset - _start_offset)
        case _ => super.write(_oprot)
      }
    }

    lazy val token: _root_.scala.Option[String] =
      if (tokenOffset == -1)
        None
      else {
        Some(_proto.decodeString(_buf, tokenOffset))
      }

    /**
     * Override the super hash code to make it a lazy val rather than def.
     *
     * Calculating the hash code can be expensive, caching it where possible
     * can provide signifigant performance wins. (Key in a hash map for instance)
     * Usually not safe since the normal constructor will accept a mutable map or
     * set as an arg
     * Here however we control how the class is generated from serialized data.
     * With the class private and the contract that we throw away our mutable references
     * having the hash code lazy here is safe.
     */
    override lazy val hashCode = super.hashCode
  }

  /**
   * This Proxy trait allows you to extend the RegResponse trait with additional state or
   * behavior and implement the read-only methods from RegResponse using an underlying
   * instance.
   */
  trait Proxy extends RegResponse {
    protected def _underlying_RegResponse: RegResponse
    override def user: com.itiancai.passport.thrift.PassportUser = _underlying_RegResponse.user
    override def token: _root_.scala.Option[String] = _underlying_RegResponse.token
    override def _passthroughFields = _underlying_RegResponse._passthroughFields
  }
}

trait RegResponse
  extends ThriftStruct
  with scala.Product2[com.itiancai.passport.thrift.PassportUser, Option[String]]
  with java.io.Serializable
{
  import RegResponse._

  def user: com.itiancai.passport.thrift.PassportUser
  def token: _root_.scala.Option[String]

  def _passthroughFields: immutable$Map[Short, TFieldBlob] = immutable$Map.empty

  def _1 = user
  def _2 = token


  /**
   * Gets a field value encoded as a binary blob using TCompactProtocol.  If the specified field
   * is present in the passthrough map, that value is returned.  Otherwise, if the specified field
   * is known and not optional and set to None, then the field is serialized and returned.
   */
  def getFieldBlob(_fieldId: Short): _root_.scala.Option[TFieldBlob] = {
    lazy val _buff = new TMemoryBuffer(32)
    lazy val _oprot = new TCompactProtocol(_buff)
    _passthroughFields.get(_fieldId) match {
      case blob: _root_.scala.Some[TFieldBlob] => blob
      case _root_.scala.None => {
        val _fieldOpt: _root_.scala.Option[TField] =
          _fieldId match {
            case 1 =>
              if (user ne null) {
                writeUserValue(user, _oprot)
                _root_.scala.Some(RegResponse.UserField)
              } else {
                _root_.scala.None
              }
            case 2 =>
              if (token.isDefined) {
                writeTokenValue(token.get, _oprot)
                _root_.scala.Some(RegResponse.TokenField)
              } else {
                _root_.scala.None
              }
            case _ => _root_.scala.None
          }
        _fieldOpt match {
          case _root_.scala.Some(_field) =>
            val _data = Arrays.copyOfRange(_buff.getArray, 0, _buff.length)
            _root_.scala.Some(TFieldBlob(_field, _data))
          case _root_.scala.None =>
            _root_.scala.None
        }
      }
    }
  }

  /**
   * Collects TCompactProtocol-encoded field values according to `getFieldBlob` into a map.
   */
  def getFieldBlobs(ids: TraversableOnce[Short]): immutable$Map[Short, TFieldBlob] =
    (ids flatMap { id => getFieldBlob(id) map { id -> _ } }).toMap

  /**
   * Sets a field using a TCompactProtocol-encoded binary blob.  If the field is a known
   * field, the blob is decoded and the field is set to the decoded value.  If the field
   * is unknown and passthrough fields are enabled, then the blob will be stored in
   * _passthroughFields.
   */
  def setField(_blob: TFieldBlob): RegResponse = {
    var user: com.itiancai.passport.thrift.PassportUser = this.user
    var token: _root_.scala.Option[String] = this.token
    var _passthroughFields = this._passthroughFields
    _blob.id match {
      case 1 =>
        user = readUserValue(_blob.read)
      case 2 =>
        token = _root_.scala.Some(readTokenValue(_blob.read))
      case _ => _passthroughFields += (_blob.id -> _blob)
    }
    new Immutable(
      user,
      token,
      _passthroughFields
    )
  }

  /**
   * If the specified field is optional, it is set to None.  Otherwise, if the field is
   * known, it is reverted to its default value; if the field is unknown, it is removed
   * from the passthroughFields map, if present.
   */
  def unsetField(_fieldId: Short): RegResponse = {
    var user: com.itiancai.passport.thrift.PassportUser = this.user
    var token: _root_.scala.Option[String] = this.token

    _fieldId match {
      case 1 =>
        user = null
      case 2 =>
        token = _root_.scala.None
      case _ =>
    }
    new Immutable(
      user,
      token,
      _passthroughFields - _fieldId
    )
  }

  /**
   * If the specified field is optional, it is set to None.  Otherwise, if the field is
   * known, it is reverted to its default value; if the field is unknown, it is removed
   * from the passthroughFields map, if present.
   */
  def unsetUser: RegResponse = unsetField(1)

  def unsetToken: RegResponse = unsetField(2)


  override def write(_oprot: TProtocol): Unit = {
    RegResponse.validate(this)
    _oprot.writeStructBegin(Struct)
    if (user ne null) writeUserField(user, _oprot)
    if (token.isDefined) writeTokenField(token.get, _oprot)
    if (_passthroughFields.nonEmpty) {
      _passthroughFields.values.foreach { _.write(_oprot) }
    }
    _oprot.writeFieldStop()
    _oprot.writeStructEnd()
  }

  def copy(
    user: com.itiancai.passport.thrift.PassportUser = this.user,
    token: _root_.scala.Option[String] = this.token,
    _passthroughFields: immutable$Map[Short, TFieldBlob] = this._passthroughFields
  ): RegResponse =
    new Immutable(
      user,
      token,
      _passthroughFields
    )

  override def canEqual(other: Any): Boolean = other.isInstanceOf[RegResponse]

  override def equals(other: Any): Boolean =
    canEqual(other) &&
      _root_.scala.runtime.ScalaRunTime._equals(this, other) &&
      _passthroughFields == other.asInstanceOf[RegResponse]._passthroughFields

  override def hashCode: Int = _root_.scala.runtime.ScalaRunTime._hashCode(this)

  override def toString: String = _root_.scala.runtime.ScalaRunTime._toString(this)


  override def productArity: Int = 2

  override def productElement(n: Int): Any = n match {
    case 0 => this.user
    case 1 => this.token
    case _ => throw new IndexOutOfBoundsException(n.toString)
  }

  override def productPrefix: String = "RegResponse"
}