-- 删除已存在的数据库
DROP DATABASE IF EXISTS `frond_end`;

-- 创建数据库
CREATE DATABASE `frond_end` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `frond_end`;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for alarm
-- ----------------------------
DROP TABLE IF EXISTS `alarm`;
CREATE TABLE `alarm` (
  `alarm_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `device_id` int NOT NULL COMMENT '所属设备id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `status` tinyint NOT NULL COMMENT '处置状态',
  `alarm_time` datetime NOT NULL COMMENT '报警时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`alarm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报警表';

-- ----------------------------
-- Table structure for device
-- ----------------------------
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device` (
  `device_id` int NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `device_type` int NOT NULL COMMENT '设备类型',
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备名称',
  `run_status` tinyint NOT NULL COMMENT '运行状态',
  `health_status` tinyint NOT NULL COMMENT '健康状态',
  `workshop_id` int DEFAULT NULL COMMENT '车间id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备表';

-- ----------------------------
-- Table structure for flowing_water_record
-- ----------------------------
DROP TABLE IF EXISTS `flowing_water_record`;
CREATE TABLE `flowing_water_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `status` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流水状态',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流水状态记录表';

-- ----------------------------
-- Table structure for indicator_record
-- ----------------------------
DROP TABLE IF EXISTS `indicator_record`;
CREATE TABLE `indicator_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `status` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '指示灯状态',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='指示灯状态记录表';

-- ----------------------------
-- Table structure for instrument_record
-- ----------------------------
DROP TABLE IF EXISTS `instrument_record`;
CREATE TABLE `instrument_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `status` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '仪表状态',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仪表状态记录表';

-- ----------------------------
-- Table structure for liquid_level_record
-- ----------------------------
DROP TABLE IF EXISTS `liquid_level_record`;
CREATE TABLE `liquid_level_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `temp` decimal(6,2) NOT NULL COMMENT '液位',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='液位记录表';

-- ----------------------------
-- Table structure for measuring_point
-- ----------------------------
DROP TABLE IF EXISTS `measuring_point`;
CREATE TABLE `measuring_point` (
  `mp_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `device_id` int NOT NULL COMMENT '测点所属设备id',
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '测点名称',
  `type` tinyint NOT NULL COMMENT '测点类型',
  `status` tinyint NOT NULL COMMENT '测点状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`mp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测点表';

-- ----------------------------
-- Table structure for pipeline_record
-- ----------------------------
DROP TABLE IF EXISTS `pipeline_record`;
CREATE TABLE `pipeline_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `status` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管路状态',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管路状态记录表';

-- ----------------------------
-- Table structure for pump_lubrication_record
-- ----------------------------
DROP TABLE IF EXISTS `pump_lubrication_record`;
CREATE TABLE `pump_lubrication_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `status` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '润滑状态',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='泵润滑状态记录表';

-- ----------------------------
-- Table structure for pump_pointer_record
-- ----------------------------
DROP TABLE IF EXISTS `pump_pointer_record`;
CREATE TABLE `pump_pointer_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `pressure` decimal(6,3) NOT NULL COMMENT '压力值',
  `unit` tinyint NOT NULL DEFAULT '2' COMMENT '压力单位，0:Pa, 1:KPa, 2:MPa',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='泵指针记录表';

-- ----------------------------
-- Table structure for pump_temp_record
-- ----------------------------
DROP TABLE IF EXISTS `pump_temp_record`;
CREATE TABLE `pump_temp_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `temp` decimal(6,2) NOT NULL COMMENT '温度值，摄氏度',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='泵温度记录表';

-- ----------------------------
-- Table structure for pump_vibration_record
-- ----------------------------
DROP TABLE IF EXISTS `pump_vibration_record`;
CREATE TABLE `pump_vibration_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `x_acc` decimal(6,3) NOT NULL COMMENT 'x轴振动加速度，m/s^2',
  `y_acc` decimal(6,3) NOT NULL COMMENT 'y轴振动加速度，m/s^2',
  `z_acc` decimal(6,3) NOT NULL COMMENT 'z轴振动加速度，m/s^2',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='泵振动记录表';

-- ----------------------------
-- Table structure for switch_position_record
-- ----------------------------
DROP TABLE IF EXISTS `switch_position_record`;
CREATE TABLE `switch_position_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `status` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手动开关位置',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='手动开关位置记录表';

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `name` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `phone_number` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0禁用，1启用',
  `role_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '角色，0：用户，1：管理员',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- Table structure for workshop
-- ----------------------------
DROP TABLE IF EXISTS `workshop`;
CREATE TABLE `workshop` (
  `workshop_id` int NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '车间名称',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0禁用，1启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`workshop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车间表';

-- 插入管理员用户
INSERT INTO `user` (`name`, `password`, `phone_number`, `status`, `role_type`, `create_time`, `update_time`)
VALUES ('测试', '123321', '13666666666', 1, 1, NOW(), NOW());

-- 初始化用户数据
INSERT INTO `user` (`name`, `password`, `phone_number`, `status`, `role_type`, `create_time`, `update_time`) VALUES
('admin', '4297f44b13955235245b2497399d7a93', '13800000000', 1, 1, NOW(), NOW()),  -- 管理员用户 密码：123123
('operator1', '4297f44b13955235245b2497399d7a93', '13800000001', 1, 0, NOW(), NOW()), -- 普通操作员
('operator2', '4297f44b13955235245b2497399d7a93', '13800000002', 1, 0, NOW(), NOW()), -- 普通操作员
('maintainer1', '4297f44b13955235245b2497399d7a93', '13800000003', 1, 0, NOW(), NOW()); -- 维护人员

-- 初始化车间数据
INSERT INTO `workshop` (`name`, `status`, `create_time`, `update_time`) VALUES
('制造一车间', 1, NOW(), NOW()),
('制造二车间', 1, NOW(), NOW()),
('装配车间', 1, NOW(), NOW()),
('测试车间', 1, NOW(), NOW());

-- 初始化设备数据
INSERT INTO `device` (`device_type`, `name`, `run_status`, `health_status`, `workshop_id`, `create_time`, `update_time`) VALUES
(1, '注塑机-01', 1, 1, 1, NOW(), NOW()),
(1, '注塑机-02', 1, 1, 1, NOW(), NOW()),
(2, '冷却泵-01', 1, 1, 1, NOW(), NOW()),
(2, '冷却泵-02', 1, 1, 2, NOW(), NOW()),
(3, '压缩机-01', 1, 1, 2, NOW(), NOW()),
(3, '装配线-01', 1, 1, 3, NOW(), NOW()),
(4, '测试台-01', 1, 1, 4, NOW(), NOW()),
(4, '测试台-02', 1, 1, 3, NOW(), NOW());

-- 初始化测点数据
INSERT INTO `measuring_point` (`device_id`, `name`, `type`, `status`, `create_time`, `update_time`) VALUES
(1, '温度传感器1', 1, 1, NOW(), NOW()),
(1, '压力传感器1', 2, 1, NOW(), NOW()),
(1, '振动传感器1', 3, 1, NOW(), NOW()),
(2, '温度传感器2', 1, 1, NOW(), NOW()),
(2, '液位传感器1', 4, 1, NOW(), NOW()),
(3, '压力传感器2', 2, 1, NOW(), NOW()),
(3, '流量传感器1', 5, 1, NOW(), NOW()),
(4, '温度传感器3', 1, 1, NOW(), NOW()),
(4, '振动传感器2', 3, 1, NOW(), NOW());

-- 初始化报警数据
INSERT INTO `alarm` (`device_id`, `mp_id`, `status`, `alarm_time`, `create_time`, `update_time`) VALUES
(1, 1, 1, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), NOW()),
(1, 2, 1, DATE_SUB(NOW(), INTERVAL 3 HOUR), NOW(), NOW()),
(2, 4, 0, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(3, 6, 0, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW());

-- 初始化流水状态记录
INSERT INTO `flowing_water_record` (`mp_id`, `status`, `time`, `create_time`, `update_time`) VALUES
(7, '正常', DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(7, '正常', DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW());

-- 初始化指示灯状态记录
INSERT INTO `indicator_record` (`mp_id`, `status`, `time`, `create_time`, `update_time`) VALUES
(1, '绿色', DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(2, '绿色', DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW());

-- 初始化仪表状态记录
INSERT INTO `instrument_record` (`mp_id`, `status`, `time`, `create_time`, `update_time`) VALUES
(2, '正常', DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(6, '正常', DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW());

-- 初始化液位记录
INSERT INTO `liquid_level_record` (`mp_id`, `temp`, `time`, `create_time`, `update_time`) VALUES
(5, 75.5, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(5, 76.2, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW());

-- 初始化管路状态记录
INSERT INTO `pipeline_record` (`mp_id`, `status`, `time`, `create_time`, `update_time`) VALUES
(3, '正常', DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(6, '正常', DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW());

-- 初始化泵润滑状态记录
INSERT INTO `pump_lubrication_record` (`mp_id`, `status`, `time`, `create_time`, `update_time`) VALUES
(3, '良好', DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(6, '良好', DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW());

-- 初始化泵指针记录
INSERT INTO `pump_pointer_record` (`mp_id`, `pressure`, `unit`, `time`, `create_time`, `update_time`) VALUES
(2, 2.5, 2, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(2, 2.6, 2, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW()),
(6, 2.3, 2, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(6, 2.2, 2, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW());

-- 初始化泵温度记录
INSERT INTO `pump_temp_record` (`mp_id`, `temp`, `time`, `create_time`, `update_time`) VALUES
(1, 45.5, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(1, 46.2, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW()),
(4, 42.3, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(4, 43.1, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW());

-- 初始化泵振动记录
INSERT INTO `pump_vibration_record` (`mp_id`, `x_acc`, `y_acc`, `z_acc`, `time`, `create_time`, `update_time`) VALUES
(3, 0.15, 0.12, 0.18, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(3, 0.16, 0.13, 0.19, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW()),
(9, 0.14, 0.11, 0.17, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(9, 0.15, 0.12, 0.18, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW());

-- 初始化开关位置记录
INSERT INTO `switch_position_record` (`mp_id`, `status`, `time`, `create_time`, `update_time`) VALUES
(1, '开启', DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(2, '开启', DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW()),
(3, '开启', DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), NOW()),
(4, '开启', DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW(), NOW()); 