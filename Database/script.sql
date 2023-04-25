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

---------------------------------Test-------------------------------------- 

insert into UserTable (account, password, online)values ('12011126', '114514');
insert into usertable(account, password) values ('12114514','114514');
insert into usertable(account, password) VALUES ('1528449354','114514');
insert into usertable(account, password) VALUES ('1919810','114514');
insert into usertable(account, password) VALUES ('111111','114514');
insert into usertable(account, password) VALUES ('222222','114514');
insert into usertable(account, password) VALUES ('333333','114514');
insert into usertable(account, password) VALUES ('444444','114514');

insert into userinfo(account, name, sign, avatar) values ('12114514','藤田佳奈','麻将这种运气游戏我可不会输！','1006');
insert into userinfo(account, name, sign, avatar) values ('1528449354','三上千织','和大家打麻将真开心呢！','5006');
insert into userinfo(account, name, sign, avatar) values ('1919810','なでしこ','即使是宝牌，该打的时候也要打。','2011');
insert into userinfo(account, name, sign, avatar) values ('111111','八木唯','自摸，全部交代','6007');
insert into userinfo(account, name, sign, avatar) values ('222222','可爱一姬','dora别跑喵！','3011');
insert into userinfo(account, name, sign, avatar) values ('333333','Nikaidou','真是意外之喜呢，该说托你的福吗，呵呵~','9008');
insert into userinfo(account, name, sign, avatar) values ('444444','九条璃雨','我是九条璃雨，如你所见是个女仆......请不要一直盯着我看。','8009');

insert into friends (useraccount, friendaccount) values ('12011126','12114514');
insert into friends (useraccount, friendaccount) values ('12011126','1528449354');
insert into friends (useraccount, friendaccount) values ('12011126','1919810');
insert into friends (useraccount, friendaccount) values ('12011126','12011126');
insert into friends(useraccount, friendaccount) values ('12114514','12011126');
insert into friends(useraccount, friendaccount) values ('12114514','1919810');
insert into friends(useraccount, friendaccount) values ('12114514','12114514');
insert into friends(useraccount, friendaccount) values ('1919810','12114514');
insert into friends(useraccount, friendaccount) values ('1919810','12011126');
insert into friends(useraccount, friendaccount) values ('1919810','1919810');
insert into friends(useraccount, friendaccount) VALUES ('1528449354','12011126');

insert into friends(useraccount, friendaccount) VALUES ('12011126','111111');
insert into friends(useraccount, friendaccount) VALUES ('12011126','222222');
insert into friends(useraccount, friendaccount) VALUES ('12011126','333333');
insert into friends(useraccount, friendaccount) VALUES ('12011126','444444');
insert into friends(useraccount, friendaccount) VALUES ('111111','12011126');
insert into friends(useraccount, friendaccount) VALUES ('222222','12011126');
insert into friends(useraccount, friendaccount) VALUES ('333333','12011126');
insert into friends(useraccount, friendaccount) VALUES ('444444','12011126');


insert into groupstable(groupnum, groupname) VALUES ('114514','雀友交流会');
insert into groupstable(groupnum, groupname) VALUES ('996007','加班挣猫粮');

insert into groups (groupnum, joiner) values ('114514','12011126');
insert into groups (groupnum, joiner) values ('114514','1919810');
insert into groups (groupnum, joiner) values ('114514','12114514');
insert into groups (groupnum, joiner) values ('114514','1528449354');

insert into groups (groupnum, joiner) values ('996007','12011126');
insert into groups (groupnum, joiner) values ('996007','1919810');
insert into groups (groupnum, joiner) values ('996007','12114514');
insert into groups (groupnum, joiner) values ('996007','1528449354');
insert into groups (groupnum, joiner) values ('996007','111111');
insert into groups (groupnum, joiner) values ('996007','222222');
insert into groups (groupnum, joiner) values ('996007','333333');
insert into groups (groupnum, joiner) values ('996007','444444');


select friendaccount from friends where useraccount = '12011126';
select friendaccount from friends where useraccount = '12011126';
select * from userinfo where account = '12114514';
select online from usertable where account = '12114514';
select avatar from userinfo where account = '1919810';
select groupnum from groups where joiner = '12011126';
select groupname from groupstable where groupnum = '114514';
select joiner from groups where groupnum = '114514';

update usertable set online = false where id > 0;

select count(*) from usertable where account = '666';
delete from usertable where account = '6666'


