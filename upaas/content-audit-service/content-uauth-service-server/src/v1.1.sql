UPDATE TABLE `upaas-uauth-server`.`sensitivewords` add column `scene` varchar(20) not null default '*';

CREATE TABLE `scenes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `scene` VARCHAR(10) NULL,
  `symbol` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`));
