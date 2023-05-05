drop table person if exists;

create table person (
	id bigint auto_increment not null primary key,
	name varchar(100)
)