import { useMutation } from "@tanstack/react-query";
import { toast } from "sonner";

import { handleError } from "@/lib/utils";

import { deleteEvent } from "../api/events";
import { useInvalidateEvents } from "./use-invalidate-events";

interface UseDeleteEventMutationOptions {
  onSuccess?: (data: void) => unknown;
  onError?: () => unknown;
}

const useDeleteEventMutation = (
  eventId: string,
  { onSuccess, onError }: UseDeleteEventMutationOptions = {},
) => {
  const { invalidateEvents } = useInvalidateEvents();

  return useMutation({
    mutationFn: () => deleteEvent(eventId),
    onSuccess: (data) => {
      toast("Событие удалено");
      onSuccess?.(data);
      void invalidateEvents();
    },
    onError: onError ?? ((error) => handleError(error)),
  });
};

export { useDeleteEventMutation };
