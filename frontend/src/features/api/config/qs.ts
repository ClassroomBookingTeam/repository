import { formatISO } from "date-fns";
import { type IStringifyOptions } from "qs";

const QS_STRINGIFY_CONFIG: IStringifyOptions = {
  arrayFormat: "comma",
  serializeDate: formatISO,
};

export { QS_STRINGIFY_CONFIG };
