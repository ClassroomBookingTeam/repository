import { ComponentProps, FC, useState } from "react";

import { EventsCalendar } from "../components/events-calendar";
import { useCurrentUserEvents } from "../hooks/use-current-user-events";
import { GetEventsParams } from "../types/api";

interface Props extends Omit<ComponentProps<typeof EventsCalendar>, "events"> {
  params?: Omit<GetEventsParams, "date_from" | "date_to">;
}

const CurrentUserEventsCalendarContainer: FC<Props> = ({
  params,
  ...props
}) => {
  const [range, setRange] = useState<[Date, Date]>();

  const { data: events = [] } = useCurrentUserEvents(
    { date_from: range?.[0], date_to: range?.[1], ...params },
    { enabled: !!range },
  );

  return <EventsCalendar onRangeChange={setRange} {...props} events={events} />;
};

export { CurrentUserEventsCalendarContainer };
