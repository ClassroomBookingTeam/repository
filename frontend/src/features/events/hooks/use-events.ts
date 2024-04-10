import { useQuery, UseQueryOptions } from "@tanstack/react-query";

import { getEvents } from "../api/events";
import { GetEventsParams } from "../types/api";
import { Event } from "../types/event";

const useEvents = (
  params?: GetEventsParams,
  options?: Omit<UseQueryOptions<Event[]>, "queryKey" | "queryFn">,
) => {
  return useQuery({
    queryKey: ["event", "list", "flat", params],
    queryFn: async ({ signal }) => getEvents(params, { signal }),
    ...options,
  });
};

export { useEvents };
