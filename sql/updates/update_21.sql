--
-- Table structure for table `custom_merchant_shopids`
--
DROP TABLE IF EXISTS custom_merchant_shopids;
CREATE TABLE custom_merchant_shopids (
  shop_id decimal(9,0) NOT NULL default '0',
  npc_id varchar(9) default NULL,
  PRIMARY KEY  (shop_id)
) DEFAULT CHARSET=utf8;

--
-- Dumping data for table `custom_merchant_shopids`
--

--  L2Emu Project



--
-- Table structure for table `custom_merchant_buylists`
--
DROP TABLE IF EXISTS custom_merchant_buylists;
CREATE TABLE custom_merchant_buylists (
  `item_id` decimal(9,0) NOT NULL default '0',
  `price` decimal(11,0) NOT NULL default '0',
  `shop_id` decimal(9,0) NOT NULL default '0',
  `order` decimal(4,0) NOT NULL default '0',
  `count` INT( 11 ) NOT NULL DEFAULT '-1',
  `currentCount` INT( 11 ) NOT NULL DEFAULT '-1',
  `time` INT NOT NULL DEFAULT '0',
  savetimer DECIMAL(20,0) NOT NULL DEFAULT '0',
  PRIMARY KEY  (shop_id,`order`)
) DEFAULT CHARSET=utf8;

--
-- Dumping data for table `custom_merchant_buylists`
--

--  L2Emu Project