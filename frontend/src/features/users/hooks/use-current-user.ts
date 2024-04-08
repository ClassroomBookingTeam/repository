import { useQuery, UseQueryOptions } from "@tanstack/react-query";

import { getCurrentUser } from "../api/users";
import { User } from "../types/user";

const useCurrentUser = (options?: UseQueryOptions<User>) => {
  return useQuery({
    queryFn: ({ signal }) => getCurrentUser({ signal }),
    queryKey: ["user", "current"],
    ...options,
  });
};

export { useCurrentUser };
