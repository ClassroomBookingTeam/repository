import { zodResolver } from "@hookform/resolvers/zod";
import { ComponentProps, FC, useEffect } from "react";
import { useForm } from "react-hook-form";

import { Button } from "@/components/ui/button";
import { handleError } from "@/lib/utils";

import { EventForm } from "../components/event-form";
import { useEvent } from "../hooks/use-event";
import { useUpdateEventMutation } from "../hooks/use-update-event-mutation";
import { event2form } from "../utils/event-form";
import { EVENT_FORM_SCHEMA, EventFormData } from "../validation/event-form";
import { DeleteEventButtonContainer } from "./delete-event-button-container";

interface Props
  extends Omit<ComponentProps<typeof EventForm>, "onSubmit" | "form"> {
  eventId: string;
  onUpdateSuccess?: () => unknown;
  onDeleteSuccess?: () => unknown;
}

const EditEventFormContainer: FC<Props> = ({
  eventId,
  onUpdateSuccess,
  onDeleteSuccess,
}) => {
  const form = useForm<EventFormData>({
    resolver: zodResolver(EVENT_FORM_SCHEMA),
  });

  const { data: event, isLoading, isError } = useEvent(eventId);

  useEffect(() => {
    if (event) {
      const defaultValues = event2form(event);
      form.reset(defaultValues);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [event]);

  const { mutateAsync, isPending } = useUpdateEventMutation(eventId, {
    onSuccess: onUpdateSuccess,
    onError: (error) => handleError(error, form),
  });

  if (isLoading) {
    return <p>Загрузка события...</p>;
  }

  if (isError) {
    return <p>Не удалось загрузить событие</p>;
  }

  return (
    <EventForm form={form} onSubmit={mutateAsync}>
      <DeleteEventButtonContainer
        eventId={eventId}
        onSuccess={onDeleteSuccess}
      />
      <Button type="submit" disabled={isPending}>
        Сохранить
      </Button>
    </EventForm>
  );
};

export { EditEventFormContainer };
