import { useQuery, UseQueryOptions } from "@tanstack/react-query";

import { getEvent } from "../api/events";
import { Event } from "../types/event";

const useEvent = (id: string, options?: UseQueryOptions<Event>) => {
  return useQuery({
    queryKey: ["event", id],
    queryFn: async ({ signal }) => getEvent(id, { signal }),
    ...options,
  });
};

export { useEvent };
