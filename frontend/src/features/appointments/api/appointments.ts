import { client, RequestConfig } from "@/features/api";

import {
  CreateAppointmentBody,
  GetAppointmentResponse,
  GetCurrentUserAppointmentsParams,
  GetCurrentUserAppointmentsResponse,
} from "../types/api";

const createAppointment = async (
  body: CreateAppointmentBody,
  config?: RequestConfig,
) => {
  const { data } = await client.post<GetAppointmentResponse>(
    "/v1/appointments/",
    body,
    config,
  );

  return data;
};

const cancelAppointment = async (id: string, config?: RequestConfig) => {
  await client.delete(`/v1/appointments/${id}/`, config);
};

const getCurrentUserAppointments = async (
  params?: GetCurrentUserAppointmentsParams,
  config?: RequestConfig,
) => {
  const { data } = await client.get<GetCurrentUserAppointmentsResponse>(
    "/v1/users/current/appointments/",
    { params, ...config },
  );

  return data;
};

export { cancelAppointment, createAppointment, getCurrentUserAppointments };
