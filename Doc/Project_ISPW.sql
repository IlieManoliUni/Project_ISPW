CREATE DATABASE  IF NOT EXISTS `ispw_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `ispw_db`;
-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ispw_db
-- ------------------------------------------------------
-- Server version	8.0.35

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
-- Table structure for table `anime`
--

DROP TABLE IF EXISTS `anime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `anime` (
  `idAniList` int NOT NULL,
  `episodes` int DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`idAniList`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `anime`
--

LOCK TABLES `anime` WRITE;
/*!40000 ALTER TABLE `anime` DISABLE KEYS */;
INSERT INTO `anime` VALUES (20,220,23,'NARUTO'),(21,0,0,'ONE PIECE'),(101,24,22,'Attack on Titan'),(3480,1,80,'Nayuta'),(10075,1,6,'NARUTO×UT');
/*!40000 ALTER TABLE `anime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `list`
--

DROP TABLE IF EXISTS `list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `list` (
  `idList` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idList`),
  KEY `username` (`username`),
  CONSTRAINT `list_ibfk_1` FOREIGN KEY (`username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `list`
--

LOCK TABLES `list` WRITE;
/*!40000 ALTER TABLE `list` DISABLE KEYS */;
INSERT INTO `list` VALUES (1,'Anime List','testUser'),(8,'1','ilie'),(9,'1','123'),(11,'1','prova3');
/*!40000 ALTER TABLE `list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `list_anime`
--

DROP TABLE IF EXISTS `list_anime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `list_anime` (
  `idList` int NOT NULL,
  `idAniList` int NOT NULL,
  PRIMARY KEY (`idList`,`idAniList`),
  KEY `idAniList` (`idAniList`),
  CONSTRAINT `list_anime_ibfk_1` FOREIGN KEY (`idList`) REFERENCES `list` (`idList`),
  CONSTRAINT `list_anime_ibfk_2` FOREIGN KEY (`idAniList`) REFERENCES `anime` (`idAniList`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `list_anime`
--

LOCK TABLES `list_anime` WRITE;
/*!40000 ALTER TABLE `list_anime` DISABLE KEYS */;
INSERT INTO `list_anime` VALUES (8,20),(8,3480);
/*!40000 ALTER TABLE `list_anime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `list_movie`
--

DROP TABLE IF EXISTS `list_movie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `list_movie` (
  `idList` int NOT NULL,
  `idMovieTmdb` int NOT NULL,
  PRIMARY KEY (`idList`,`idMovieTmdb`),
  KEY `idMovieTmdb` (`idMovieTmdb`),
  CONSTRAINT `list_movie_ibfk_1` FOREIGN KEY (`idList`) REFERENCES `list` (`idList`),
  CONSTRAINT `list_movie_ibfk_2` FOREIGN KEY (`idMovieTmdb`) REFERENCES `movie` (`idMovieTmdb`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `list_movie`
--

LOCK TABLES `list_movie` WRITE;
/*!40000 ALTER TABLE `list_movie` DISABLE KEYS */;
INSERT INTO `list_movie` VALUES (8,27205),(8,1339709);
/*!40000 ALTER TABLE `list_movie` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `list_tvseries`
--

DROP TABLE IF EXISTS `list_tvseries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `list_tvseries` (
  `idList` int NOT NULL,
  `idTvSeriesTmdb` int NOT NULL,
  PRIMARY KEY (`idList`,`idTvSeriesTmdb`),
  KEY `idTvSeriesTmdb` (`idTvSeriesTmdb`),
  CONSTRAINT `list_tvseries_ibfk_1` FOREIGN KEY (`idList`) REFERENCES `list` (`idList`),
  CONSTRAINT `list_tvseries_ibfk_2` FOREIGN KEY (`idTvSeriesTmdb`) REFERENCES `tvseries` (`idTvSeriesTmdb`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `list_tvseries`
--

LOCK TABLES `list_tvseries` WRITE;
/*!40000 ALTER TABLE `list_tvseries` DISABLE KEYS */;
INSERT INTO `list_tvseries` VALUES (8,87692),(8,229195);
/*!40000 ALTER TABLE `list_tvseries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movie`
--

DROP TABLE IF EXISTS `movie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie` (
  `idMovieTmdb` int NOT NULL,
  `runtime` int DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`idMovieTmdb`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie`
--

LOCK TABLES `movie` WRITE;
/*!40000 ALTER TABLE `movie` DISABLE KEYS */;
INSERT INTO `movie` VALUES (201,120,'Inception'),(11483,124,'Pirates'),(27205,148,'Inception'),(1068126,50,'The True Story of Pirates'),(1339709,28,'Like a Spiral');
/*!40000 ALTER TABLE `movie` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tvseries`
--

DROP TABLE IF EXISTS `tvseries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tvseries` (
  `idTvSeriesTmdb` int NOT NULL,
  `numberOfEpisodes` int DEFAULT NULL,
  `episodeRuntime` int DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`idTvSeriesTmdb`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tvseries`
--

LOCK TABLES `tvseries` WRITE;
/*!40000 ALTER TABLE `tvseries` DISABLE KEYS */;
INSERT INTO `tvseries` VALUES (1,10,45,'Stranger Things'),(2,10,45,'Stranger Things'),(3,10,45,'Stranger Things'),(4,10,45,'Stranger Things'),(6,10,45,'Stranger Things'),(7,10,45,'Stranger Things'),(10,10,45,'Stranger Things'),(44217,89,44,'Vikings'),(87692,12,23,'Amazing Stranger'),(93511,48,0,'Green Porcelain'),(229195,3,0,'The Vikings');
/*!40000 ALTER TABLE `tvseries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `username` varchar(50) NOT NULL,
  `password` varchar(8) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('123','123'),('fabio','fabio'),('hed','sad'),('ilie','psw'),('prova','prova'),('prova2','des'),('prova3','psw'),('prova4','psw'),('saf','saf'),('testUser','password');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-04 13:36:27
