import { client, RequestConfig } from "@/features/api";

import {
  CreateEventBody,
  GetEventResponse,
  GetEventsParams,
  GetEventsResponse,
  UpdateEventBody,
} from "../types/api";

const getEvents = async (params?: GetEventsParams, config?: RequestConfig) => {
  const { data: initialData } = await client.get<GetEventsResponse>(
    "/v1/events/",
    { params: { ...params, page_size: 1 }, ...config },
  );

  if (
    initialData.count <= 1 ||
    initialData.count === initialData.results.length
  ) {
    return initialData.results;
  }

  const { data } = await client.get<GetEventsResponse>("/v1/events", {
    params: { ...params, page_size: initialData.count },
    ...config,
  });

  return data.results;
};

const getCurrentUserEvents = async (
  params?: GetEventsParams,
  config?: RequestConfig,
) => {
  const { data: initialData } = await client.get<GetEventsResponse>(
    "/v1/users/current/events/",
    { params: { ...params, page_size: 1 }, ...config },
  );

  if (
    initialData.count <= 1 ||
    initialData.count === initialData.results.length
  ) {
    return initialData.results;
  }

  const { data } = await client.get<GetEventsResponse>(
    "/v1/users/current/events/",
    { params: { ...params, page_size: initialData.count }, ...config },
  );

  return data.results;
};

const getEventsPaginated = async (
  params?: GetEventsParams,
  config?: RequestConfig,
) => {
  const { data } = await client.get<GetEventsResponse>("/v1/events/", {
    params,
    ...config,
  });

  return data;
};

const createEvent = async (body: CreateEventBody, config?: RequestConfig) => {
  const { data } = await client.post<GetEventResponse>(
    "/v1/events/",
    body,
    config,
  );

  return data;
};

const deleteEvent = async (id: string, config?: RequestConfig) => {
  await client.delete(`/v1/events/${id}/`, config);
};

const getEvent = async (id: string, config?: RequestConfig) => {
  const { data } = await client.get<GetEventResponse>(
    `/v1/events/${id}/`,
    config,
  );

  return data;
};

const updateEvent = async (
  id: string,
  body: UpdateEventBody,
  config?: RequestConfig,
) => {
  const { data } = await client.patch<GetEventResponse>(
    `/v1/events/${id}/`,
    body,
    config,
  );

  return data;
};

export {
  createEvent,
  deleteEvent,
  getCurrentUserEvents,
  getEvent,
  getEvents,
  getEventsPaginated,
  updateEvent,
};
