create table orders(
    id bigserial primary key,
    status varchar(30),
    order_id varchar(150),
    customer_id bigint,
    event_id varchar(150),
    bet varchar(500),
    odd numeric(15,10),
    amount numeric(20,10),
    event_date_time timestamp,
    card_id bigint
);