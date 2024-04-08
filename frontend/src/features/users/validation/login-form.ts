import { z } from "zod";

const LOGIN_FORM_SCHEMA = z.object({
  email: z
    .string({ required_error: "Укажите E-mail" })
    .email("Укажите корректный E-mail"),
  password: z
    .string({ required_error: "Введите пароль" })
    .min(1, "Пароль не может быть пустым"),
});

type LoginFormData = z.infer<typeof LOGIN_FORM_SCHEMA>;

export { LOGIN_FORM_SCHEMA, type LoginFormData };
