import { useMutation, useQueryClient } from "@tanstack/react-query";

import { handleError } from "@/lib/utils";

import { login } from "../api/users";

interface UseLoginMutationOptions {
  onSuccess?: (data: void) => unknown;
  onError?: (error: unknown) => unknown;
}

const useLoginMutation = ({ onSuccess, onError }: UseLoginMutationOptions) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: login,
    onSuccess: async (data) => {
      await queryClient.resetQueries();
      onSuccess?.(data);
    },
    onError: onError ?? ((error) => handleError(error)),
  });
};

export { useLoginMutation };
