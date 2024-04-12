import { QueryClient } from "@tanstack/react-query";

import { QUERY_CLIENT_CONFIG } from "../config/query-client";

const queryClient = new QueryClient(QUERY_CLIENT_CONFIG);

export { queryClient };
