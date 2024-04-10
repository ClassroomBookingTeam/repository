import { useQueryClient } from "@tanstack/react-query";

const useInvalidateAppointments = () => {
  const queryClient = useQueryClient();

  const invalidateAppointments = async (): Promise<void> => {
    await queryClient.invalidateQueries({
      predicate: ({ queryKey }) => queryKey.includes("appointment"),
    });
  };

  return { invalidateAppointments };
};

export { useInvalidateAppointments };
