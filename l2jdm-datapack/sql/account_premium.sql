CREATE TABLE `account_premium` (
  `account_name` varchar(45) NOT NULL,
  `premium_services` int(1) NOT NULL,
  `premium_expires` decimal(20,0) NOT NULL,
  PRIMARY KEY (`account_name`)
);
