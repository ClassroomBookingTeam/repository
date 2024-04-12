import { FC } from "react";

import { Event } from "@/features/events";

import { CreateAppointmentButton } from "../components/create-appointment-button";
import { useCreateAppointmentMutation } from "../hooks/use-create-appointment-mutation";

interface Props {
  event: Event;
}

const CreateAppointmentButtonContainer: FC<Props> = ({ event }) => {
  const { mutate, isPending } = useCreateAppointmentMutation(event.id);

  return (
    <CreateAppointmentButton
      event={event!}
      onClick={() => void mutate()}
      disabled={isPending}
    />
  );
};

export { CreateAppointmentButtonContainer };
