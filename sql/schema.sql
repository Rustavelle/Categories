create table categories
(
    id             bigint unsigned auto_increment,
    category_name  varchar(100)     not null,
    left_key       int              not null,
    right_key      int              not null,
    level_category tinyint unsigned not null,
    primary key (id)
);
