import { type RawAxiosRequestConfig } from "axios";

// `params` передаются отдельным аргументом функции запроса API
type RequestConfig = Omit<RawAxiosRequestConfig, "params">;

export type { RequestConfig };
