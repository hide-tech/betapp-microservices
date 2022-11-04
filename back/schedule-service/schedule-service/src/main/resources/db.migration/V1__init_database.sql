create table schedules(
    id bigserial primary key,
    event_id varchar(150),
    type varchar(50),
    date_time timestamp,
    description varchar(5000)
);