-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: gimnasio3000
-- ------------------------------------------------------
-- Server version	8.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `clientemembresia`
--

DROP TABLE IF EXISTS `clientemembresia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientemembresia` (
  `id_cliente_membresia` bigint NOT NULL AUTO_INCREMENT,
  `id_usuario` bigint NOT NULL,
  `id_membresia` bigint NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `estado` enum('Activa','Congelada','Vencida') NOT NULL,
  PRIMARY KEY (`id_cliente_membresia`),
  KEY `id_usuario` (`id_usuario`),
  KEY `id_membresia` (`id_membresia`),
  CONSTRAINT `clientemembresia_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE CASCADE,
  CONSTRAINT `clientemembresia_ibfk_2` FOREIGN KEY (`id_membresia`) REFERENCES `membresia` (`id_membresia`) ON DELETE CASCADE,
  CONSTRAINT `FKfudo5nh0hv7n5naixg22olr4p` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientemembresia`
--

LOCK TABLES `clientemembresia` WRITE;
/*!40000 ALTER TABLE `clientemembresia` DISABLE KEYS */;
INSERT INTO `clientemembresia` VALUES (1,1,4,'2025-01-01','2026-01-01','Activa'),(2,2,3,'2025-07-01','2026-01-01','Activa'),(3,3,1,'2025-09-15','2025-10-15','Vencida'),(4,4,2,'2025-10-01','2026-01-01','Activa');
/*!40000 ALTER TABLE `clientemembresia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `congelamiento`
--

DROP TABLE IF EXISTS `congelamiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `congelamiento` (
  `id_congelamiento` bigint NOT NULL AUTO_INCREMENT,
  `id_cliente_membresia` bigint NOT NULL,
  `motivo` varchar(255) DEFAULT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  PRIMARY KEY (`id_congelamiento`),
  KEY `id_cliente_membresia` (`id_cliente_membresia`),
  CONSTRAINT `congelamiento_ibfk_1` FOREIGN KEY (`id_cliente_membresia`) REFERENCES `clientemembresia` (`id_cliente_membresia`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `congelamiento`
--

LOCK TABLES `congelamiento` WRITE;
/*!40000 ALTER TABLE `congelamiento` DISABLE KEYS */;
/*!40000 ALTER TABLE `congelamiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `membresia`
--

DROP TABLE IF EXISTS `membresia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `membresia` (
  `id_membresia` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` text,
  `precio` double DEFAULT NULL,
  `duracion_dias` int NOT NULL,
  PRIMARY KEY (`id_membresia`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `membresia`
--

LOCK TABLES `membresia` WRITE;
/*!40000 ALTER TABLE `membresia` DISABLE KEYS */;
INSERT INTO `membresia` VALUES (1,'Mensual','Acceso total al gimnasio durante 30 días',80000,30),(2,'Trimestral','Acceso ilimitado durante 3 meses con descuento especial',210000,90),(3,'Semestral','Incluye asesoría personalizada durante 6 meses',390000,180),(4,'Anual','Acceso completo por 1 año + beneficios premium',700000,365);
/*!40000 ALTER TABLE `membresia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notificacion`
--

DROP TABLE IF EXISTS `notificacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notificacion` (
  `id_notificacion` bigint NOT NULL AUTO_INCREMENT,
  `id_usuario` bigint NOT NULL,
  `mensaje` text NOT NULL,
  `fecha_envio` datetime NOT NULL,
  `estado` enum('Pendiente','Enviada','Leida') DEFAULT 'Pendiente',
  PRIMARY KEY (`id_notificacion`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `notificacion_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notificacion`
--

LOCK TABLES `notificacion` WRITE;
/*!40000 ALTER TABLE `notificacion` DISABLE KEYS */;
/*!40000 ALTER TABLE `notificacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pago`
--

DROP TABLE IF EXISTS `pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago` (
  `id_pago` bigint NOT NULL AUTO_INCREMENT,
  `id_cliente_membresia` bigint NOT NULL,
  `fecha_pago` date NOT NULL,
  `monto` decimal(38,2) NOT NULL,
  `metodo_pago` enum('Efectivo','Tarjeta','Transferencia') NOT NULL,
  `fechaPago` date DEFAULT NULL,
  `metodoPago` enum('Efectivo','Tarjeta','Transferencia') DEFAULT NULL,
  PRIMARY KEY (`id_pago`),
  KEY `id_cliente_membresia` (`id_cliente_membresia`),
  CONSTRAINT `pago_ibfk_1` FOREIGN KEY (`id_cliente_membresia`) REFERENCES `clientemembresia` (`id_cliente_membresia`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pago`
--

LOCK TABLES `pago` WRITE;
/*!40000 ALTER TABLE `pago` DISABLE KEYS */;
INSERT INTO `pago` VALUES (1,1,'2025-10-01',80000.00,'Tarjeta',NULL,NULL),(2,2,'2025-09-15',210000.00,'Transferencia',NULL,NULL),(3,3,'2025-08-10',390000.00,'Efectivo',NULL,NULL),(4,4,'2025-07-20',700000.00,'Efectivo',NULL,NULL),(5,1,'2025-11-01',80000.00,'Tarjeta',NULL,NULL),(6,3,'2025-09-10',390000.00,'Transferencia',NULL,NULL);
/*!40000 ALTER TABLE `pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `rol` enum('Administrador','Entrenador','Cliente') NOT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Miguel','Jaimes','miguel@gmail.com','$2a$10$S.v08P80R9Q7e.j6H5O4A.eG4F3D2C1B0A9Z8Y7X6W5V4U3T2S1R0Q9P8O7N6M5L4K3J2I','3124567890','Administrador'),(2,'Laura','Gómez','laura@gmail.com','$2a$10$wN1Q.7eY2W.K7E/w/H4nEOGe6q.h1O2J3K4L5M6N7O8P9Q0R1S2T3U4V5W6X7Y8Z9A0B1C2D3','3101112233','Entrenador'),(3,'Pedro','López','pedro@gmail.com','$2a$10$tM2N.4dG5J6K7L8M9N0O1P2Q3R4S5T6U7V8W9X0Y1Z2A3B4C5D6E7F8G9H0I1J2K3L4M5N6O7P8','3004445566','Cliente'),(4,'Thomas','Rincon','Thomi11@gmail.com','$2a$10$j8C3xQ3V0S9T2G1U4Y7Z8/E5F4G3H2I1J0K9L8M7N6O5P4Q3R2S1T0U9V8W7X6Y5Z4A3B2C1D0','3205505129','Cliente');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id_usuario` bigint NOT NULL AUTO_INCREMENT,
  `apellido` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `rol` enum('Administrador','Entrenador','Cliente') NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `UK_kfsp0s1tflm1cwlj8idhqsad0` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Alejandro','manuel@gmail.com','Manuel','$2a$10$0O.uUFclLrFOtLBrV5kMJOuwSfJ2DmOSLzPmbLpRRORwA.JkTPrtS','Cliente','3205505129'),(2,'Andsres','mmmml@gmail.com','Miguelito','$2a$10$665.msPDpcCM.HIg0bkm8.vXQcRLszS4Os/8qWbUOAVxEPY9SvD8q','Administrador','3205505129'),(3,'Admin','admin@gmail.com','Admin','$2a$10$nI4EBdiWa5dgM/p70k8A2eJlQD6K3rInRKHoAvtlaPBWS81sLo4dm','Administrador','3205505129'),(4,'Principal','admin@gimnasio.com','Admin','$2a$10$ttzT/5nDdMISuZr3/eCB.ObP1ggV4ktur9moVVbGjUC2Z7MOJ61dC','Administrador','3001234567'),(5,'Principal','admin2@gimnasio.com','Admin2','$2a$10$nOFByjJnbzLefD43pMo8L.Pr2laAxJgoadoknSFEG9hfMRGTMDQsS','Administrador','3001234567'),(6,'Entrenador','entrenador@gimnasio.com','Carlos','$2a$10$wyfKsz0ewcEsLCL1FB8d6uGj6PI6OHowngXRXaOFdQ4QhpkZeWTyy','Entrenador','3009876543'),(7,'Pérez','juan@gmail.com','Juan','$2a$10$7jmh0sG9qBeNy2jUHEha8OMqBl4pJ/ex8epwMj4ePH5C6odTu4J3q','Cliente','3157654321');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-15 21:10:35
