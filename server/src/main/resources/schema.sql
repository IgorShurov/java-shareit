drop table if exists users cascade;

CREATE TABLE users (
     id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
     name varchar(50) NOT NULL,
     email varchar(50) NOT NULL,
     CONSTRAINT pk_users PRIMARY KEY (ID),
     CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);


drop table if exists requests cascade;
CREATE TABLE requests (
    id 			BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description 		VARCHAR (2000) NOT NULL,
    requester_id 		BIGINT NOT NULL,
    created 		TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT pk_requests PRIMARY KEY (id),
  CONSTRAINT fk_request_requester_id FOREIGN KEY (requester_id) REFERENCES users (id)
);

drop table if exists items cascade;
CREATE TABLE items (
     id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
     name varchar(50) NOT NULL,
     description varchar(200) NOT NULL,
     is_available BOOLEAN,
     user_id bigint NOT NULL,
     request_id bigint,
     CONSTRAINT pk_items PRIMARY KEY (id),
     CONSTRAINT fk_item_user_id FOREIGN KEY (user_id) REFERENCES users (id),
     CONSTRAINT fk_item_request_id FOREIGN KEY (request_id) REFERENCES requests (id)
);

drop table if exists bookings cascade;
CREATE TABLE bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date TIMESTAMP WITHOUT TIME ZONE,
  item_id bigint NOT NULL,
  booker_id bigint NOT NULL,
  status varchar(40) NOT NULL,
  CONSTRAINT pk_bookings PRIMARY KEY (id),
  CONSTRAINT fk_booking_booker_id FOREIGN KEY (booker_id) REFERENCES users (id),
  CONSTRAINT fk_booking_item_id FOREIGN KEY (item_id) REFERENCES items (id)
);

drop table if exists comments cascade;
CREATE TABLE comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text varchar(200) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id bigint NOT NULL,
    author_id bigint NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comment_item_id FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_comment_author_id FOREIGN KEY (author_id) REFERENCES users (id)
);
