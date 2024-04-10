import { useMutation } from "@tanstack/react-query";
import { toast } from "sonner";

import { useInvalidateEvents } from "@/features/events";
import { handleError } from "@/lib/utils";

import { cancelAppointment } from "../api/appointments";
import { useInvalidateAppointments } from "./use-invalidate-appointments";

interface UseCancelAppointmentMutationOptions {
  onSuccess?: (data: void) => unknown;
  onError?: (error: unknown) => unknown;
}

const useCancelAppointmentMutation = (
  appointmentId: string,
  { onSuccess, onError }: UseCancelAppointmentMutationOptions = {},
) => {
  const { invalidateAppointments } = useInvalidateAppointments();
  const { invalidateEvents } = useInvalidateEvents();

  return useMutation({
    mutationFn: () => cancelAppointment(appointmentId),
    onSuccess: (data) => {
      toast("Запись успешно отменена");
      onSuccess?.(data);
      void invalidateEvents();
      void invalidateAppointments();
    },
    onError: onError ?? ((error) => handleError(error)),
  });
};

export { useCancelAppointmentMutation };
