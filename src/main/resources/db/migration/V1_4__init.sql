CREATE TABLE users (
  id SERIAL PRIMARY KEY ,
  login VARCHAR(32) UNIQUE NOT NULL ,
  email VARCHAR(128) UNIQUE NOT NULL ,
  password VARCHAR(256) NOT NULL ,
  firstName VARCHAR(128) ,
  lastName VARCHAR(128)
);