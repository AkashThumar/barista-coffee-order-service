CREATE SCHEMA coffee_shop;

ALTER SCHEMA coffee_shop OWNER TO postgres;

COMMENT ON SCHEMA coffee_shop IS 'Barista''s coffee shop business schema';

CREATE TABLE coffee_shop."order" (
    orderid bigint NOT NULL,
    number bigint NOT NULL,
    productname character(30) NOT NULL,
    quantity integer NOT NULL,
    amount bigint NOT NULL,
    status character(10) NOT NULL,
    date timestamp without time zone NOT NULL
);


ALTER TABLE coffee_shop."order" OWNER TO postgres;

CREATE SEQUENCE coffee_shop.order_number_seq
    START WITH 100
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE coffee_shop.order_number_seq OWNER TO postgres;

ALTER TABLE coffee_shop."order" ALTER COLUMN orderid ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME coffee_shop.order_orderid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE coffee_shop.product (
    productid bigint NOT NULL,
    name character(20) NOT NULL,
    amount numeric NOT NULL,
    isactive integer NOT NULL,
    addedby character(30),
    addeddate timestamp without time zone,
    quantity numeric NOT NULL
);


ALTER TABLE coffee_shop.product OWNER TO postgres;

ALTER TABLE coffee_shop.product ALTER COLUMN productid ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME coffee_shop.product_productid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

ALTER TABLE ONLY coffee_shop."order"
    ADD CONSTRAINT order_pkey PRIMARY KEY (orderid);

ALTER TABLE ONLY coffee_shop.product
    ADD CONSTRAINT produt_pkey PRIMARY KEY (productid);

