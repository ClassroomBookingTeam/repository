import { client, RequestConfig } from "@/features/api";

import {
  GetRoomResponse,
  GetRoomsParams,
  GetRoomsResponse,
} from "../types/api";

const getRooms = async (
  buildingId?: string,
  params?: GetRoomsParams,
  config?: RequestConfig,
) => {
  const { data: initialData } = await client.get<GetRoomsResponse>(
    buildingId ? `/v1/buildings/${buildingId}/rooms/` : "/v1/rooms/",
    { params: { ...params, page_size: 1 }, ...config },
  );

  if (
    initialData.count <= 1 ||
    initialData.count === initialData.results.length
  ) {
    return initialData.results;
  }

  const { data } = await client.get<GetRoomsResponse>(
    buildingId ? `/v1/buildings/${buildingId}/rooms/` : "/v1/rooms/",
    { params: { ...params, page_size: initialData.count }, ...config },
  );

  return data.results;
};

const getRoom = async (id: string, config?: RequestConfig) => {
  const { data } = await client.get<GetRoomResponse>(
    `/v1/rooms/${id}/`,
    config,
  );

  return data;
};

export { getRoom, getRooms };
