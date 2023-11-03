CREATE TABLE Backups (
  -- rowid alias
  id INTEGER,
  name VARCHAR(2083) NOT NULL UNIQUE,
  create_time TIMESTAMP NOT NULL,
  display_name VARCHAR(256) NOT NULL,
  cron_schedule VARCHAR(256) NOT NULL,
  source_dir VARCHAR(4096) NOT NULL,
  destination_dir VARCHAR(4096) NOT NULL,
  config TEXT NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE BackupResults (
  -- rowid alias
  id INTEGER,
  name VARCHAR(2083) NOT NULL UNIQUE,
  parent_name VARCHAR(2083),
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  status VARCHAR(50) NOT NULL,
  output TEXT NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY (parent_name) REFERENCES backups(name) ON DELETE CASCADE
);