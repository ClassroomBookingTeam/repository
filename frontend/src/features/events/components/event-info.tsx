import * as _ from "lodash-es";
import { FC } from "react";

import { Badge } from "@/components/ui/badge";

import { Event } from "../types/event";

interface Props {
  event: Event;
}

const EventInfo: FC<Props> = ({ event }) => {
  return (
    <div className="flex flex-wrap gap-2">
      <Badge>{event.room.number}</Badge>
      <Badge>{event.user.short_name}</Badge>
      <Badge variant="outline">
        Записей: {event.appointments_count}
        {_.isNumber(event.max_appointments) && <>/{event.max_appointments}</>}
      </Badge>
    </div>
  );
};

export { EventInfo };
