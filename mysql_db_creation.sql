DROP TABLE book;
CREATE TABLE book (
    id BIGINT primary key,
    title VARCHAR(500),
    author VARCHAR(500),
    registrationTime DATETIME
);

select * from book;