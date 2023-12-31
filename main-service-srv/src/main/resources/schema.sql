DROP TABLE IF EXISTS users, events, compilations, categories, requests, locations, compilation_event, comments;

CREATE TABLE IF NOT EXISTS users (
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    email   VARCHAR(512) NOT NULL UNIQUE,
    name    VARCHAR(256) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS categories (
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name    VARCHAR(256) NOT NULL UNIQUE,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS locations (
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat     REAL NOT NULL,
    lon     REAL NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events (
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation          VARCHAR(2000) NOT NULL,
    created_on          TIMESTAMP NOT NULL,
    description         VARCHAR(7000) NOT NULL,
    event_date          TIMESTAMP NOT NULL,
    paid                BOOLEAN,
    participant_limit   INTEGER,
    published_on        TIMESTAMP,
    request_moderation  BOOLEAN NOT NULL,
    state               VARCHAR(256) NOT NULL,
    title               VARCHAR(150) NOT NULL,
    category_id         BIGINT REFERENCES categories (id),
    initiator_id        BIGINT REFERENCES users (id) ON DELETE CASCADE,
    location_id         BIGINT REFERENCES locations (id),
    CONSTRAINT pk_event PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS requests (
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created             TIMESTAMP NOT NULL,
    status              VARCHAR(256) NOT NULL,
    event_id            BIGINT REFERENCES events (id) ON DELETE CASCADE,
    requester_id        BIGINT REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned              BOOLEAN NOT NULL,
    title               VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT pk_compilation PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS compilation_event (
    compilation_id      BIGINT REFERENCES compilations (id) ON DELETE CASCADE,
    event_id            BIGINT REFERENCES events (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text                VARCHAR(1500) NOT NULL,
    created             TIMESTAMP NOT NULL,
    updated_time        TIMESTAMP,
    commentator_id      BIGINT REFERENCES users (id) ON DELETE CASCADE,
    event_id            BIGINT REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);