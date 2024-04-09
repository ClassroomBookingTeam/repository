import { useQuery, UseQueryOptions } from "@tanstack/react-query";

import { getBuildings } from "../api/buildings";
import { Building } from "../types/building";

const useBuildings = (options?: UseQueryOptions<Building[]>) => {
  return useQuery({
    queryKey: ["building", "list"],
    queryFn: async ({ signal }) => getBuildings({ signal }),
    ...options,
  });
};

export { useBuildings };
