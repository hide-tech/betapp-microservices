create table odds(
    id bigserial primary key,
    event_id varchar(150),
    ratio numeric(15,10),
    back_ratio numeric(15,10),
    threshold_time timestamp
);