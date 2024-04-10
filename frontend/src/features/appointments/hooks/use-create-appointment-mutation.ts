import { useMutation } from "@tanstack/react-query";
import { toast } from "sonner";

import { useInvalidateEvents } from "@/features/events";
import { handleError } from "@/lib/utils";

import { createAppointment } from "../api/appointments";
import { Appointment } from "../types/appointment";
import { useInvalidateAppointments } from "./use-invalidate-appointments";

interface UseCreateAppointmentMutationOptions {
  onSuccess?: (data: Appointment) => unknown;
  onError?: (error: unknown) => unknown;
}

const useCreateAppointmentMutation = (
  eventId: string,
  { onSuccess, onError }: UseCreateAppointmentMutationOptions = {},
) => {
  const { invalidateAppointments } = useInvalidateAppointments();
  const { invalidateEvents } = useInvalidateEvents();

  return useMutation({
    mutationFn: () => createAppointment({ event: eventId }),
    onSuccess: async (data) => {
      await invalidateEvents();
      toast("Запись успешно создана");
      onSuccess?.(data);
      void invalidateAppointments();
    },
    onError: onError ?? ((error) => handleError(error)),
  });
};

export { useCreateAppointmentMutation };
