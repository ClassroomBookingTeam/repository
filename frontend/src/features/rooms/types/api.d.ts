import { PaginatedResponse, PaginationParams } from "@/features/api";

import { Room } from "./room";

interface GetRoomsParams extends PaginationParams {
  available_at?: Date;
}

type GetRoomsResponse = PaginatedResponse<Room>;

type GetRoomResponse = Response<Room>;

export { GetRoomResponse, GetRoomsParams, GetRoomsResponse };
