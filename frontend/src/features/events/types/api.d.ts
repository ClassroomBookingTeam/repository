import { PaginatedResponse, PaginationParams, Response } from "@/features/api";

import { Event } from "./event";

interface GetEventsParams extends PaginationParams {
  date_from?: Date;
  date_to?: Date;
  room_id?: string | string[];
  search?: string;
}

type GetEventsResponse = PaginatedResponse<Event>;

type GetEventResponse = Response<Event>;

interface GetCurrentUserEventsParams extends PaginationParams {
  date_from?: Date;
  date_to?: Date;
}

interface CreateEventBody {
  room: string;
  title?: string;
  description?: string;
  recurrence?: string;
  recurrence_until?: Date;
  date_from: Date;
  date_to: Date;
}

type UpdateEventBody = Partial<CreateEventBody>;

export {
  CreateEventBody,
  GetCurrentUserEventsParams,
  GetEventResponse,
  GetEventsParams,
  GetEventsResponse,
  UpdateEventBody,
};
