# --- !Ups
CREATE TABLE user (

   firstName   varchar(40) ,
    middleName  varchar(40) ,
   lastName    varchar(40) ,
    userName    varchar(40) ,
    password    varchar(20) ,
    mobileNo    varchar(10) ,
    gender      varchar(10) ,
    age         int(11)     ,
    hobbies     varchar(40) ,
    isEnabled  boolean ,
    isAdmin      boolean
);
create table assignment (
id int primary key auto_increment,
title varchar(50),
description varchar(100)
);

# --- !Downs
DROP TABLE user;
DROP TABLE assignment;