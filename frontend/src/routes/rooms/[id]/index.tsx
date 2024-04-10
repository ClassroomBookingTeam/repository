import NiceModal from "@ebay/nice-modal-react";
import { MapPin } from "lucide-react";
import { useParams } from "react-router-dom";

import { SlotInfo } from "@/components/internal/calendar";
import { Button } from "@/components/ui/button";
import { CreateEventModal, EventsCalendar } from "@/features/events";
import { useRoom } from "@/features/rooms";
import { Role, withAuth } from "@/features/users";

const RoomPage = () => {
  const { id } = useParams<{ id: string }>();

  const { data: room, isLoading, isError } = useRoom(id!);

  if (isLoading) {
    return <p>Загрузка аудитории...</p>;
  }

  if (isError) {
    return <p>Не удалось загрузить аудиторию</p>;
  }

  const onSelectSlot = ({ start, end }: SlotInfo) => {
    NiceModal.show(CreateEventModal, {
      roomId: id,
      defaultValues: { date_from: start, date_to: end },
    });
  };

  return (
    <div>
      <header className="mb-5 flex justify-between items-center">
        <div>
          <h1 className="scroll-m-20 text-4xl font-extrabold tracking-tight lg:text-5xl mb-2">
            {room!.number}
          </h1>
          <span className="flex items-center font-semibold text-lg gap-1">
            <MapPin />
            {room!.building.name}
          </span>
        </div>

        <Button onClick={() => onBookClick(id!)}>Забронировать</Button>
      </header>

      <EventsCalendar
        selectable
        onSelectSlot={onSelectSlot}
        params={{ room_id: id }}
      />
    </div>
  );
};

const onBookClick = (roomId: string) => {
  void NiceModal.show(CreateEventModal, { roomId });
};

export default withAuth([Role.Teacher])(RoomPage);
