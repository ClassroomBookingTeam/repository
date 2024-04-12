import { useQuery, UseQueryOptions } from "@tanstack/react-query";

import { getRooms } from "../api/rooms";
import { GetRoomsParams } from "../types/api";
import { Room } from "../types/room";

const useRooms = (
  buildingId?: string,
  params?: GetRoomsParams,
  options?: UseQueryOptions<Room[]>,
) => {
  return useQuery({
    queryKey: ["room", "list", buildingId, params],
    queryFn: async ({ signal }) => getRooms(buildingId, params, { signal }),
    ...options,
  });
};

export { useRooms };
