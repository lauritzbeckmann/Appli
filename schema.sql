--
-- Table structure for table `url_mapping`
--
DROP TABLE IF EXISTS `url_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `url_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `full_url` varchar(2048) NOT NULL,
  `alias` varchar(255) NOT NULL UNIQUE,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_alias` (`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `click_event`
--

DROP TABLE IF EXISTS `click_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `click_event` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `url_id` bigint NOT NULL,
  `clicked_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_url_id` (`url_id`),
  CONSTRAINT `fk_url_id` FOREIGN KEY (`url_id`) REFERENCES `url_mapping` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
