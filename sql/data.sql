truncate table categories;

insert into categories (category_name, left_key, right_key, level_category)
values ('Комплектующие', 1, 10, 0),
       ('Процессоры', 2, 7, 1),
       ('Intel', 3, 4, 2),
       ('AMD', 5, 6, 2),
       ('ОЗУ', 8, 9, 1),
       ('Аудиотехника', 11, 20, 0),
       ('Наушники', 12, 17, 3),
       ('С Микрофоном', 13, 14, 4),
       ('Без микрофона', 15, 16, 4),
       ('Колонки', 18, 19, 3);
