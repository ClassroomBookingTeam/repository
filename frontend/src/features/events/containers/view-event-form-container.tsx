import { ComponentProps, FC, useEffect } from "react";
import { useForm } from "react-hook-form";

import { EventForm } from "../components/event-form";
import { useEvent } from "../hooks/use-event";
import { event2form } from "../utils/event-form";
import { EventFormData } from "../validation/event-form";

interface Props
  extends Omit<ComponentProps<typeof EventForm>, "onSubmit" | "form"> {
  eventId: string;
}

const ViewEventFormContainer: FC<Props> = ({ eventId }) => {
  const form = useForm<EventFormData>();

  const { data: event, isLoading, isError } = useEvent(eventId);

  useEffect(() => {
    if (event) {
      const defaultValues = event2form(event);
      form.reset(defaultValues);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [event]);

  if (isLoading) {
    return <p>Загрузка события...</p>;
  }

  if (isError) {
    return <p>Не удалось загрузить событие</p>;
  }

  return <EventForm form={form} />;
};

export { ViewEventFormContainer };
