DROP DATABASE IF EXISTS validations;

DROP USER IF EXISTS 'validations'@'%';

-- Creates databases
CREATE DATABASE validations;


-- Creates user & grants permission
CREATE USER 'validations'@'%' IDENTIFIED BY 'validations';


-- GRANT START Either this 
GRANT ALL ON *.* TO 'validations'@'%' ;

-- or this

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, RELOAD, PROCESS, REFERENCES, INDEX, ALTER, SHOW DATABASES, CREATE TEMPORARY TABLES, LOCK TABLES, EXECUTE, REPLICATION SLAVE, REPLICATION CLIENT, CREATE VIEW, SHOW VIEW, CREATE ROUTINE, ALTER ROUTINE, CREATE USER, EVENT, TRIGGER ON *.* TO 'validations'@'%' ;
-- GRANT END Either this.

-- Create Tables validations Schema Start --
CREATE TABLE validations.`merchant_payment_request` (
 `id` int NOT NULL AUTO_INCREMENT,
 `endUserID` varchar(100),
 `merchantTransactionReference` varchar(50) NOT NULL,
 `transactionRequest` text DEFAULT NULL,
 `creationDate` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
 PRIMARY KEY (`id`),
UNIQUE KEY (`merchantTransactionReference`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE validations.`users` (
 `id` int NOT NULL AUTO_INCREMENT,
 `endUserID` varchar(100) NOT NULL,
 `email` varchar(100) NOT NULL,
 `phoneNumber` varchar(100) DEFAULT NULL,
 `firstName` varchar(100) NOT NULL,
 `lastName` varchar(100) NOT NULL,
 `creationDate` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
 PRIMARY KEY (`id`),
 UNIQUE KEY (`endUserID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE validations.`validation_rules` (
 `id` int NOT NULL AUTO_INCREMENT,
 `validatorName` varchar(50) NOT NULL,
 `isActive` BOOLEAN NOT NULL,
 `priority` SMALLINT NOT NULL,
 `creationDate` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
 PRIMARY KEY (`id`),
 UNIQUE KEY (`validatorName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE validations.`validation_rules_params` (
 `id` int NOT NULL AUTO_INCREMENT,
 `validatorName` varchar(50) NOT NULL,
 `paramName` varchar(200) NOT NULL,
 `paramValue` varchar(200) NOT NULL,
 `creationDate` timestamp(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
 PRIMARY KEY (`id`),
 FOREIGN KEY (`validatorName`) REFERENCES `validation_rules` (`validatorName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- Create Tables validations Schema End***


