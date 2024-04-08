import { type QueryClientConfig } from "@tanstack/react-query";

const QUERY_CLIENT_CONFIG: QueryClientConfig = {
  defaultOptions: {
    queries: {
      retry: false,
      refetchOnWindowFocus: false,
      staleTime: 5 * 60 * 1000,
    },
  },
};

export { QUERY_CLIENT_CONFIG };
