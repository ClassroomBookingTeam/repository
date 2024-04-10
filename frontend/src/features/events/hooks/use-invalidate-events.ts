import { useQueryClient } from "@tanstack/react-query";

const useInvalidateEvents = () => {
  const queryClient = useQueryClient();

  const invalidateEvents = async (): Promise<void> => {
    await queryClient.invalidateQueries({
      predicate: ({ queryKey }) => queryKey.includes("event"),
    });
  };

  return { invalidateEvents };
};

export { useInvalidateEvents };
