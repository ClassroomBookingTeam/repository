import os
from transliterate import translit
from datetime import datetime, timedelta
import uuid


def get_parse_events(directory):
    events = []
    for filename in os.listdir(directory):
        if filename.endswith(".ics"):
            with open(os.path.join(directory, filename), "r") as f:
                ics_data = f.readlines()

            current_event = {}
            is_location = 0
            is_description = 0
            additional_users = []

            for line in ics_data:
                line = line.strip()

                if line.startswith("BEGIN:VEVENT"):
                    current_event = {}

                elif line.startswith("SUMMARY:"):
                    current_event["SUMMARY"] = line[len("SUMMARY:") :]

                elif line.startswith("DTSTART:"):
                    current_event["DTSTART"] = line[len("DTSTART:") :]

                elif line.startswith("DTEND:"):
                    current_event["DTEND"] = line[len("DTEND:") :]

                elif line.startswith("DESCRIPTION:"):
                    current_event["DESCRIPTION"] = line[len("DESCRIPTION:") + 1 :]
                    if len(current_event["DESCRIPTION"]) == 0:
                        continue
                    is_description = 1
                    users = current_event["DESCRIPTION"].split()
                    current_event["DESCRIPTION"] = " ".join(
                        [users[0], users[1], users[2]]
                    )
                    if len(users) > 3:
                        for i in range(3, len(users), 3):
                            additional_users.append(
                                " ".join([users[i], users[i + 1], users[i + 2]])
                            )

                elif line.startswith("LOCATION:"):
                    location = line[len("LOCATION:") :]
                    if "каф." in location:
                        continue
                    is_location = 1
                    current_event["LOCATION"] = location

                elif line.startswith("RRULE:"):
                    rrule_data = {}
                    parts = line[len("RRULE:") :].split(";")
                    for part in parts:
                        key, value = part.split("=")
                        rrule_data[key] = value
                    current_event["RRULE"] = rrule_data

                elif line.startswith("END:VEVENT"):
                    if (
                        is_description == 1
                        and is_location == 1
                        and current_event not in events  # проверка на лекции
                    ):
                        events.append(current_event)
                        for user in additional_users:
                            current_event["DESCRIPTION"] = user
                            if current_event not in events:
                                events.append(current_event)
                    is_location = 0
                    is_description = 0
                    additional_users = []
    return events


building_ent = {}


def get_buildings():
    buildings = ["УЛК", "ГЗ", "Э", "СМ", "МТ"]
    values = []
    for building in buildings:
        building_uuid = str(uuid.uuid4())
        building_ent[building] = building_uuid
        values.append(f"('{building_uuid}', '{building}')")
    format_string = ",\n\t".join(values)
    query = """
INSERT INTO ClassroomBooking.Building (id, name)
VALUES
\t{};
            """.format(
        format_string
    )
    return query


room_ent = {}


def get_rooms(parse_events):
    unique_locations = set()

    for event in parse_events:
        unique_locations.add(event["LOCATION"])

    values = []
    for location in unique_locations:
        building = ""
        char = location[-1]
        if char == "л":
            building = "УЛК"
        elif char == "э":
            building = "Э"
        elif char == "м":
            building = "СМ"
        elif char == "т":
            building = "МТ"
        else:
            building = "ГЗ"

        floor = location[0]

        try:
            if location[3].isdigit():
                floor = location[0:2]
        except IndexError:
            pass

        room_uuid = str(uuid.uuid4())
        room_ent[location] = room_uuid
        building_uuid = building_ent[building]
        values.append(f"('{room_uuid}', '{location}', {floor}, '{building_uuid}')")
    format_string = ",\n\t".join(values)
    query = """
INSERT INTO ClassroomBooking.Room (id, number, floor, fk_building_id)
VALUES
\t{};
            """.format(
        format_string
    )

    return query


role_ent = {}


def get_roles():
    roles = ["teacher", "student"]
    values = []
    for role in roles:
        role_uuid = str(uuid.uuid4())
        role_ent[role] = role_uuid
        values.append(f"('{role_uuid}', '{role}')")

    format_string = ",\n\t".join(values)
    query = """
INSERT INTO ClassroomBooking.Role (id, name)
VALUES
\t{};
            """.format(
        format_string
    )
    return query


user_ent = {}


def get_users_permissions(parse_events):
    unique_users = set()

    for event in parse_events:
        unique_users.add(event["DESCRIPTION"])

    users_values = []
    permissions_values = []
    for user in unique_users:
        email = (
            translit(user, "ru", reversed=True)
            .replace("'", "")
            .replace(" ", "")
            .replace(".", "")
            .lower()
            + "@bmstu.ru"
        )
        user_data = user.split()

        user_uuid = str(uuid.uuid4())
        user_ent[email] = user_uuid
        users_values.append(
            f"('{user_uuid}', '{user_data[1]}', '{user_data[0]}', '{user_data[2]}', '{email}', 'bcrypt+sha512$b83e51abb020ab7f859de0a2e20397d0$12$58a4bd33ceabdb6961adebc31535319fbd74b9216e3cfe4a')"
        )

        role_uuid = role_ent["teacher"]
        permissions_values.append(f"('{role_uuid}', '{user_uuid}')")

    format_users = ",\n\t".join(users_values)
    format_permissions = ",\n\t".join(permissions_values)

    users_query = """
INSERT INTO ClassroomBooking.User (id, first_name, last_name, middle_name, email, password)
VALUES
\t{};
            """.format(
        format_users
    )

    permissions_query = """
INSERT INTO ClassroomBooking.Permission (fk_role_id, fk_user_id)
VALUES
\t{};
            """.format(
        format_permissions
    )

    return [users_query, permissions_query]


recurrence_ent = {}


def get_recurrences():
    recurrences = ["Каждый день", "Каждую неделю", "Каждые 2 недели", "Каждый месяц"]
    values = []
    for rec in recurrences:
        rec_uuid = str(uuid.uuid4())
        recurrence_ent[rec] = rec_uuid
        values.append(f"('{rec_uuid}', '{rec}')")

    format_string = ",\n\t".join(values)
    query = """
INSERT INTO ClassroomBooking.Recurrence (id, name)
VALUES
\t{};
            """.format(
        format_string
    )
    return query


def get_events(parse_events):
    values = []
    for event in parse_events:
        email = (
            translit(event["DESCRIPTION"], "ru", reversed=True)
            .replace("'", "")
            .replace(" ", "")
            .replace(".", "")
            .lower()
            + "@bmstu.ru"
        )
        number = event["LOCATION"]

        rec_name = ""
        interval = event["RRULE"]["INTERVAL"]
        if interval == "1":
            rec_name = "Каждую неделю"
        elif interval == "2":
            rec_name = "Каждые 2 недели"
        title = event["SUMMARY"]
        date_from = event["DTSTART"]
        date_to = event["DTEND"]
        rec_until = event["RRULE"]["UNTIL"]
        master_uuid = str(uuid.uuid4())

        user_uuid = user_ent[email]
        room_uuid = room_ent[number]
        rec_uuid = recurrence_ent[rec_name]

        values.append(
            f"('{master_uuid}', '{user_uuid}', '{room_uuid}', '{rec_uuid}', null, '{title}', '{date_from}', '{date_to}', '{rec_until}')"
        )

        current_date_from = datetime(
            int(date_from[:4]),
            int(date_from[4:6]),
            int(date_from[6:8]),
            hour=int(date_from[9:11]),
            minute=int(date_from[11:13]),
            second=int(date_from[13:15]),
        )
        current_date_to = datetime(
            int(date_to[:4]),
            int(date_to[4:6]),
            int(date_to[6:8]),
            hour=int(date_to[9:11]),
            minute=int(date_to[11:13]),
            second=int(date_to[13:15]),
        )
        rec_date = datetime(
            int(rec_until[:4]),
            int(rec_until[4:6]),
            int(rec_until[6:8]),
        )

        date_interval = timedelta(weeks=int(interval))
        while current_date_from + date_interval <= rec_date:
            current_date_from += date_interval
            current_date_to += date_interval
            event_uuid = str(uuid.uuid4())
            values.append(
                f"('{event_uuid}', '{user_uuid}', '{room_uuid}', '{rec_uuid}', '{master_uuid}', '{title}', '{current_date_from}', '{current_date_to}', '{rec_until}')"
            )

    format_string = ",\n\t".join(values)
    query = """
INSERT INTO ClassroomBooking.Event (id, fk_user_id, fk_room_id, fk_recurrence_id, master_id, title, date_from, date_to, recurrence_until)
VALUES
\t{};
            """.format(
        format_string
    )
    return query


"""
    TODO: Надо бы в ООП обернуть и норм вид сделать...
"""

parse_events = get_parse_events("../docs/ics_files/")
res_directory = "../docs/sql/"
if not os.path.exists(res_directory):
    os.makedirs(res_directory)
with open("../docs/sql/data.sql", "w") as f:
    print(get_buildings(), file=f)
    print(get_rooms(parse_events), file=f)
    print(get_roles(), file=f)
    users_perm = get_users_permissions(parse_events)
    print(users_perm[0], file=f)
    print(users_perm[1], file=f)
    print(get_recurrences(), file=f)
    print(get_events(parse_events), file=f)
