DROP TABLE IF EXISTS PUBLIC.ITEMS;
DROP TABLE IF EXISTS PUBLIC.USERS;

CREATE TABLE IF NOT EXISTS USERS (
	USER_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	USER_NAME VARCHAR(255) NOT NULL,
	USER_EMAIL VARCHAR(254) UNIQUE NOT NULL,
	CONSTRAINT USERS_PK PRIMARY KEY (USER_ID)
);

CREATE TABLE IF NOT EXISTS ITEMS (
	ITEM_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
	ITEM_NAME VARCHAR(255) NOT NULL,
	DESCRIPTION VARCHAR NOT NULL,
	AVAILABLE BOOLEAN NOT NULL,
	USER_ID BIGINT NOT NULL,
	CONSTRAINT ITEMS_PK PRIMARY KEY (ITEM_ID),
	CONSTRAINT ITEMS_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS BOOKINGS (
    BOOKING_ID BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    START_TIME TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    END_TIME TIMESTAMP WITHOUT TIME ZONE NULL,
    ITEM_ID BIGINT NOT NULL,
    USER_ID BIGINT NOT NULL,
    STATUS VARCHAR(8) NOT NULL,
    CONSTRAINT BOOKINGS_PK PRIMARY KEY (BOOKING_ID);
    CONSTRAINT BOOKINGS_ITEMS_FK FOREIGN KEY (ITEM_ID) REFERENCES ITEMS(ITEM_ID) ON DELETE CASCADE,
    CONSTRAINT BOOKINGS_USERS_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID) ON DELETE CASCADE
)
