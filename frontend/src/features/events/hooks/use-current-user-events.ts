import { useQuery, UseQueryOptions } from "@tanstack/react-query";

import { getCurrentUserEvents } from "../api/events";
import { GetCurrentUserEventsParams } from "../types/api";
import { Event } from "../types/event";

const useCurrentUserEvents = (
  params?: GetCurrentUserEventsParams,
  options?: Omit<UseQueryOptions<Event[]>, "queryKey" | "queryFn">,
) => {
  return useQuery({
    queryKey: ["user", "current", "event", "list", "flat", params],
    queryFn: async ({ signal }) => getCurrentUserEvents(params, { signal }),
    ...options,
  });
};

export { useCurrentUserEvents };
