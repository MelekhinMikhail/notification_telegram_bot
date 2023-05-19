--liquibase formatted sql

--changeset melekhin:1
CREATE TABLE notification_task
(
    id                     BIGINT generated by default as identity primary key,
    message                TEXT      NOT NULL,
    chat_id                BIGINT    NOT NULL,
    notification_date_time TIMESTAMP NOT NULL
);