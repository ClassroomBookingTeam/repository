import { ComponentProps, FC } from "react";

import { RoomPicker } from "../components/room-picker";
import { useRooms } from "../hooks/use-rooms";
import { GetRoomsParams } from "../types/api";

interface Props extends Omit<ComponentProps<typeof RoomPicker>, "rooms"> {
  buildingId: string;
  params?: GetRoomsParams;
}

const RoomPickerContainer: FC<Props> = ({ buildingId, params, ...props }) => {
  const { data: rooms = [], isLoading, isError } = useRooms(buildingId, params);

  if (isLoading) {
    return <p>Загрузка списка аудиторий...</p>;
  }

  if (isError) {
    return <p>Не удалось загрузить список аудиторий</p>;
  }

  return <RoomPicker {...props} rooms={rooms} />;
};

export { RoomPickerContainer };
