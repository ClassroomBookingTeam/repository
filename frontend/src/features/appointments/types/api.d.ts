import { PaginatedResponse, PaginationParams, Response } from "@/features/api";

import { Appointment } from "./appointment";

interface CreateAppointmentBody {
  event: string;
}

interface GetCurrentUserAppointmentsParams extends PaginationParams {
  date_from?: Date;
  date_to?: Date;
}

type GetCurrentUserAppointmentsResponse = PaginatedResponse<Appointment>;

type GetAppointmentResponse = Response<Appointment>;

export {
  CreateAppointmentBody,
  GetAppointmentResponse,
  GetCurrentUserAppointmentsParams,
  GetCurrentUserAppointmentsResponse,
};
