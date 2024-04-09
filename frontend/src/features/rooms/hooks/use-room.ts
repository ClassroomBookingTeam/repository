import { useQuery, UseQueryOptions } from "@tanstack/react-query";

import { getRoom } from "../api/rooms";
import { Room } from "../types/room";

const useRoom = (id: string, options?: UseQueryOptions<Room>) => {
  return useQuery({
    queryKey: ["room", id],
    queryFn: async ({ signal }) => getRoom(id, { signal }),
    ...options,
  });
};

export { useRoom };
