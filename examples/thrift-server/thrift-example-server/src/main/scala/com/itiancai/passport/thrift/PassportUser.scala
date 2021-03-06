/**
 * Generated by Scrooge
 *   version: 4.5.0
 *   rev: 014664de600267b36809bbc85225e26aec286216
 *   built at: 20160203-205352
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
* 用户信息
**/
object PassportUser extends ThriftStructCodec3[PassportUser] {
  private val NoPassthroughFields = immutable$Map.empty[Short, TFieldBlob]
  val Struct = new TStruct("PassportUser")
  val IdField = new TField("id", TType.I64, 1)
  val IdFieldManifest = implicitly[Manifest[Long]]
  val MobileField = new TField("mobile", TType.STRING, 2)
  val MobileFieldManifest = implicitly[Manifest[String]]
  val LoginNameField = new TField("loginName", TType.STRING, 3)
  val LoginNameFieldManifest = implicitly[Manifest[String]]
  val SourceField = new TField("source", TType.ENUM, 4)
  val SourceFieldI32 = new TField("source", TType.I32, 4)
  val SourceFieldManifest = implicitly[Manifest[com.itiancai.passport.thrift.Source]]
  val SyscodeField = new TField("syscode", TType.ENUM, 5)
  val SyscodeFieldI32 = new TField("syscode", TType.I32, 5)
  val SyscodeFieldManifest = implicitly[Manifest[com.itiancai.passport.thrift.SysCode]]
  val RegisterDateField = new TField("registerDate", TType.I64, 6)
  val RegisterDateFieldManifest = implicitly[Manifest[Long]]
  val LastLoginDateField = new TField("lastLoginDate", TType.I64, 7)
  val LastLoginDateFieldManifest = implicitly[Manifest[Long]]

  /**
   * Field information in declaration order.
   */
  lazy val fieldInfos: scala.List[ThriftStructFieldInfo] = scala.List[ThriftStructFieldInfo](
    new ThriftStructFieldInfo(
      IdField,
      false,
      true,
      IdFieldManifest,
      _root_.scala.None,
      _root_.scala.None,
      immutable$Map.empty[String, String],
      immutable$Map.empty[String, String]
    ),
    new ThriftStructFieldInfo(
      MobileField,
      false,
      true,
      MobileFieldManifest,
      _root_.scala.None,
      _root_.scala.None,
      immutable$Map.empty[String, String],
      immutable$Map.empty[String, String]
    ),
    new ThriftStructFieldInfo(
      LoginNameField,
      false,
      true,
      LoginNameFieldManifest,
      _root_.scala.None,
      _root_.scala.None,
      immutable$Map.empty[String, String],
      immutable$Map.empty[String, String]
    ),
    new ThriftStructFieldInfo(
      SourceField,
      false,
      true,
      SourceFieldManifest,
      _root_.scala.None,
      _root_.scala.None,
      immutable$Map.empty[String, String],
      immutable$Map.empty[String, String]
    ),
    new ThriftStructFieldInfo(
      SyscodeField,
      false,
      true,
      SyscodeFieldManifest,
      _root_.scala.None,
      _root_.scala.None,
      immutable$Map.empty[String, String],
      immutable$Map.empty[String, String]
    ),
    new ThriftStructFieldInfo(
      RegisterDateField,
      false,
      true,
      RegisterDateFieldManifest,
      _root_.scala.None,
      _root_.scala.None,
      immutable$Map.empty[String, String],
      immutable$Map.empty[String, String]
    ),
    new ThriftStructFieldInfo(
      LastLoginDateField,
      true,
      false,
      LastLoginDateFieldManifest,
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
  def validate(_item: PassportUser): Unit = {
    if (_item.mobile == null) throw new TProtocolException("Required field mobile cannot be null")
    if (_item.loginName == null) throw new TProtocolException("Required field loginName cannot be null")
    if (_item.source == null) throw new TProtocolException("Required field source cannot be null")
    if (_item.syscode == null) throw new TProtocolException("Required field syscode cannot be null")
  }

  def withoutPassthroughFields(original: PassportUser): PassportUser =
    new Immutable(
      id =
        {
          val field = original.id
          field
        },
      mobile =
        {
          val field = original.mobile
          field
        },
      loginName =
        {
          val field = original.loginName
          field
        },
      source =
        {
          val field = original.source
          field
        },
      syscode =
        {
          val field = original.syscode
          field
        },
      registerDate =
        {
          val field = original.registerDate
          field
        },
      lastLoginDate =
        {
          val field = original.lastLoginDate
          field.map { field =>
            field
          }
        }
    )

  override def encode(_item: PassportUser, _oproto: TProtocol): Unit = {
    _item.write(_oproto)
  }

  private[this] def lazyDecode(_iprot: LazyTProtocol): PassportUser = {

    var id: Long = 0L
    var _got_id = false
    var mobileOffset: Int = -1
    var _got_mobile = false
    var loginNameOffset: Int = -1
    var _got_loginName = false
    var source: com.itiancai.passport.thrift.Source = null
    var _got_source = false
    var syscode: com.itiancai.passport.thrift.SysCode = null
    var _got_syscode = false
    var registerDate: Long = 0L
    var _got_registerDate = false
    var lastLoginDateOffset: Int = -1

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
              case TType.I64 =>
    
                id = readIdValue(_iprot)
                _got_id = true
              case _actualType =>
                val _expectedType = TType.I64
                throw new TProtocolException(
                  "Received wrong type for field 'id' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 2 =>
            _field.`type` match {
              case TType.STRING =>
                mobileOffset = _iprot.offsetSkipString
    
                _got_mobile = true
              case _actualType =>
                val _expectedType = TType.STRING
                throw new TProtocolException(
                  "Received wrong type for field 'mobile' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 3 =>
            _field.`type` match {
              case TType.STRING =>
                loginNameOffset = _iprot.offsetSkipString
    
                _got_loginName = true
              case _actualType =>
                val _expectedType = TType.STRING
                throw new TProtocolException(
                  "Received wrong type for field 'loginName' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 4 =>
            _field.`type` match {
              case TType.I32 | TType.ENUM =>
    
                source = readSourceValue(_iprot)
                _got_source = true
              case _actualType =>
                val _expectedType = TType.ENUM
                throw new TProtocolException(
                  "Received wrong type for field 'source' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 5 =>
            _field.`type` match {
              case TType.I32 | TType.ENUM =>
    
                syscode = readSyscodeValue(_iprot)
                _got_syscode = true
              case _actualType =>
                val _expectedType = TType.ENUM
                throw new TProtocolException(
                  "Received wrong type for field 'syscode' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 6 =>
            _field.`type` match {
              case TType.I64 =>
    
                registerDate = readRegisterDateValue(_iprot)
                _got_registerDate = true
              case _actualType =>
                val _expectedType = TType.I64
                throw new TProtocolException(
                  "Received wrong type for field 'registerDate' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 7 =>
            _field.`type` match {
              case TType.I64 =>
                lastLoginDateOffset = _iprot.offsetSkipI64
    
              case _actualType =>
                val _expectedType = TType.I64
                throw new TProtocolException(
                  "Received wrong type for field 'lastLoginDate' (expected=%s, actual=%s).".format(
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

    if (!_got_id) throw new TProtocolException("Required field 'id' was not found in serialized data for struct PassportUser")
    if (!_got_mobile) throw new TProtocolException("Required field 'mobile' was not found in serialized data for struct PassportUser")
    if (!_got_loginName) throw new TProtocolException("Required field 'loginName' was not found in serialized data for struct PassportUser")
    if (!_got_source) throw new TProtocolException("Required field 'source' was not found in serialized data for struct PassportUser")
    if (!_got_syscode) throw new TProtocolException("Required field 'syscode' was not found in serialized data for struct PassportUser")
    if (!_got_registerDate) throw new TProtocolException("Required field 'registerDate' was not found in serialized data for struct PassportUser")
    new LazyImmutable(
      _iprot,
      _iprot.buffer,
      _start_offset,
      _iprot.offset,
      id,
      mobileOffset,
      loginNameOffset,
      source,
      syscode,
      registerDate,
      lastLoginDateOffset,
      if (_passthroughFields == null)
        NoPassthroughFields
      else
        _passthroughFields.result()
    )
  }

  override def decode(_iprot: TProtocol): PassportUser =
    _iprot match {
      case i: LazyTProtocol => lazyDecode(i)
      case i => eagerDecode(i)
    }

  private[this] def eagerDecode(_iprot: TProtocol): PassportUser = {
    var id: Long = 0L
    var _got_id = false
    var mobile: String = null
    var _got_mobile = false
    var loginName: String = null
    var _got_loginName = false
    var source: com.itiancai.passport.thrift.Source = null
    var _got_source = false
    var syscode: com.itiancai.passport.thrift.SysCode = null
    var _got_syscode = false
    var registerDate: Long = 0L
    var _got_registerDate = false
    var lastLoginDate: _root_.scala.Option[Long] = _root_.scala.None
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
              case TType.I64 =>
                id = readIdValue(_iprot)
                _got_id = true
              case _actualType =>
                val _expectedType = TType.I64
                throw new TProtocolException(
                  "Received wrong type for field 'id' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 2 =>
            _field.`type` match {
              case TType.STRING =>
                mobile = readMobileValue(_iprot)
                _got_mobile = true
              case _actualType =>
                val _expectedType = TType.STRING
                throw new TProtocolException(
                  "Received wrong type for field 'mobile' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 3 =>
            _field.`type` match {
              case TType.STRING =>
                loginName = readLoginNameValue(_iprot)
                _got_loginName = true
              case _actualType =>
                val _expectedType = TType.STRING
                throw new TProtocolException(
                  "Received wrong type for field 'loginName' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 4 =>
            _field.`type` match {
              case TType.I32 | TType.ENUM =>
                source = readSourceValue(_iprot)
                _got_source = true
              case _actualType =>
                val _expectedType = TType.ENUM
                throw new TProtocolException(
                  "Received wrong type for field 'source' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 5 =>
            _field.`type` match {
              case TType.I32 | TType.ENUM =>
                syscode = readSyscodeValue(_iprot)
                _got_syscode = true
              case _actualType =>
                val _expectedType = TType.ENUM
                throw new TProtocolException(
                  "Received wrong type for field 'syscode' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 6 =>
            _field.`type` match {
              case TType.I64 =>
                registerDate = readRegisterDateValue(_iprot)
                _got_registerDate = true
              case _actualType =>
                val _expectedType = TType.I64
                throw new TProtocolException(
                  "Received wrong type for field 'registerDate' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 7 =>
            _field.`type` match {
              case TType.I64 =>
                lastLoginDate = _root_.scala.Some(readLastLoginDateValue(_iprot))
              case _actualType =>
                val _expectedType = TType.I64
                throw new TProtocolException(
                  "Received wrong type for field 'lastLoginDate' (expected=%s, actual=%s).".format(
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

    if (!_got_id) throw new TProtocolException("Required field 'id' was not found in serialized data for struct PassportUser")
    if (!_got_mobile) throw new TProtocolException("Required field 'mobile' was not found in serialized data for struct PassportUser")
    if (!_got_loginName) throw new TProtocolException("Required field 'loginName' was not found in serialized data for struct PassportUser")
    if (!_got_source) throw new TProtocolException("Required field 'source' was not found in serialized data for struct PassportUser")
    if (!_got_syscode) throw new TProtocolException("Required field 'syscode' was not found in serialized data for struct PassportUser")
    if (!_got_registerDate) throw new TProtocolException("Required field 'registerDate' was not found in serialized data for struct PassportUser")
    new Immutable(
      id,
      mobile,
      loginName,
      source,
      syscode,
      registerDate,
      lastLoginDate,
      if (_passthroughFields == null)
        NoPassthroughFields
      else
        _passthroughFields.result()
    )
  }

  def apply(
    id: Long,
    mobile: String,
    loginName: String,
    source: com.itiancai.passport.thrift.Source,
    syscode: com.itiancai.passport.thrift.SysCode,
    registerDate: Long,
    lastLoginDate: _root_.scala.Option[Long] = _root_.scala.None
  ): PassportUser =
    new Immutable(
      id,
      mobile,
      loginName,
      source,
      syscode,
      registerDate,
      lastLoginDate
    )

  def unapply(_item: PassportUser): _root_.scala.Option[scala.Product7[Long, String, String, com.itiancai.passport.thrift.Source, com.itiancai.passport.thrift.SysCode, Long, Option[Long]]] = _root_.scala.Some(_item)


  @inline private def readIdValue(_iprot: TProtocol): Long = {
    _iprot.readI64()
  }

  @inline private def writeIdField(id_item: Long, _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(IdField)
    writeIdValue(id_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeIdValue(id_item: Long, _oprot: TProtocol): Unit = {
    _oprot.writeI64(id_item)
  }

  @inline private def readMobileValue(_iprot: TProtocol): String = {
    _iprot.readString()
  }

  @inline private def writeMobileField(mobile_item: String, _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(MobileField)
    writeMobileValue(mobile_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeMobileValue(mobile_item: String, _oprot: TProtocol): Unit = {
    _oprot.writeString(mobile_item)
  }

  @inline private def readLoginNameValue(_iprot: TProtocol): String = {
    _iprot.readString()
  }

  @inline private def writeLoginNameField(loginName_item: String, _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(LoginNameField)
    writeLoginNameValue(loginName_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeLoginNameValue(loginName_item: String, _oprot: TProtocol): Unit = {
    _oprot.writeString(loginName_item)
  }

  @inline private def readSourceValue(_iprot: TProtocol): com.itiancai.passport.thrift.Source = {
    com.itiancai.passport.thrift.Source.getOrUnknown(_iprot.readI32())
  }

  @inline private def writeSourceField(source_item: com.itiancai.passport.thrift.Source, _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(SourceFieldI32)
    writeSourceValue(source_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeSourceValue(source_item: com.itiancai.passport.thrift.Source, _oprot: TProtocol): Unit = {
    _oprot.writeI32(source_item.value)
  }

  @inline private def readSyscodeValue(_iprot: TProtocol): com.itiancai.passport.thrift.SysCode = {
    com.itiancai.passport.thrift.SysCode.getOrUnknown(_iprot.readI32())
  }

  @inline private def writeSyscodeField(syscode_item: com.itiancai.passport.thrift.SysCode, _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(SyscodeFieldI32)
    writeSyscodeValue(syscode_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeSyscodeValue(syscode_item: com.itiancai.passport.thrift.SysCode, _oprot: TProtocol): Unit = {
    _oprot.writeI32(syscode_item.value)
  }

  @inline private def readRegisterDateValue(_iprot: TProtocol): Long = {
    _iprot.readI64()
  }

  @inline private def writeRegisterDateField(registerDate_item: Long, _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(RegisterDateField)
    writeRegisterDateValue(registerDate_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeRegisterDateValue(registerDate_item: Long, _oprot: TProtocol): Unit = {
    _oprot.writeI64(registerDate_item)
  }

  @inline private def readLastLoginDateValue(_iprot: TProtocol): Long = {
    _iprot.readI64()
  }

  @inline private def writeLastLoginDateField(lastLoginDate_item: Long, _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(LastLoginDateField)
    writeLastLoginDateValue(lastLoginDate_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeLastLoginDateValue(lastLoginDate_item: Long, _oprot: TProtocol): Unit = {
    _oprot.writeI64(lastLoginDate_item)
  }


  object Immutable extends ThriftStructCodec3[PassportUser] {
    override def encode(_item: PassportUser, _oproto: TProtocol): Unit = { _item.write(_oproto) }
    override def decode(_iprot: TProtocol): PassportUser = PassportUser.decode(_iprot)
    override lazy val metaData: ThriftStructMetaData[PassportUser] = PassportUser.metaData
  }

  /**
   * The default read-only implementation of PassportUser.  You typically should not need to
   * directly reference this class; instead, use the PassportUser.apply method to construct
   * new instances.
   */
  class Immutable(
      val id: Long,
      val mobile: String,
      val loginName: String,
      val source: com.itiancai.passport.thrift.Source,
      val syscode: com.itiancai.passport.thrift.SysCode,
      val registerDate: Long,
      val lastLoginDate: _root_.scala.Option[Long],
      override val _passthroughFields: immutable$Map[Short, TFieldBlob])
    extends PassportUser {
    def this(
      id: Long,
      mobile: String,
      loginName: String,
      source: com.itiancai.passport.thrift.Source,
      syscode: com.itiancai.passport.thrift.SysCode,
      registerDate: Long,
      lastLoginDate: _root_.scala.Option[Long] = _root_.scala.None
    ) = this(
      id,
      mobile,
      loginName,
      source,
      syscode,
      registerDate,
      lastLoginDate,
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
      val id: Long,
      mobileOffset: Int,
      loginNameOffset: Int,
      val source: com.itiancai.passport.thrift.Source,
      val syscode: com.itiancai.passport.thrift.SysCode,
      val registerDate: Long,
      lastLoginDateOffset: Int,
      override val _passthroughFields: immutable$Map[Short, TFieldBlob])
    extends PassportUser {

    override def write(_oprot: TProtocol): Unit = {
      _oprot match {
        case i: LazyTProtocol => i.writeRaw(_buf, _start_offset, _end_offset - _start_offset)
        case _ => super.write(_oprot)
      }
    }

    lazy val mobile: String =
      if (mobileOffset == -1)
        null
      else {
        _proto.decodeString(_buf, mobileOffset)
      }
    lazy val loginName: String =
      if (loginNameOffset == -1)
        null
      else {
        _proto.decodeString(_buf, loginNameOffset)
      }
    lazy val lastLoginDate: _root_.scala.Option[Long] =
      if (lastLoginDateOffset == -1)
        None
      else {
        Some(_proto.decodeI64(_buf, lastLoginDateOffset))
      }

    /**
     * Override the super hash code to make it a lazy val rather than def.
     *
     * Calculating the hash code can be expensive, caching it where possible
     * can provide significant performance wins. (Key in a hash map for instance)
     * Usually not safe since the normal constructor will accept a mutable map or
     * set as an arg
     * Here however we control how the class is generated from serialized data.
     * With the class private and the contract that we throw away our mutable references
     * having the hash code lazy here is safe.
     */
    override lazy val hashCode = super.hashCode
  }

  /**
   * This Proxy trait allows you to extend the PassportUser trait with additional state or
   * behavior and implement the read-only methods from PassportUser using an underlying
   * instance.
   */
  trait Proxy extends PassportUser {
    protected def _underlying_PassportUser: PassportUser
    override def id: Long = _underlying_PassportUser.id
    override def mobile: String = _underlying_PassportUser.mobile
    override def loginName: String = _underlying_PassportUser.loginName
    override def source: com.itiancai.passport.thrift.Source = _underlying_PassportUser.source
    override def syscode: com.itiancai.passport.thrift.SysCode = _underlying_PassportUser.syscode
    override def registerDate: Long = _underlying_PassportUser.registerDate
    override def lastLoginDate: _root_.scala.Option[Long] = _underlying_PassportUser.lastLoginDate
    override def _passthroughFields = _underlying_PassportUser._passthroughFields
  }
}

trait PassportUser
  extends ThriftStruct
  with scala.Product7[Long, String, String, com.itiancai.passport.thrift.Source, com.itiancai.passport.thrift.SysCode, Long, Option[Long]]
  with java.io.Serializable
{
  import PassportUser._

  def id: Long
  def mobile: String
  def loginName: String
  def source: com.itiancai.passport.thrift.Source
  def syscode: com.itiancai.passport.thrift.SysCode
  def registerDate: Long
  def lastLoginDate: _root_.scala.Option[Long]

  def _passthroughFields: immutable$Map[Short, TFieldBlob] = immutable$Map.empty

  def _1 = id
  def _2 = mobile
  def _3 = loginName
  def _4 = source
  def _5 = syscode
  def _6 = registerDate
  def _7 = lastLoginDate


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
              if (true) {
                writeIdValue(id, _oprot)
                _root_.scala.Some(PassportUser.IdField)
              } else {
                _root_.scala.None
              }
            case 2 =>
              if (mobile ne null) {
                writeMobileValue(mobile, _oprot)
                _root_.scala.Some(PassportUser.MobileField)
              } else {
                _root_.scala.None
              }
            case 3 =>
              if (loginName ne null) {
                writeLoginNameValue(loginName, _oprot)
                _root_.scala.Some(PassportUser.LoginNameField)
              } else {
                _root_.scala.None
              }
            case 4 =>
              if (source ne null) {
                writeSourceValue(source, _oprot)
                _root_.scala.Some(PassportUser.SourceField)
              } else {
                _root_.scala.None
              }
            case 5 =>
              if (syscode ne null) {
                writeSyscodeValue(syscode, _oprot)
                _root_.scala.Some(PassportUser.SyscodeField)
              } else {
                _root_.scala.None
              }
            case 6 =>
              if (true) {
                writeRegisterDateValue(registerDate, _oprot)
                _root_.scala.Some(PassportUser.RegisterDateField)
              } else {
                _root_.scala.None
              }
            case 7 =>
              if (lastLoginDate.isDefined) {
                writeLastLoginDateValue(lastLoginDate.get, _oprot)
                _root_.scala.Some(PassportUser.LastLoginDateField)
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
  def setField(_blob: TFieldBlob): PassportUser = {
    var id: Long = this.id
    var mobile: String = this.mobile
    var loginName: String = this.loginName
    var source: com.itiancai.passport.thrift.Source = this.source
    var syscode: com.itiancai.passport.thrift.SysCode = this.syscode
    var registerDate: Long = this.registerDate
    var lastLoginDate: _root_.scala.Option[Long] = this.lastLoginDate
    var _passthroughFields = this._passthroughFields
    _blob.id match {
      case 1 =>
        id = readIdValue(_blob.read)
      case 2 =>
        mobile = readMobileValue(_blob.read)
      case 3 =>
        loginName = readLoginNameValue(_blob.read)
      case 4 =>
        source = readSourceValue(_blob.read)
      case 5 =>
        syscode = readSyscodeValue(_blob.read)
      case 6 =>
        registerDate = readRegisterDateValue(_blob.read)
      case 7 =>
        lastLoginDate = _root_.scala.Some(readLastLoginDateValue(_blob.read))
      case _ => _passthroughFields += (_blob.id -> _blob)
    }
    new Immutable(
      id,
      mobile,
      loginName,
      source,
      syscode,
      registerDate,
      lastLoginDate,
      _passthroughFields
    )
  }

  /**
   * If the specified field is optional, it is set to None.  Otherwise, if the field is
   * known, it is reverted to its default value; if the field is unknown, it is removed
   * from the passthroughFields map, if present.
   */
  def unsetField(_fieldId: Short): PassportUser = {
    var id: Long = this.id
    var mobile: String = this.mobile
    var loginName: String = this.loginName
    var source: com.itiancai.passport.thrift.Source = this.source
    var syscode: com.itiancai.passport.thrift.SysCode = this.syscode
    var registerDate: Long = this.registerDate
    var lastLoginDate: _root_.scala.Option[Long] = this.lastLoginDate

    _fieldId match {
      case 1 =>
        id = 0L
      case 2 =>
        mobile = null
      case 3 =>
        loginName = null
      case 4 =>
        source = null
      case 5 =>
        syscode = null
      case 6 =>
        registerDate = 0L
      case 7 =>
        lastLoginDate = _root_.scala.None
      case _ =>
    }
    new Immutable(
      id,
      mobile,
      loginName,
      source,
      syscode,
      registerDate,
      lastLoginDate,
      _passthroughFields - _fieldId
    )
  }

  /**
   * If the specified field is optional, it is set to None.  Otherwise, if the field is
   * known, it is reverted to its default value; if the field is unknown, it is removed
   * from the passthroughFields map, if present.
   */
  def unsetId: PassportUser = unsetField(1)

  def unsetMobile: PassportUser = unsetField(2)

  def unsetLoginName: PassportUser = unsetField(3)

  def unsetSource: PassportUser = unsetField(4)

  def unsetSyscode: PassportUser = unsetField(5)

  def unsetRegisterDate: PassportUser = unsetField(6)

  def unsetLastLoginDate: PassportUser = unsetField(7)


  override def write(_oprot: TProtocol): Unit = {
    PassportUser.validate(this)
    _oprot.writeStructBegin(Struct)
    writeIdField(id, _oprot)
    if (mobile ne null) writeMobileField(mobile, _oprot)
    if (loginName ne null) writeLoginNameField(loginName, _oprot)
    if (source ne null) writeSourceField(source, _oprot)
    if (syscode ne null) writeSyscodeField(syscode, _oprot)
    writeRegisterDateField(registerDate, _oprot)
    if (lastLoginDate.isDefined) writeLastLoginDateField(lastLoginDate.get, _oprot)
    if (_passthroughFields.nonEmpty) {
      _passthroughFields.values.foreach { _.write(_oprot) }
    }
    _oprot.writeFieldStop()
    _oprot.writeStructEnd()
  }

  def copy(
    id: Long = this.id,
    mobile: String = this.mobile,
    loginName: String = this.loginName,
    source: com.itiancai.passport.thrift.Source = this.source,
    syscode: com.itiancai.passport.thrift.SysCode = this.syscode,
    registerDate: Long = this.registerDate,
    lastLoginDate: _root_.scala.Option[Long] = this.lastLoginDate,
    _passthroughFields: immutable$Map[Short, TFieldBlob] = this._passthroughFields
  ): PassportUser =
    new Immutable(
      id,
      mobile,
      loginName,
      source,
      syscode,
      registerDate,
      lastLoginDate,
      _passthroughFields
    )

  override def canEqual(other: Any): Boolean = other.isInstanceOf[PassportUser]

  override def equals(other: Any): Boolean =
    canEqual(other) &&
      _root_.scala.runtime.ScalaRunTime._equals(this, other) &&
      _passthroughFields == other.asInstanceOf[PassportUser]._passthroughFields

  override def hashCode: Int = _root_.scala.runtime.ScalaRunTime._hashCode(this)

  override def toString: String = _root_.scala.runtime.ScalaRunTime._toString(this)


  override def productArity: Int = 7

  override def productElement(n: Int): Any = n match {
    case 0 => this.id
    case 1 => this.mobile
    case 2 => this.loginName
    case 3 => this.source
    case 4 => this.syscode
    case 5 => this.registerDate
    case 6 => this.lastLoginDate
    case _ => throw new IndexOutOfBoundsException(n.toString)
  }

  override def productPrefix: String = "PassportUser"
}