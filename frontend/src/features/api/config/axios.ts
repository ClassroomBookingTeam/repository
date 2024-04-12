import { type CreateAxiosDefaults } from "axios";
import qs from "qs";

import { QS_STRINGIFY_CONFIG } from "./qs";

const AXIOS_CONFIG: CreateAxiosDefaults = {
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
  paramsSerializer: {
    serialize: (params) => qs.stringify(params, QS_STRINGIFY_CONFIG),
  },
};

export { AXIOS_CONFIG };
