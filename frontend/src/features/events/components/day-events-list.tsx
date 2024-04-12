import { format, startOfHour } from "date-fns";
import * as _ from "lodash-es";
import { FC } from "react";

import { Event } from "../types/event";
import { HourEventsList } from "./hour-events-list";

interface Props {
  events: Event[];
}

const DayEventsList: FC<Props> = ({ events }) => {
  const groupedByHour = _.orderBy(
    _.map(
      _.groupBy(events, (event) =>
        format(startOfHour(event.date_from), "HH:mm"),
      ),
      (events, hour) => ({ events, hour }),
    ),
    "hour",
  );

  return (
    <div>
      <h2 className="scroll-m-20 text-2xl font-semibold tracking-tight mb-4">
        {format(events[0]?.date_from, "d MMMM yyyy")}
      </h2>
      <div className="grid gap-4">
        {groupedByHour.map((group) => (
          <HourEventsList key={group.hour} events={group.events} />
        ))}
      </div>
    </div>
  );
};

export { DayEventsList };
