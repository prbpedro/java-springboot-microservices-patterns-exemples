CREATE USER 'readonlyuser'@'127.0.0.1' IDENTIFIED BY 'password';
GRANT SELECT, SHOW VIEW ON $dumb_db.* TO 'readonlyuser'@'127.0.0.1' IDENTIFIED BY 'password';
FLUSH PRIVILEGES;