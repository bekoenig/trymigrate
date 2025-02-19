create table example_entity1
(
    entity1_id char(36) not null
        constraint example_entity1_pk
            primary key,
    attribute1 varchar(250)
);