import { FC } from "react";

import { CancelAppointmentButton } from "../components/cancel-appointment-button";
import { useCancelAppointmentMutation } from "../hooks/use-cancel-appointment-mutation";

interface Props {
  appointmentId: string;
}

const CancelAppointmentButtonContainer: FC<Props> = ({ appointmentId }) => {
  const { mutate, isPending } = useCancelAppointmentMutation(appointmentId);

  return (
    <CancelAppointmentButton
      onClick={() => void mutate()}
      disabled={isPending}
    />
  );
};

export { CancelAppointmentButtonContainer };
