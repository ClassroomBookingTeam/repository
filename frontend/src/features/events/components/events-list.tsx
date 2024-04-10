import { format, startOfDay } from "date-fns";
import * as _ from "lodash-es";
import { FC } from "react";

import { Event } from "../types/event";
import { DayEventsList } from "./day-events-list";

interface Props {
  events: Event[];
}

const EventsList: FC<Props> = ({ events }) => {
  if (events.length === 0) {
    return <p>Нет событий</p>;
  }

  const groupedByDay = _.orderBy(
    _.map(
      _.groupBy(events, (event) =>
        format(startOfDay(event.date_from), "yyyy-MM-dd"),
      ),
      (events, day) => ({ events, day }),
    ),
    "day",
  );

  return (
    <div className="grid gap-10">
      {groupedByDay.map((group) => (
        <DayEventsList events={group.events} key={group.day} />
      ))}
    </div>
  );
};

export { EventsList };
