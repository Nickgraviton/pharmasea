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
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
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

LOCK TABLES product WRITE;
/*!40000 ALTER TABLE product DISABLE KEYS */;
INSERT INTO product VALUES
(0,'Depon','Άμεση ανακούφιση από τον πόνο.','Παυσίπονα', '0'),
(1,'Panadol','Αντιμετώπιση πονοκεφάλου σε λίγα λεπτά.','Παυσίπονα', '0'),
(2,'Strepsils','Καραμέλες για τον λαιμό.','Πονόλαιμος', '0'),
(3,'Betadine','Απολυμαίνει κάθε πληγή.','Αντισηπτικά', '0'),
(4,'Nasonex','Απολαύστε κάθε στιγμή', 'Σπρέι', '0'),
(5,'Maalox','Πες αντίο στον στομαχόπονο.','Στομαχόπονος', '0'),
(6,'FaceLift', 'Η καλύτερη κρέμα προσώπου.','Ενυδατική', '0'),
(7,'BodyLift', 'Η καλύτερη κρέμα σώματος.','Ενυδατική', '0'),
(8,'FaceLiftOld', 'Η μέτρια κρέμα προσώπου.','Ενυδατική', '1'),
(9,'BodyLiftOld', 'Η μέτρια κρέμα σώματος.','Ενυδατική', '1'),
(10,'VitaminSea', 'Η βιταμίνη του καλοκαιριού.', 'Βιταμίνες','0'),
(11, 'Otrivin','Ανακουφίζει από τη ρινική συμφώρηση.','Σπρέι', '0'),
(12, 'Ponstan','Στιγμιαίο παυσίπονο', 'Παυσίπονα', '0'),
(13, 'Xozal', 'Αντιισταμινικό σε χάπι', 'Αντιισταμινικά', '0'),
(14, 'Claritine', 'Αντιισταμινικό για παιδιά', 'Αντιισταμινικά', '0'),
(15, 'Centrum Men', 'Πολυβιταμίνες για άντρες', 'Πολυβιταμίνες', '0'),
(16, 'Centrum Women', 'Πολυβιταμίνες για γυναίκες', 'Πολυβιταμίνες', '0'),
(17, 'Tonotil', 'Πολυβιταμίνες για μαθητές', 'Πολυβιταμίνες', '0'),
(18, 'Tonotil Plus', 'Πολυβιταμίνες για φοιτητές', 'Πολυβιταμίνες', '0'),
(19, 'Epressat', 'Πολυβιταμίνες για Γ λυκείου', 'Πολυβιταμίνες', '0'),
(20, 'Centrum Performance', 'Πολυβιταμίνες για αθλητές', 'Πολυβιταμίνες', '0');
/*!40000 ALTER TABLE product ENABLE KEYS */;
UNLOCK TABLES;

DROP TABLE IF EXISTS shop;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE shop (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  address varchar(100) NOT NULL,
  lat decimal(11,7) NOT NULL,
  lng decimal(11,7) NOT NULL,
  withdrawn tinyint(1) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES shop WRITE;
/*!40000 ALTER TABLE shop DISABLE KEYS */;
INSERT INTO shop VALUES
(0, 'Το Φαρμακείο της Γειτονιάς', 'Καραϊσκάκι 34, Αγία Παρασκευή, 15341', 38.004022, 23.815614, 0),
(1, 'Παπαδοπούλου', 'Κρήτης 76, Χαλάνδρι, 15231', 38.009913, 23.801955, 0),
(2, 'Σολωμού', 'Φανερωμένης 28, Περιστέρι, 12133', 38.020362, 23.698726, 0),
(3, 'Συνοικιακό Φαρμακείο', 'Ιπποκράτους 112, Αθήνα, 11472', 37.985662, 23.741597, 0),
(4, 'Παρασκευάς', 'Μαυρομιχάλη 84, Αθήνα, 11472', 37.985319, 23.739279, 0),
(5, 'Αρούκατος Φαρμακείο', 'Πόντου 28, Αργυρούπολη, 16452', 37.903325, 23.750037, 0),
(6, 'Το φαρμακείο της Μπουμπουλίνας', 'Μπουμπουλίνας 50, Πειραιάς, 18535', 37.939815, 23.647922, 0),
(7, 'Αστρικό Φαρμακείο', 'Κολοκοτρώνη 124, Κορυδαλλός, 18121', 37.982488, 23.654968, 0);
/*!40000 ALTER TABLE shop ENABLE KEYS */;
UNLOCK TABLES;

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

LOCK TABLES price WRITE;
/*!40000 ALTER TABLE price DISABLE KEYS */;
INSERT INTO price VALUES
(0, 0, 0, 3.86, '2018-05-20'),
(1, 0, 1, 7.15, '2018-05-20'),
(2, 0, 2, 3.35, '2018-05-20'),
(3, 0, 3, 6.92, '2018-05-20'),
(4, 0, 4, 9.21, '2018-05-20'),
(5, 0, 5, 2.27, '2018-05-20'),
(6, 0, 6, 0.59, '2018-05-20'),
(7, 0, 7, 3.26, '2018-05-20'),
(8, 0, 8, 0.26, '2018-05-20'),
(9, 0, 9, 2.36, '2018-05-20'),
(10, 0, 10, 1.68, '2018-05-20'),
(11, 0, 11, 7.29, '2018-05-20'),
(12, 0, 12, 2.30, '2018-05-20'),
(13, 0, 13, 2.23, '2018-05-20'),
(14, 0, 14, 7.35, '2018-05-20'),
(15, 0, 15, 9.2, '2018-05-20'),
(16, 0, 16, 2.58, '2018-05-20'),
(17, 0, 17, 9.67, '2018-05-20'),
(18, 0, 18, 3.56, '2018-05-20'),
(19, 0, 19, 1.42, '2018-05-20'),
(20, 0, 20, 9.73, '2018-05-20'),
(21, 1, 0, 1.19, '2018-05-20'),
(22, 1, 1, 4.37, '2018-05-20'),
(23, 1, 2, 8.24, '2018-05-20'),
(24, 1, 3, 5.70, '2018-05-20'),
(25, 1, 4, 3.26, '2018-05-20'),
(26, 1, 5, 1.80, '2018-05-20'),
(27, 1, 6, 6.73, '2018-05-20'),
(28, 1, 7, 2.70, '2018-05-20'),
(29, 1, 8, 6.81, '2018-05-20'),
(30, 1, 9, 5.25, '2018-05-20'),
(31, 1, 10, 4.27, '2018-05-20'),
(32, 1, 11, 6.5, '2018-05-20'),
(33, 1, 12, 6.29, '2018-05-20'),
(34, 1, 13, 3.57, '2018-05-20'),
(35, 1, 14, 4.95, '2018-05-20'),
(36, 1, 15, 2.45, '2018-05-20'),
(37, 1, 16, 4.67, '2018-05-20'),
(38, 1, 17, 4.64, '2018-05-20'),
(39, 1, 18, 3.50, '2018-05-20'),
(40, 1, 19, 7.8, '2018-05-20'),
(41, 1, 20, 6.78, '2018-05-20'),
(42, 2, 0, 8.84, '2018-05-20'),
(43, 2, 1, 3.51, '2018-05-20'),
(44, 2, 2, 4.99, '2018-05-20'),
(45, 2, 3, 2.60, '2018-05-20'),
(46, 2, 4, 6.68, '2018-05-20'),
(47, 2, 5, 9.12, '2018-05-20'),
(48, 2, 6, 6.86, '2018-05-20'),
(49, 2, 7, 4.39, '2018-05-20'),
(50, 2, 8, 5.70, '2018-05-20'),
(51, 2, 9, 4.78, '2018-05-20'),
(52, 2, 10, 7.1, '2018-05-20'),
(53, 2, 11, 7.2, '2018-05-20'),
(54, 2, 12, 7.92, '2018-05-20'),
(55, 2, 13, 2.56, '2018-05-20'),
(56, 2, 14, 1.80, '2018-05-20'),
(57, 2, 15, 6.41, '2018-05-20'),
(58, 2, 16, 5.89, '2018-05-20'),
(59, 2, 17, 4.19, '2018-05-20'),
(60, 2, 18, 0.29, '2018-05-20'),
(61, 2, 19, 1.17, '2018-05-20'),
(62, 2, 20, 7.71, '2018-05-20'),
(63, 3, 0, 1.75, '2018-05-20'),
(64, 3, 1, 9.27, '2018-05-20'),
(65, 3, 2, 7.56, '2018-05-20'),
(66, 3, 3, 7.53, '2018-05-20'),
(67, 3, 4, 6.65, '2018-05-20'),
(68, 3, 5, 6.83, '2018-05-20'),
(69, 3, 6, 9.24, '2018-05-20'),
(70, 3, 7, 8.71, '2018-05-20'),
(71, 3, 8, 2.29, '2018-05-20'),
(72, 3, 9, 3.19, '2018-05-20'),
(73, 3, 10, 0.68, '2018-05-20'),
(74, 3, 11, 8.15, '2018-05-20'),
(75, 3, 12, 0.49, '2018-05-20'),
(76, 3, 13, 6.23, '2018-05-20'),
(77, 3, 14, 8.45, '2018-05-20'),
(78, 3, 15, 6.51, '2018-05-20'),
(79, 3, 16, 1.55, '2018-05-20'),
(80, 3, 17, 9.88, '2018-05-20'),
(81, 3, 18, 4.28, '2018-05-20'),
(82, 3, 19, 1.50, '2018-05-20'),
(83, 3, 20, 3.0, '2018-05-20'),
(84, 4, 0, 4.64, '2018-05-20'),
(85, 4, 1, 4.14, '2018-05-20'),
(86, 4, 2, 7.56, '2018-05-20'),
(87, 4, 3, 3.91, '2018-05-20'),
(88, 4, 4, 7.65, '2018-05-20'),
(89, 4, 5, 9.36, '2018-05-20'),
(90, 4, 6, 2.51, '2018-05-20'),
(91, 4, 7, 7.28, '2018-05-20'),
(92, 4, 8, 5.7, '2018-05-20'),
(93, 4, 9, 4.21, '2018-05-20'),
(94, 4, 10, 8.95, '2018-05-20'),
(95, 4, 11, 9.37, '2018-05-20'),
(96, 4, 12, 5.93, '2018-05-20'),
(97, 4, 13, 8.28, '2018-05-20'),
(98, 4, 14, 3.11, '2018-05-20'),
(99, 4, 15, 8.29, '2018-05-20'),
(100, 4, 16, 6.4, '2018-05-20'),
(101, 4, 17, 3.63, '2018-05-20'),
(102, 4, 18, 3.38, '2018-05-20'),
(103, 4, 19, 6.40, '2018-05-20'),
(104, 4, 20, 4.18, '2018-05-20'),
(105, 5, 0, 8.88, '2018-05-20'),
(106, 5, 1, 9.17, '2018-05-20'),
(107, 5, 2, 7.96, '2018-05-20'),
(108, 5, 3, 4.43, '2018-05-20'),
(109, 5, 4, 0.83, '2018-05-20'),
(110, 5, 5, 0.99, '2018-05-20'),
(111, 5, 6, 2.25, '2018-05-20'),
(112, 5, 7, 4.90, '2018-05-20'),
(113, 5, 8, 5.39, '2018-05-20'),
(114, 5, 9, 4.86, '2018-05-20'),
(115, 5, 10, 9.82, '2018-05-20'),
(116, 5, 11, 2.64, '2018-05-20'),
(117, 5, 12, 7.7, '2018-05-20'),
(118, 5, 13, 5.4, '2018-05-20'),
(119, 5, 14, 8.11, '2018-05-20'),
(120, 5, 15, 2.28, '2018-05-20'),
(121, 5, 16, 9.43, '2018-05-20'),
(122, 5, 17, 6.68, '2018-05-20'),
(123, 5, 18, 0.22, '2018-05-20'),
(124, 5, 19, 1.10, '2018-05-20'),
(125, 5, 20, 5.1, '2018-05-20'),
(126, 6, 0, 1.30, '2018-05-20'),
(127, 6, 1, 8.5, '2018-05-20'),
(128, 6, 2, 0.36, '2018-05-20'),
(129, 6, 3, 4.26, '2018-05-20'),
(130, 6, 4, 2.65, '2018-05-20'),
(131, 6, 5, 8.16, '2018-05-20'),
(132, 6, 6, 2.58, '2018-05-20'),
(133, 6, 7, 4.37, '2018-05-20'),
(134, 6, 8, 2.24, '2018-05-20'),
(135, 6, 9, 0.36, '2018-05-20'),
(136, 6, 10, 2.99, '2018-05-20'),
(137, 6, 11, 9.50, '2018-05-20'),
(138, 6, 12, 8.71, '2018-05-20'),
(139, 6, 13, 3.31, '2018-05-20'),
(140, 6, 14, 1.30, '2018-05-20'),
(141, 6, 15, 3.94, '2018-05-20'),
(142, 6, 16, 0.63, '2018-05-20'),
(143, 6, 17, 9.81, '2018-05-20'),
(144, 6, 18, 9.96, '2018-05-20'),
(145, 6, 19, 9.73, '2018-05-20'),
(146, 6, 20, 3.68, '2018-05-20'),
(147, 7, 0, 0.95, '2018-05-20'),
(148, 7, 1, 6.66, '2018-05-20'),
(149, 7, 2, 4.40, '2018-05-20'),
(150, 7, 3, 0.84, '2018-05-20'),
(151, 7, 4, 6.42, '2018-05-20'),
(152, 7, 5, 6.7, '2018-05-20'),
(153, 7, 6, 5.56, '2018-05-20'),
(154, 7, 7, 9.18, '2018-05-20'),
(155, 7, 8, 7.12, '2018-05-20'),
(156, 7, 9, 8.72, '2018-05-20'),
(157, 7, 10, 9.9, '2018-05-20'),
(158, 7, 11, 6.10, '2018-05-20'),
(159, 7, 12, 2.87, '2018-05-20'),
(160, 7, 13, 6.1, '2018-05-20'),
(161, 7, 14, 3.72, '2018-05-20'),
(162, 7, 15, 1.55, '2018-05-20'),
(163, 7, 16, 9.99, '2018-05-20'),
(164, 7, 17, 1.4, '2018-05-20'),
(165, 7, 18, 9.11, '2018-05-20'),
(166, 7, 19, 0.67, '2018-05-20'),
(167, 7, 20, 5.28, '2018-05-20');
/*!40000 ALTER TABLE price ENABLE KEYS */;
UNLOCK TABLES;

DROP TABLE IF EXISTS product_tag;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE product_tag (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES product_tag WRITE;
/*!40000 ALTER TABLE product_tag DISABLE KEYS */;
INSERT INTO product_tag VALUES
(0, 'Βιταμίνες & Συμπληρώματα Διατροφής'),
(1, 'Κρέμες Προσώπου'),
(2, 'Κρέμες Σώματος'),
(3, 'Παυσίπονα'),
(4, 'Παστίλιες'),
(5, 'Αντισηπτικά'),
(6, 'Ρινικό Εκνέφωμα');
/*!40000 ALTER TABLE product_tag ENABLE KEYS */;
UNLOCK TABLES;

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

LOCK TABLES has_product_tag WRITE;
/*!40000 ALTER TABLE has_product_tag DISABLE KEYS */;
INSERT INTO has_product_tag VALUES
(0, 0, 3),
(1, 1, 3),
(2, 2, 4),
(3, 3, 5),
(4, 4, 6),
(5, 5, 3),
(6, 6, 1),
(7, 7, 2),
(8, 8, 1),
(9, 9, 2),
(10, 10, 0),
(11, 11, 6),
(12, 12, 3);
/*!40000 ALTER TABLE has_product_tag ENABLE KEYS */;
UNLOCK TABLES;

DROP TABLE IF EXISTS shop_tag;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE shop_tag (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES shop_tag WRITE;
/*!40000 ALTER TABLE shop_tag DISABLE KEYS */;
INSERT INTO shop_tag VALUES
(0, 'Εκπτώσεις'),
(1, 'Όλη μέρα'),
(2, 'Όλη νύχτα'),
(3, 'Ψευδοεπιστημονικό');
/*!40000 ALTER TABLE shop_tag ENABLE KEYS */;
UNLOCK TABLES;

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

LOCK TABLES has_shop_tag WRITE;
/*!40000 ALTER TABLE has_shop_tag DISABLE KEYS */;
INSERT INTO has_shop_tag VALUES
(0, 0, 0),
(1, 0, 1),
(2, 1, 0),
(3, 1, 2),
(4, 2, 2),
(5, 2, 3);
/*!40000 ALTER TABLE has_shop_tag ENABLE KEYS */;
UNLOCK TABLES;


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
