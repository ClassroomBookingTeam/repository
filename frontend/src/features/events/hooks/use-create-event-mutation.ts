import { useMutation } from "@tanstack/react-query";
import { toast } from "sonner";

import { handleError } from "@/lib/utils";

import { createEvent } from "../api/events";
import { CreateEventBody } from "../types/api";
import { Event } from "../types/event";
import { useInvalidateEvents } from "./use-invalidate-events";

interface UseCreateEventMutationOptions {
  onSuccess?: (data: Event) => unknown;
  onError?: (error: unknown) => unknown;
}

const useCreateEventMutation = (
  roomId: string,
  { onSuccess, onError }: UseCreateEventMutationOptions = {},
) => {
  const { invalidateEvents } = useInvalidateEvents();

  return useMutation({
    mutationFn: (body: Omit<CreateEventBody, "room">) =>
      createEvent({ ...body, room: roomId }),
    onSuccess: (data) => {
      toast("Событие создано");
      onSuccess?.(data);
      void invalidateEvents();
    },
    onError: onError ?? ((error) => handleError(error)),
  });
};

export { useCreateEventMutation };
