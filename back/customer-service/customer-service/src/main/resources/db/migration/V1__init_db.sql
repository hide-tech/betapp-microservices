create table customers(
    id bigserial primary key,
    name varchar(50),
    surname varchar(50),
    email varchar(127) unique,
    photo_url varchar(1000),
    passport_sheet1_photo_url varchar(1000),
    passport_sheet2_photo_url varchar(1000),
    status varchar(20)
);

create table credit_cards(
    id bigserial primary key,
    name varchar(127),
    card_number varchar(32),
    terminate_date date,
    holder_name varchar(127),
    cvv_number varchar(10)
    customer_id bigint,
    constraint fk_customer_card foreign key (customer_id) references customers(id)
);