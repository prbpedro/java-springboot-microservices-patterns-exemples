CREATE USER 'readonlyuser'@'%' IDENTIFIED BY 'password';
GRANT SELECT, SHOW VIEW ON *.* TO 'readonlyuser'@'%' IDENTIFIED BY 'password';
FLUSH PRIVILEGES;