import { formatISO, parseISO } from "date-fns";
import * as _ from "lodash-es";

const ISO_DATE_REGEX = /^\d{4}-(0\d|1[0-2])-([0-2]\d|3[01])$/;

const ISO_DATETIME_REGEX =
  /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:\.\d*)?(?:[+-]\d{2}:?\d{2}|Z)?$/;

const isISODateString = (value: unknown): value is string => {
  return (
    _.isString(value) &&
    (ISO_DATE_REGEX.test(value) || ISO_DATETIME_REGEX.test(value))
  );
};

const deserializeDates = (source: unknown): void => {
  if (!_.isObject(source)) return;

  const obj = source as Record<string, unknown>;

  for (const key in obj) {
    const value = obj[key];

    if (isISODateString(value)) {
      obj[key] = parseISO(value);
    } else {
      deserializeDates(value);
    }
  }
};

const serializeDates = (source: unknown): void => {
  if (!_.isObject(source)) return;

  const obj = source as Record<string, unknown>;

  for (const key in obj) {
    const value = obj[key];

    if (_.isDate(value)) {
      obj[key] = formatISO(value);
    } else {
      serializeDates(value);
    }
  }
};

export { deserializeDates, serializeDates };
