import { format } from "date-fns";
import { FC } from "react";

import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { AppointmentButton } from "@/features/appointments";

import { Event } from "../types/event";

interface Props {
  event: Event;
}

const EventCard: FC<Props> = ({ event }) => {
  const timeFormatted = (() => {
    const timeFrom = format(event.date_from, "HH:mm");
    const timeTo = format(event.date_to, "HH:mm");

    return `${timeFrom} – ${timeTo}`;
  })();

  return (
    <Card>
      <CardHeader>
        <CardTitle>{event.title || event.user.short_name}</CardTitle>
        <CardDescription className="flex items-center gap-2 gap-y-0 flex-wrap">
          <span>{event.user.short_name}</span>
          <Separator asChild orientation="vertical" className="h-4">
            <span />
          </Separator>
          <span>{timeFormatted}</span>
          <Separator asChild orientation="vertical" className="h-4">
            <span />
          </Separator>
          <span>{event.room.building.name}</span>
          <Separator asChild orientation="vertical" className="h-4">
            <span />
          </Separator>
          <span>{event.room.number}</span>
        </CardDescription>
      </CardHeader>

      {!!event.description && <CardContent>{event.description}</CardContent>}

      <CardFooter>
        <AppointmentButton event={event} />
      </CardFooter>
    </Card>
  );
};

export { EventCard };
