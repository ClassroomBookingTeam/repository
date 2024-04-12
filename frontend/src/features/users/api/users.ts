import { client, RequestConfig } from "@/features/api";

import { GetCurrentUserResponse, LoginBody } from "../types/api";

const getCurrentUser = async (config?: RequestConfig) => {
  const { data } = await client.get<GetCurrentUserResponse>(
    "/v1/users/current/",
    config,
  );

  return data;
};

const logout = async (config?: RequestConfig) => {
  await client.delete("/v1/session/", config);
};

const login = async (body: LoginBody, config?: RequestConfig) => {
  await client.post("/v1/session/", body, config);
};

export { getCurrentUser, login, logout };
