import * as _ from "lodash-es";
import { ComponentPropsWithoutRef, FC } from "react";

import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

import { Room } from "../types/room";

interface Props extends Omit<ComponentPropsWithoutRef<"div">, "onSelect"> {
  rooms: Room[];
  onSelect?: (room: Room) => unknown;
}

const RoomPicker: FC<Props> = ({ rooms, className, onSelect, ...props }) => {
  const groupedRooms = _.groupBy(rooms, "floor");

  if (rooms.length === 0) {
    return <p>Нет доступных аудиторий</p>;
  }

  return (
    <div className={cn("grid gap-4", className)} {...props}>
      {Object.entries(groupedRooms).map(([floor, rooms]) => (
        <div key={floor}>
          <h2 className="scroll-m-20 text-xl font-semibold tracking-tight">
            {floor} этаж
          </h2>
          <div className="flex gap-2 flex-wrap mt-2">
            {rooms.map((room) => (
              <Button key={room.id} onClick={() => onSelect?.(room)}>
                {room.number}
              </Button>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
};

export { RoomPicker };
