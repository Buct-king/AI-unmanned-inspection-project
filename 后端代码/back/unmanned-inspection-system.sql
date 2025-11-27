-- MySQL dump 10.13  Distrib 8.3.0, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: unmanned-inspection-system
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alarm`
--

DROP TABLE IF EXISTS `alarm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alarm` (
  `alarm_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `device_id` int NOT NULL COMMENT '所属设备id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `type` tinyint DEFAULT NULL COMMENT '报警类型（同异常测点的状态）',
  `description` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '报警描述',
  `status` tinyint NOT NULL COMMENT '处置状态',
  `alarm_time` datetime NOT NULL COMMENT '报警时间',
  `processed_time` datetime DEFAULT NULL COMMENT '处理时间点',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`alarm_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报警表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alarm`
--

LOCK TABLES `alarm` WRITE;
/*!40000 ALTER TABLE `alarm` DISABLE KEYS */;
INSERT INTO `alarm` VALUES (1,1,1,1,'测试报警记录',1,'2025-01-01 16:11:42','2025-02-02 21:23:16','2025-05-01 16:11:49','2025-05-02 17:06:15'),(2,1,2,2,'111',1,'2025-03-02 20:33:52','2025-03-02 21:23:16','2025-05-02 20:33:52','2025-05-02 21:04:09'),(3,1,3,5,'222',1,'2025-03-02 20:34:07','2025-05-02 21:23:17','2025-05-02 20:34:08','2025-05-02 20:34:09'),(4,2,4,2,'333',1,'2025-02-02 20:34:29','2025-04-02 21:23:17','2025-05-02 20:34:29','2025-05-02 20:34:31'),(5,2,3,3,'555',1,'2025-05-02 20:35:03','2025-04-02 21:23:18','2025-05-02 20:35:03','2025-05-02 20:35:03'),(6,2,3,3,'666',1,'2025-01-02 21:26:59','2025-01-02 21:27:01','2025-05-02 21:27:02','2025-05-02 21:27:02');
/*!40000 ALTER TABLE `alarm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
INSERT INTO `device` VALUES (1,1,'设备1',1,1,1,'2025-05-01 16:43:44','2025-05-01 16:43:46'),(2,2,'设备2',1,1,1,'2025-05-02 19:59:11','2025-05-02 19:59:14'),(3,2,'设备3',1,1,1,'2025-05-02 19:59:11','2025-05-02 19:59:14'),(4,0,'设备4',1,1,1,'2025-05-02 19:59:11','2025-05-02 19:59:14');
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flowing_water_record`
--

DROP TABLE IF EXISTS `flowing_water_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flowing_water_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `status` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流水状态',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流水状态记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flowing_water_record`
--

LOCK TABLES `flowing_water_record` WRITE;
/*!40000 ALTER TABLE `flowing_water_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `flowing_water_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `indicator_record`
--

DROP TABLE IF EXISTS `indicator_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `indicator_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `status` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '指示灯状态',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='指示灯状态记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `indicator_record`
--

LOCK TABLES `indicator_record` WRITE;
/*!40000 ALTER TABLE `indicator_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `indicator_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `instrument_record`
--

DROP TABLE IF EXISTS `instrument_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `instrument_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `status` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '仪表状态',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仪表状态记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `instrument_record`
--

LOCK TABLES `instrument_record` WRITE;
/*!40000 ALTER TABLE `instrument_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `instrument_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `liquid_level_record`
--

DROP TABLE IF EXISTS `liquid_level_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `liquid_level_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `temp` decimal(6,2) NOT NULL COMMENT '液位',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='液位记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `liquid_level_record`
--

LOCK TABLES `liquid_level_record` WRITE;
/*!40000 ALTER TABLE `liquid_level_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `liquid_level_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `measuring_point`
--

DROP TABLE IF EXISTS `measuring_point`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `measuring_point` (
  `mp_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `device_id` int NOT NULL COMMENT '测点所属设备id',
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '测点名称',
  `type` tinyint NOT NULL COMMENT '测点类型',
  `status` tinyint NOT NULL COMMENT '测点状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`mp_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测点表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `measuring_point`
--

LOCK TABLES `measuring_point` WRITE;
/*!40000 ALTER TABLE `measuring_point` DISABLE KEYS */;
INSERT INTO `measuring_point` VALUES (1,1,'测点1',1,1,'2025-05-01 16:44:40','2025-05-01 16:44:41'),(2,1,'测点2',1,1,'2025-05-03 17:27:59','2025-05-03 17:27:59'),(3,2,'测点3',1,1,'2025-05-03 18:01:53','2025-05-03 18:01:53');
/*!40000 ALTER TABLE `measuring_point` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pipeline_record`
--

DROP TABLE IF EXISTS `pipeline_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pipeline_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `status` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管路状态',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管路状态记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pipeline_record`
--

LOCK TABLES `pipeline_record` WRITE;
/*!40000 ALTER TABLE `pipeline_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `pipeline_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pump_lubrication_record`
--

DROP TABLE IF EXISTS `pump_lubrication_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pump_lubrication_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `status` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '润滑状态',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='泵润滑状态记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pump_lubrication_record`
--

LOCK TABLES `pump_lubrication_record` WRITE;
/*!40000 ALTER TABLE `pump_lubrication_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `pump_lubrication_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pump_pointer_record`
--

DROP TABLE IF EXISTS `pump_pointer_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pump_pointer_record`
--

LOCK TABLES `pump_pointer_record` WRITE;
/*!40000 ALTER TABLE `pump_pointer_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `pump_pointer_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pump_temp_record`
--

DROP TABLE IF EXISTS `pump_temp_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pump_temp_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `temp` decimal(6,2) NOT NULL COMMENT '温度值，摄氏度',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='泵温度记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pump_temp_record`
--

LOCK TABLES `pump_temp_record` WRITE;
/*!40000 ALTER TABLE `pump_temp_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `pump_temp_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pump_vibration_record`
--

DROP TABLE IF EXISTS `pump_vibration_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pump_vibration_record`
--

LOCK TABLES `pump_vibration_record` WRITE;
/*!40000 ALTER TABLE `pump_vibration_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `pump_vibration_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `switch_position_record`
--

DROP TABLE IF EXISTS `switch_position_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `switch_position_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mp_id` bigint NOT NULL COMMENT '所属测点id',
  `status` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手动开关位置',
  `time` datetime NOT NULL COMMENT '记录时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='手动开关位置记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `switch_position_record`
--

LOCK TABLES `switch_position_record` WRITE;
/*!40000 ALTER TABLE `switch_position_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `switch_position_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'测试11223','4297f44b13955235245b2497399d7a93','13666666666',1,1,'2025-04-21 15:17:21','2025-05-03 18:21:02'),(2,'普通244','c8837b23ff8aaa8a2dde915473ce0991','12345678901',1,0,'2025-05-02 17:17:59','2025-05-02 17:44:06'),(3,'普通12','c8837b23ff8aaa8a2dde915473ce0991','12345678900',1,0,'2025-05-02 17:29:02','2025-05-02 17:29:02');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workshop`
--

DROP TABLE IF EXISTS `workshop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workshop` (
  `workshop_id` int NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '车间名称',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0禁用，1启用',
  `device_count` int DEFAULT NULL COMMENT '拥有设备数目',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`workshop_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车间表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workshop`
--

LOCK TABLES `workshop` WRITE;
/*!40000 ALTER TABLE `workshop` DISABLE KEYS */;
INSERT INTO `workshop` VALUES (1,'车间1',1,1,'2025-05-01 16:44:14','2025-05-01 16:44:16'),(2,'车间2',1,3,'2025-05-02 19:51:51','2025-05-02 19:51:52'),(3,'车间3',1,2,'2025-05-02 19:52:01','2025-05-02 19:51:59');
/*!40000 ALTER TABLE `workshop` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-05 15:20:57

-- 新增模型(pt)信息表
DROP TABLE IF EXISTS `model_pt`;
CREATE TABLE `model_pt` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `file_name` varchar(128) NOT NULL COMMENT 'pt文件名',
  `title` varchar(64) NOT NULL COMMENT '功能标题',
  `description` varchar(512) DEFAULT NULL COMMENT '功能描述',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_name` (`file_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模型pt文件信息表';

-- 新增摄像头与模型绑定表
DROP TABLE IF EXISTS `camera_model_bind`;
CREATE TABLE `camera_model_bind` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `camera_id` varchar(64) NOT NULL COMMENT '摄像头唯一标识',
  `camera_name` varchar(128) NOT NULL COMMENT '摄像头名称',
  `camera_source` varchar(256) NOT NULL COMMENT '摄像头源（数字索引或RTSP URL）',
  `model_name` varchar(128) NOT NULL COMMENT '绑定的pt模型文件名',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_camera_id` (`camera_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='摄像头与模型绑定表';
