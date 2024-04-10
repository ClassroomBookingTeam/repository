import { useQuery, UseQueryOptions } from "@tanstack/react-query";

import { getRecurrences } from "../api/recurrences";
import { Recurrence } from "../types/recurrence";

const useRecurrences = (options?: UseQueryOptions<Recurrence[]>) => {
  return useQuery({
    queryKey: ["recurrence", "list"],
    queryFn: async ({ signal }) => getRecurrences({ signal }),
    ...options,
  });
};

export { useRecurrences };
