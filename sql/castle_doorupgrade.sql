-- --------------------------------------
-- Table structure for castle_doorupgrade
-- --------------------------------------
CREATE TABLE IF NOT EXISTS castle_doorupgrade (
  `doorId` int(11) NOT NULL default '0',
  `hp` int(11) NOT NULL default '0',
  `pDef` int(11) NOT NULL default '0',
  `mDef` int(11) NOT NULL default '0',
  PRIMARY KEY  (`doorId`)
) DEFAULT CHARSET=utf8;

-- L2Emu Project