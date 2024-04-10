import { endOfDay, startOfDay } from "date-fns";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { DatePicker } from "@/components/internal/date-picker";
import { DebouncedInput } from "@/components/internal/debounced-input";
import { BuildingSelect } from "@/features/buildings";
import { EventLocationPicker, EventsListContainer } from "@/features/events";
import { LoginModal, Role, useCurrentUser } from "@/features/users";

const HomePage = () => {
  const { data: user, isLoading } = useCurrentUser();

  if (isLoading) {
    return <p>Загрузка...</p>;
  }

  if (!user) {
    return <LoginModal />;
  }

  if (user.roles.includes(Role.Teacher)) {
    return <TeacherHomePage />;
  }

  if (user.roles.includes(Role.Student)) {
    return <StudentHomePage />;
  }
};

const TeacherHomePage = () => {
  const navigate = useNavigate();

  return (
    <EventLocationPicker onSelect={(room) => navigate(`/rooms/${room.id}/`)} />
  );
};

const StudentHomePage = () => {
  const [day, setDay] = useState<Date>();
  const [building, setBuilding] = useState<string>();
  const [search, setSearch] = useState<string>("");

  const params = {
    date_from: day ? startOfDay(day) : startOfDay(new Date()),
    date_to: day ? endOfDay(day) : undefined,
    building_id: building,
    search: search || undefined,
  };

  return (
    <>
      <div className="grid sm:grid-cols-[1fr_2fr_1fr] gap-2 mb-4">
        <BuildingSelect
          placeholder="Корпус"
          value={building}
          onValueChange={setBuilding}
        />
        <DebouncedInput
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Поиск по названию предмета или ФИО преподавателя"
        />
        <DatePicker value={day} onChange={setDay} />
      </div>

      <EventsListContainer params={params} />
    </>
  );
};

export default HomePage;
