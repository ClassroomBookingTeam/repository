import { Response } from "@/features/api";

import { Recurrence } from "./recurrence";

type GetRecurrencesResponse = Response<Recurrence[]>;

export { GetRecurrencesResponse };
