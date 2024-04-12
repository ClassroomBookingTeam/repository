import { useInfiniteQuery } from "@tanstack/react-query";

import { getCurrentUserAppointments } from "../api/appointments";
import { GetCurrentUserAppointmentsParams } from "../types/api";

const useCurrentUserAppointmentsPaginated = (
  params?: GetCurrentUserAppointmentsParams,
) => {
  return useInfiniteQuery({
    queryKey: ["user", "current", "appointment", "list", params],
    queryFn: ({ pageParam, signal }) =>
      getCurrentUserAppointments(
        { page: pageParam as number, ...params },
        { signal },
      ),
    getNextPageParam: (response) => response.next,
    getPreviousPageParam: (response) => response.previous,
    initialPageParam: 1,
  });
};

export { useCurrentUserAppointmentsPaginated };
