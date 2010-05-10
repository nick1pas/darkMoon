CREATE TABLE IF NOT EXISTS item_attributes (
  itemId INT(11) NOT NULL DEFAULT 0,
  augAttributes INT(11) NOT NULL DEFAULT -1,
  augSkillId INT(11) NOT NULL DEFAULT -1,
  augSkillLevel INT(11) NOT NULL DEFAULT -1,
  elemType TINYINT(1) NOT NULL DEFAULT -1,
  elemValue INT(11) NOT NULL DEFAULT -1,
  PRIMARY KEY (itemId)
) DEFAULT CHARSET=utf8;