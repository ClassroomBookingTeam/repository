import { zodResolver } from "@hookform/resolvers/zod";
import { ComponentProps, FC } from "react";
import { DefaultValues, useForm } from "react-hook-form";

import { Button } from "@/components/ui/button";
import { handleError } from "@/lib/utils";

import { EventForm } from "../components/event-form";
import { useCreateEventMutation } from "../hooks/use-create-event-mutation";
import { Event } from "../types/event";
import { EVENT_FORM_SCHEMA, EventFormData } from "../validation/event-form";

interface Props
  extends Omit<ComponentProps<typeof EventForm>, "onSubmit" | "form"> {
  roomId: string;
  onSuccess?: (data: Event) => unknown;
  defaultValues?: DefaultValues<EventFormData>;
}

const CreateEventFormContainer: FC<Props> = ({
  roomId,
  onSuccess,
  defaultValues,
}) => {
  const form = useForm<EventFormData>({
    resolver: zodResolver(EVENT_FORM_SCHEMA),
    defaultValues: { max_appointments: 0, ...defaultValues },
  });

  const { mutateAsync, isPending } = useCreateEventMutation(roomId, {
    onSuccess,
    onError: (error) => handleError(error, form),
  });

  return (
    <EventForm onSubmit={mutateAsync} form={form}>
      <Button type="submit" disabled={isPending}>
        Создать
      </Button>
    </EventForm>
  );
};

export { CreateEventFormContainer };
