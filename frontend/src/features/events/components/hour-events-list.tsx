import { format, startOfHour } from "date-fns";
import { FC } from "react";

import { Event } from "../types/event";
import { EventCard } from "./event-card";

interface Props {
  events: Event[];
}

const HourEventsList: FC<Props> = ({ events }) => {
  return (
    <div className="grid gap-2 sm:flex sm:items-start sm:gap-0">
      <span className="flex-shrink-0 w-[70px] scroll-m-20 text-xl font-semibold tracking-tight">
        {format(startOfHour(events[0]?.date_from), "H:mm")}
      </span>
      <div className="grid gap-2 grow">
        {events.map((event) => (
          <EventCard event={event} key={event.id} />
        ))}
      </div>
    </div>
  );
};

export { HourEventsList };
