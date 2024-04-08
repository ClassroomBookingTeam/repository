import { useMutation, useQueryClient } from "@tanstack/react-query";

import { handleError } from "@/lib/utils";

import { logout } from "../api/users";

interface UseLogoutMutationOptions {
  onSuccess?: (data: void) => unknown;
  onError?: (error: unknown) => unknown;
}

const useLogoutMutation = ({
  onSuccess,
  onError,
}: UseLogoutMutationOptions = {}) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => logout(),
    onSuccess: (data) => {
      onSuccess?.(data);
      queryClient.resetQueries();
    },
    onError: onError ?? ((error) => handleError(error)),
  });
};

export { useLogoutMutation };
