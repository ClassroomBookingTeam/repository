import { Response } from "@/features/api";

import { Building } from "./building";

type GetBuildingsResponse = Response<Building[]>;

export { GetBuildingsResponse };
