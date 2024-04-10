import { useInfiniteQuery } from "@tanstack/react-query";

import { getEventsPaginated } from "../api/events";
import { GetEventsParams } from "../types/api";

const useEventsPaginated = (params?: GetEventsParams) => {
  return useInfiniteQuery({
    queryKey: ["event", "list", params],
    queryFn: ({ pageParam }) =>
      getEventsPaginated({ page: pageParam as number, ...params }),
    getNextPageParam: (response) => response.next,
    getPreviousPageParam: (response) => response.previous,
    initialPageParam: 1,
  });
};

export { useEventsPaginated };
