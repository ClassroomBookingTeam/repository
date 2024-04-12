
-- Создание таблиц
CREATE TABLE IF NOT EXISTS ClassroomBooking.Building
(
    "id"                UUID PRIMARY KEY,
    "name"              TEXT NOT NULL,
    "created_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--;;

CREATE TABLE IF NOT EXISTS ClassroomBooking.Room
(
    "id"                UUID PRIMARY KEY,
    "number"            VARCHAR(30) NOT NULL,
    "floor"             SMALLINT NOT NULL,
    "fk_building_id"    UUID NOT NULL REFERENCES ClassroomBooking.Building(id) ON DELETE CASCADE,
    "created_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--;;

CREATE INDEX ON ClassroomBooking.Room USING BTREE (fk_building_id);
--;;

CREATE TABLE IF NOT EXISTS ClassroomBooking.User
(
    "id"                UUID PRIMARY KEY,
    "first_name"        VARCHAR(50) NOT NULL,
    "last_name"         VARCHAR(50) NOT NULL,
    "middle_name"       VARCHAR(50),
    "email"             VARCHAR(50) UNIQUE NOT NULL,
    "password"          TEXT NOT NULL,
    "created_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--;;

CREATE INDEX ON ClassroomBooking.User USING GIN (to_tsvector('russian', email));
--;;

CREATE TABLE IF NOT EXISTS ClassroomBooking.Role
(
    "id"                UUID PRIMARY KEY,
    "name"              TEXT NOT NULL,
    "created_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--;;

CREATE TABLE IF NOT EXISTS ClassroomBooking.Permission
(
    "fk_role_id"        UUID NOT NULL REFERENCES ClassroomBooking.Role(id) ON DELETE CASCADE,
    "fk_user_id"        UUID NOT NULL REFERENCES ClassroomBooking.User(id) ON DELETE CASCADE,
    "created_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY("fk_role_id", "fk_user_id")
);
--;;

CREATE INDEX ON ClassroomBooking.Permission USING BTREE (fk_role_id);
--;;

CREATE INDEX ON ClassroomBooking.Permission USING BTREE (fk_user_id);
--;;

CREATE TABLE IF NOT EXISTS ClassroomBooking.Recurrence
(
    "id"                UUID PRIMARY KEY,
    "name"              TEXT NOT NULL,
    "created_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--;;

CREATE TABLE IF NOT EXISTS ClassroomBooking.Event
(
    "id"                UUID PRIMARY KEY,
    "master_id"         UUID,
    "fk_user_id"        UUID NOT NULL REFERENCES ClassroomBooking.User(id),
    "fk_room_id"        UUID NOT NULL REFERENCES ClassroomBooking.Room(id),
    "fk_recurrence_id"  UUID REFERENCES ClassroomBooking.Recurrence(id),
    "recurrence_until"  TIMESTAMP WITH TIME ZONE,
    "title"             TEXT,
    "description"       TEXT,
    "date_from"         TIMESTAMP NOT NULL,
    "date_to"           TIMESTAMP NOT NULL,
    "created_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "max_appointments"  INT NOT NULL DEFAULT 0
);
--;;

CREATE INDEX ON ClassroomBooking.Event USING BTREE (date_from, date_to);
--;;

CREATE INDEX ON ClassroomBooking.Event USING GIN (to_tsvector('russian', title));
--;;

CREATE TABLE IF NOT EXISTS ClassroomBooking.Appointment
(
    "id"                UUID PRIMARY KEY NOT NULL,
    "fk_event_id"       UUID NOT NULL REFERENCES ClassroomBooking.Event(id) ON DELETE CASCADE,
    "fk_user_id"        UUID NOT NULL REFERENCES ClassroomBooking.User(id) ON DELETE CASCADE,
    "created_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at"        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE("fk_event_id", "fk_user_id")
);
--;;

CREATE INDEX ON ClassroomBooking.Appointment USING BTREE (fk_event_id);
--;;
