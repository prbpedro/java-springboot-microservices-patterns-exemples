CREATE TABLE dumb_entity (
  id int(11) NOT NULL AUTO_INCREMENT,
  value int(11),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE dumb_entity_transaction_outbox (
  id int(11) NOT NULL AUTO_INCREMENT,
  dumb_entity_id int(11) NOT NULL,
  message_body JSON,
  message_attributes JSON,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  status varchar(20) NOT NULL DEFAULT 'PENDING',
  operation varchar(100) NOT NULL ,
  generated_uuid varchar(200) NOT NULL ,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;