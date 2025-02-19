create table EXAMPLE_ENTITY1
(
    ENTITY1_ID CHARACTER(36) not null
        constraint EXAMPLE_ENTITY1_PK
            primary key,
    ATTRIBUTE1 VARCHAR(250),
    ATTRIBUTE2 INTEGER       not null
);