-- MySQL dump 10.13  Distrib 5.7.22, for Linux (x86_64)
--
-- Host: localhost    Database: drugs
-- ------------------------------------------------------
-- Server version	5.7.22-0ubuntu18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40014 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO'*/;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP TABLE IF EXISTS token;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE token (
  id int(11) NOT NULL AUTO_INCREMENT,
  role varchar(60) NOT NULL,
  token varchar(255) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS user;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE user (
  id int(11) NOT NULL AUTO_INCREMENT,
  username varchar(60) NOT NULL,
  LName varchar(45) NOT NULL,
  FName varchar(45) NOT NULL,
  Email varchar(60) NOT NULL,
  Password varchar(255) NOT NULL,
  role varchar(45) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES user WRITE;
/*!40000 ALTER TABLE user DISABLE KEYS */;
INSERT INTO user VALUES
(0, 'seimen42', 'Seimenoulidis', 'Miliompouras', 'seim@gmail.com','$2a$10$R.f/VlbnU04JkNdiqrEXQ.I59966M0bNBhDZeySeO3DFMCWZ.3iOm', 'admin'),
(1, 'tom', 'Waits','Tom','wait@gmail.com', '$2a$10$AcM1ARLlLieWBz9nDIt1Muhzbq8CYYKtbvrF6wPlZMLgSWzkndpQ.', 'user'),
(2, 'clive', 'Barker','Clive','bark@hotmail.com', '$2a$10$1vb.jBKcrAaPXcFbcNcK3OLYWMAjU8uW0uxfprr0UW0PLkLifz9Yi', 'user'),
(3, 'clark', 'Clark','Arthur','clark@yahoo.com', '$2a$10$UciNysO7GD8OJuFVudOKRe0nS8QFouR7gKx7EBpyyj7U5jYE5OuJ2', 'user'),
(4, 'smith', 'Smith','John', 'smith@hotmail.com', '$2a$10$cPEWWhfKjc5DLUebwsLhre3Dw3NPen3.bYzsYIDyF99cc8ykLy1nG', 'user'),
(5, 'alice', 'Merton','Alice', 'merton@gmail.com', '$2a$10$Ts2SWO9uI/ZAW/13IWtacOwIIpODQ3uCrghPOJAye2ekb5bzSbhwW', 'user'),
(6, 'admin', 'admin', 'admin', 'admin@admin.com', '$2a$10$bUVaIAV8H4lVVz/rbUWS0elxwzolHq7eB.p28EYtuIF/uck3e.p72', 'admin');
/*!40000 ALTER TABLE user ENABLE KEYS */;
UNLOCK TABLES;

DROP TABLE IF EXISTS product;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE product (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(40) NOT NULL,
  description varchar(500) NOT NULL,
  category varchar(45) NOT NULL,
  withdrawn tinyint(1) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;



DROP TABLE IF EXISTS shop;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE shop (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  address varchar(100) NOT NULL,
  lng decimal(11,7) NOT NULL,
  lat decimal(11,7) NOT NULL,
  withdrawn tinyint(1) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;



DROP TABLE IF EXISTS price;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE price (
  id int(11) NOT NULL AUTO_INCREMENT,
  S_id int(11) NOT NULL,
  P_id int(11) NOT NULL,
  price decimal(4,2) NOT NULL,
  date datetime NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (S_id) REFERENCES shop(id) ON DELETE CASCADE,
  FOREIGN KEY (P_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;



DROP TABLE IF EXISTS product_tag;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE product_tag (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS has_product_tag;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE has_product_tag (
  id int(11) NOT NULL AUTO_INCREMENT,
  P_id int(11) NOT NULL,
  T_id int(11) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (T_id) REFERENCES product_tag(id) ON DELETE CASCADE,
  FOREIGN KEY (P_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;



DROP TABLE IF EXISTS shop_tag;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE shop_tag (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS has_shop_tag;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE has_shop_tag (
  id int(11) NOT NULL AUTO_INCREMENT,
  S_id int(11) NOT NULL,
  T_id int(11) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (T_id) REFERENCES shop_tag(id) ON DELETE CASCADE,
  FOREIGN KEY (S_id) REFERENCES shop(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

ALTER TABLE user AUTO_INCREMENT=0;
ALTER TABLE token AUTO_INCREMENT=0;
ALTER TABLE product AUTO_INCREMENT=0;
ALTER TABLE shop AUTO_INCREMENT=0;
ALTER TABLE price AUTO_INCREMENT=0;
ALTER TABLE product_tag AUTO_INCREMENT=0;
ALTER TABLE shop_tag AUTO_INCREMENT=0;
ALTER TABLE has_product_tag AUTO_INCREMENT=0;
ALTER TABLE has_shop_tag AUTO_INCREMENT=0;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40014 SET SQL_MODE=@OLD_SQL_MODE*/;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
