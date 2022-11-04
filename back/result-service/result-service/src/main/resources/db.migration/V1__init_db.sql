create table results(
    id bigserial primary key,
    order_id varchar(150),
    customer_id bigint,
    event_id varchar(150),
    bet varchar(500),
    odd numeric(15,10),
    amount numeric(20,10),
    event_date_time timestamp,
    check_time timestamp,
    status varchar(30)
);