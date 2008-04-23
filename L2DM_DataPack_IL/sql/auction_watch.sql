-- Shilen's Temple Dev Team
CREATE TABLE `auction_watch` (
  `charObjId` int(11) NOT NULL default '0',
  `auctionId` int(11) NOT NULL default '0',
  PRIMARY KEY  (`charObjId`,`auctionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

