import { Response } from "@/features/api";

import { User } from "./user";

type GetCurrentUserResponse = Response<User>;

interface LoginBody {
  email: string;
  password: string;
}

export { GetCurrentUserResponse, LoginBody };
