import { type ClassValue, clsx } from "clsx";
import * as _ from "lodash-es";
import { FieldPath, FieldValues, UseFormReturn } from "react-hook-form";
import { toast } from "sonner";
import { twMerge } from "tailwind-merge";
import { z } from "zod";

const cn = (...inputs: ClassValue[]) => {
  return twMerge(clsx(inputs));
};

const parseResponseError = (error: unknown) => {
  const response = _.get(error, "response.data");

  const parseResult = z
    .array(z.object({ message: z.string(), path: z.string().nullable() }))
    .safeParse(response);

  if (parseResult.success) {
    return parseResult.data;
  }

  return [{ message: "Неизвестная ошибка", path: null }];
};

const handleError = <TValues extends FieldValues>(
  error: unknown,
  form?: UseFormReturn<TValues>,
): void => {
  const messages = parseResponseError(error);

  const validFields = form ? Object.keys(form.getValues()) : [];

  for (const { message, path } of messages) {
    if (!form || !path || !validFields.includes(path)) {
      toast(message);
      continue;
    }

    form.setError(path as FieldPath<TValues>, { type: "custom", message });
  }
};

export { cn, handleError };
