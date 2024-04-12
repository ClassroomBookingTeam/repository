import { FC } from "react";

import { Event } from "@/features/events";

import { CancelAppointmentButtonContainer } from "../containers/cancel-appointment-button-container";
import { CreateAppointmentButtonContainer } from "../containers/create-appointment-button-container";

interface Props {
  event: Event;
}

const AppointmentButton: FC<Props> = ({ event }) => {
  return event.appointment_id ? (
    <CancelAppointmentButtonContainer appointmentId={event.appointment_id} />
  ) : (
    <CreateAppointmentButtonContainer event={event} />
  );
};

export { AppointmentButton };
