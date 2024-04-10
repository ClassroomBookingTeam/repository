import { client, RequestConfig } from "@/features/api";

import { GetRecurrencesResponse } from "../types/api";

const getRecurrences = async (config?: RequestConfig) => {
  const { data } = await client.get<GetRecurrencesResponse>(
    "/v1/recurrences/",
    config,
  );

  return data;
};

export { getRecurrences };
