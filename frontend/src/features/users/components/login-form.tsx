import { FC, ReactNode } from "react";
import { UseFormReturn } from "react-hook-form";

import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";

import { LoginFormData } from "../validation/login-form";

interface Props {
  form: UseFormReturn<LoginFormData>;
  onSubmit: (values: LoginFormData) => unknown;
  children?: ReactNode;
}

const LoginForm: FC<Props> = ({ form, onSubmit, children }) => {
  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-2">
        <FormField
          control={form.control}
          name="email"
          render={({ field }) => (
            <FormItem>
              <FormLabel>E-mail</FormLabel>
              <FormControl>
                <Input type="email" placeholder="ivan@bmstu.ru" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="password"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Пароль</FormLabel>
              <FormControl>
                <Input type="password" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {!!children && (
          <div className="!mt-4 w-full flex gap-2 justify-end">{children}</div>
        )}
      </form>
    </Form>
  );
};

export { LoginForm };
