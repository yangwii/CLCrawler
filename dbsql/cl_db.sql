create database `cl_db`;

use cl_db;

drop table if exists `techdiscuss`;

CREATE TABLE `techdiscuss` (
  `id` int(10) unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `title` varchar(1000) NOT NULL,
  `url` varchar(1000) NOT NULL,
  `hashCode` varchar(20) NOT NULL,
  `isFetched` tinyint(1) NOT NULL DEFAULT '0'
);

CREATE TABLE `degalflag` (
  `id` int(10) unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `title` varchar(1000) NOT NULL,
  `url` varchar(1000) NOT NULL,
  `hashCode` varchar(20) NOT NULL,
  `isFetched` tinyint(1) NOT NULL DEFAULT '0'
);

CREATE TABLE `doubanbooks` (
	`id` int(10) unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`title` varchar(100) NOT NULL,
	`something` varchar(1000) NOT NULL,
	`link` varchar(100) NOT NULL,
	`rate` float NOT NULL
)