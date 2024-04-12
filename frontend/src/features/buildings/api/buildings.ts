import { client, RequestConfig } from "@/features/api";

import { GetBuildingsResponse } from "../types/api";

const getBuildings = async (config?: RequestConfig) => {
  const { data } = await client.get<GetBuildingsResponse>(
    "/v1/buildings/",
    config,
  );

  return data;
};

export { getBuildings };
