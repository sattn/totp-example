drop table if exists account;
create table if not exists account
(
    user_id varchar(32) primary key,
    password varchar(128) not null,
    email varchar(128) unique,
    secret varchar(128) not null,
    enabled bit(1) not null default false,
    email_verified_at DATETIME default null,
    created_at DATETIME not null
);

drop table if exists account_token;
create table if not exists account_token
(
    token varchar(128) primary key,
    user_id varchar(32) not null,
    expired_at DATETIME default null,
    created_at DATETIME not null
);
