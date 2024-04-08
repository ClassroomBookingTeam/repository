import axios from "axios";

import { AXIOS_CONFIG } from "../config/axios";
import { deserializeDates, serializeDates } from "../utils/dates";

const client = axios.create(AXIOS_CONFIG);

client.interceptors.request.use((originalRequest) => {
  serializeDates(originalRequest.data);
  return originalRequest;
});

client.interceptors.response.use((originalResponse) => {
  deserializeDates(originalResponse.data);
  return originalResponse;
});

export { client };
