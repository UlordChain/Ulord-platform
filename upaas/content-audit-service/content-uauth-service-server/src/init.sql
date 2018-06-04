CREATE SCHEMA `upaas-uauth-server` DEFAULT CHARACTER SET utf8 ;

CREATE TABLE `upaas-uauth-server`.`sensitivewords` (
  `uid` INT NOT NULL AUTO_INCREMENT,
  `keyword` VARCHAR(100) NULL,
  `level` INT NULL,
  `disabled` INT NULL,
  PRIMARY KEY (`uid`));