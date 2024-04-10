import { useMutation } from "@tanstack/react-query";
import { toast } from "sonner";

import { handleError } from "@/lib/utils";

import { updateEvent } from "../api/events";
import { UpdateEventBody } from "../types/api";
import { Event } from "../types/event";
import { useInvalidateEvents } from "./use-invalidate-events";

interface UseUpdateEventMutationOptions {
  onSuccess?: (data: Event) => unknown;
  onError?: (error: unknown) => unknown;
}

const useUpdateEventMutation = (
  eventId: string,
  { onSuccess, onError }: UseUpdateEventMutationOptions = {},
) => {
  const { invalidateEvents } = useInvalidateEvents();

  return useMutation({
    mutationFn: (body: UpdateEventBody) => updateEvent(eventId, body),
    onSuccess: (data) => {
      toast("Событие сохранено");
      onSuccess?.(data);
      void invalidateEvents();
    },
    onError: onError ?? ((error) => handleError(error)),
  });
};

export { useUpdateEventMutation };
