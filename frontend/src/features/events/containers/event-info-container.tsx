import { ComponentProps, FC } from "react";

import { EventInfo } from "../components/event-info";
import { useEvent } from "../hooks/use-event";

interface Props extends Omit<ComponentProps<typeof EventInfo>, "event"> {
  eventId: string;
}

const EventInfoContainer: FC<Props> = ({ eventId }) => {
  const { data: event, isLoading, isError } = useEvent(eventId);

  if (isLoading) {
    return <p>Загрузка события...</p>;
  }

  if (isError) {
    return <p>Не удалось загрузить событие</p>;
  }

  return <EventInfo event={event!} />;
};

export { EventInfoContainer };
