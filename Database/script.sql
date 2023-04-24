create table if not exists usertable
(
    id       serial,
    account  char(10) not null
        constraint usertable_account_key
            unique,
    password char(20) not null,
    online   boolean default false
);

create table if not exists userinfo
(
    id            serial,
    account       char(10) not null
        constraint userinfo_account_key
            unique,
    name          char(12) not null,
    sign          char(100),
    avatar        integer,
    lastlogintime date
);

create table if not exists friends
(
    id            serial,
    useraccount   char(10) not null,
    friendaccount char(10) not null,
    isuser        boolean default true,
    constraint friends_friendaccount_useraccount_key
        unique (friendaccount, useraccount)
);

create table if not exists groupstable
(
    id        serial,
    groupnum  char(6)  not null
        constraint groupstable_groupnum_key
            unique,
    groupname char(10) not null
);

create table if not exists groups
(
    id       serial,
    groupnum char(6)  not null,
    joiner   char(10) not null,
    constraint groups_groupnum_joiner_key
        unique (groupnum, joiner)
);


