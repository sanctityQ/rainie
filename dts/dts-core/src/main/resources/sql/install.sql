CREATE DATABASE dts;

USE dts;

SET NAMES UTF8;

CREATE TABLE dts_activity (
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '唯一id',
  tx_id varchar(63) NOT NULL DEFAULT '' COMMENT '事务唯一id',
  business_id varchar(63) NOT NULL DEFAULT '' COMMENT '业务id',
  business_type varchar(63) NOT NULL DEFAULT '' COMMENT '业务类型',
  retry_count int(11) NOT NULL DEFAULT 0 COMMENT '重试次数',
  time_out bigint(20) NOT NULL DEFAULT 0 COMMENT '超时时间',
  status tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态: 0-初始,1-pre成功 2-成功,3-失败',
  finish tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态: 0-未开始,1-完成,2-处理中',
  c_time timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  m_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tx_id` (`tx_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE dts_action (
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '唯一id',
  tx_id varchar(63) NOT NULL DEFAULT '' COMMENT '事务唯一id',
  action_id varchar(63) NOT NULL DEFAULT '' COMMENT '子事务ID',
  instruction_id varchar(63) NOT NULL DEFAULT '' COMMENT '幂等id',
  service_name varchar(63) NOT NULL DEFAULT '' COMMENT '服务名',
  status tinyint(4) NOT NULL DEFAULT 0 COMMENT '0-初始状态,1-prepare状态,2-成功,3-失败',
  c_time timestamp NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  m_time timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_action_id` (`action_id`),
  KEY `tx_id` (`tx_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;