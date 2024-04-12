import NiceModal from "@ebay/nice-modal-react";
import { ComponentProps, FC, useCallback } from "react";

import { Calendar as CalendarComponent } from "@/components/internal/calendar";
import { useCurrentUser } from "@/features/users";

import { Event } from "../types/event";
import { EditEventModal } from "./edit-event-modal";
import { ViewEventModal } from "./view-event-modal";

interface Props extends ComponentProps<typeof CalendarComponent<Event>> {
  titleWithRoom?: boolean;
}

const EventsCalendar: FC<Props> = ({ titleWithRoom = true, ...props }) => {
  const { data: user } = useCurrentUser();

  const titleAccessor = useCallback(
    (event: Event) => {
      const baseTitle = event.title || event.user.short_name;
      return titleWithRoom ? `(${event.room.number}) ${baseTitle}` : baseTitle;
    },
    [titleWithRoom],
  );

  const onSelectEvent = useCallback(
    (event: Event) => {
      const modal =
        event.user.id === user?.id ? EditEventModal : ViewEventModal;
      void NiceModal.show(modal, { eventId: event.id });
    },
    [user],
  );

  return (
    <CalendarComponent
      startAccessor="date_from"
      endAccessor="date_to"
      titleAccessor={titleAccessor}
      defaultView="week"
      dayLayoutAlgorithm="no-overlap"
      timeslots={1}
      onSelectEvent={onSelectEvent}
      {...props}
    />
  );
};

export { EventsCalendar };
