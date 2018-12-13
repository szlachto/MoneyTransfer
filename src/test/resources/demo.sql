DROP TABLE IF EXISTS Customer;

CREATE TABLE Customer (CustomerId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
 CustomerName VARCHAR(30) NOT NULL);

CREATE UNIQUE INDEX idx_c on Customer(CustomerName);

INSERT INTO Customer (CustomerName) VALUES ('CaptainMarvel');
INSERT INTO Customer (CustomerName) VALUES ('SteveRogers');
INSERT INTO Customer (CustomerName) VALUES ('TonyStark');

DROP TABLE IF EXISTS Account;

CREATE TABLE Account (AccountId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
CustomerName VARCHAR(30),
Balance DECIMAL(19,4),
CurrencyCode VARCHAR(30)
);

CREATE UNIQUE INDEX idx_a on Account(CustomerName,CurrencyCode);

INSERT INTO Account (CustomerName,Balance,CurrencyCode) VALUES ('CaptainMarvel',400,'USD');
INSERT INTO Account (CustomerName,Balance,CurrencyCode) VALUES ('CaptainMarvel',450,'EUR');
INSERT INTO Account (CustomerName,Balance,CurrencyCode) VALUES ('SteveRogers',500,'USD');
INSERT INTO Account (CustomerName,Balance,CurrencyCode) VALUES ('SteveRogers',500,'EUR');
INSERT INTO Account (CustomerName,Balance,CurrencyCode) VALUES ('TonyStark',100,'USD');
INSERT INTO Account (CustomerName,Balance,CurrencyCode) VALUES ('TonyStark',150,'EUR');
