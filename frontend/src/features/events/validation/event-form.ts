import { z } from "zod";

const EVENT_FORM_SCHEMA = z
  .object({
    title: z.string().optional(),
    description: z.string().optional(),
    date_from: z.date({ required_error: "Укажите дату начала" }),
    date_to: z.date({ required_error: "Укажите дату окончания" }),
    recurrence: z.string().uuid().optional(),
    recurrence_until: z.date().optional(),
    max_appointments: z
      .string()
      .or(z.number())
      .transform((value) => (value === "" ? undefined : value))
      .optional()
      .refine(
        (value) =>
          value === undefined || (Number.isInteger(+value) && +value >= 0),
        { message: "Количество мест должно быть целым неотрицательным числом" },
      )
      .transform((value) => (value === undefined ? undefined : Number(value))),
  })
  .superRefine(({ recurrence, recurrence_until }, ctx) => {
    if (recurrence && !recurrence_until) {
      ctx.addIssue({
        code: "custom",
        message: "Укажите дату окончания повторения",
        path: ["recurrence_until"],
      });
    }
  });

type EventFormData = z.infer<typeof EVENT_FORM_SCHEMA>;

export { EVENT_FORM_SCHEMA, type EventFormData };
