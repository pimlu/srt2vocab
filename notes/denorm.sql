/*drop table if exists pos;
drop table if exists entity;
drop table if exists sense;
drop table if exists gloss;
drop table if exists misc;
drop table if exists k_ele;
drop table if exists r_ele;
drop table if exists entry;
CREATE TABLE pos (id INTEGER PRIMARY KEY, fk INTEGER, entity INTEGER);
CREATE TABLE entity (id INTEGER PRIMARY KEY, entity TEXT, expansion TEXT);
CREATE TABLE sense (id INTEGER PRIMARY KEY, fk INTEGER);
CREATE TABLE gloss (id INTEGER PRIMARY KEY, fk INTEGER,value TEXT);
CREATE TABLE misc (id INTEGER PRIMARY KEY, fk INTEGER, entity INTEGER);
CREATE TABLE k_ele (id INTEGER PRIMARY KEY, fk INTEGER, value TEXT);
CREATE TABLE r_ele (id INTEGER PRIMARY KEY, fk INTEGER, value TEXT);
CREATE TABLE entry (id INTEGER PRIMARY KEY, ent_seq INTEGER);

insert into entry values 
  (1,1337),
  (2,1338),
  (3,1339);

insert into k_ele values
  (223,1, "CYCLING"),
  (224,1, "BIKING"),
  (225,2, "DOKO"),
  (226,3, "NANI?");

insert into r_ele values
  (123,1, "cycling"),
  (124,1, "biking"),
  (125,2, "doko"),
  (126,3, "nani?"),
  (127,3, "nani...?");

insert into sense values
  (5, 1),
  (6, 1),
  (7, 2),
  (8, 3);

insert into gloss values
  (10, 5, "fun stuff"),
  (11, 6, "great stuff"),
  (12, 7, "where"),
  (13, 8, "what?!"),
  (14, 8, "wheh");

insert into entity values
  (1, 'n', 'noun'),
  (2, 'v', 'verb'),
  (3, 'uv', 'unusually verbose'),
  (4, 'ex', 'extinct');
insert into pos values
  (1, 5, 1),
  (2, 5, 2),
  (3, 7, 2),
  (5, 8, 1);
insert into misc values
  (1, 5, 3),
  (2, 5, 4),
  (5, 8, 4);*/
drop table if exists norm_entry;

create table norm_entry as
  select eid, kanji, GROUP_CONCAT(r_ele.value,"@") as reading from 
	(select entry.id as eid, GROUP_CONCAT(k_ele.value,"@") as kanji
	  from entry left join k_ele on entry.id=k_ele.fk
	  group by entry.id)
	left join r_ele on eid=r_ele.fk
	group by eid;
SELECT 'did norm_entry';

drop table if exists temp1;
drop table if exists temp2;
drop table if exists temp3;
drop table if exists temp4;
drop table if exists norm_sense;
create table temp1 as 
  select sense.fk as eid, sense.id as sid, group_concat(gloss.value, '@') as def
	  from sense left join gloss on sid=gloss.fk
	  group by sid;
SELECT 'did temp1';

create table temp2 as 
  select temp1.*, group_concat(entity.entity, '@') as type
	  from (temp1 left join pos as r on sid=r.fk) left join entity on r.entity=entity.id
	  group by sid;
drop table temp1;
SELECT 'did temp2';
create table temp3 as 
  select temp2.*, group_concat(entity.entity, '@') as misc
	  from (temp2 left join misc as r on sid=r.fk) left join entity on r.entity=entity.id
	  group by sid;
drop table temp2;
CREATE INDEX temp3_eid ON temp3 (eid);
SELECT 'did temp3';
create table temp4 as
  select eid,def,
    ifnull(ta3.type, (select tb3.type from temp3 as tb3 where tb3.eid=ta3.eid and tb3.type is not null limit 1)) as pos,
    misc from temp3 as ta3;
drop table temp3;
SELECT 'did temp4';

create table norm_sense as select * from temp4;
drop table temp4;
SELECT 'did norm_sense';
vacuum;


--sqlite3 -header -csv '/home/pimlu/Downloads/norm.db' "select * from norm_sense" > sense.csv 

