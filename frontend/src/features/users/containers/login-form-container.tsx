import { zodResolver } from "@hookform/resolvers/zod";
import { FC } from "react";
import { useForm } from "react-hook-form";

import { Button } from "@/components/ui/button";
import { handleError } from "@/lib/utils";

import { LoginForm } from "../components/login-form";
import { useLoginMutation } from "../hooks/use-login-mutation";
import { LOGIN_FORM_SCHEMA, LoginFormData } from "../validation/login-form";

interface Props {
  onSuccess?: (data: void) => unknown;
}

const LoginFormContainer: FC<Props> = ({ onSuccess }) => {
  const form = useForm<LoginFormData>({
    resolver: zodResolver(LOGIN_FORM_SCHEMA),
  });

  const loginMutation = useLoginMutation({
    onSuccess,
    onError: (error) => handleError(error, form),
  });

  return (
    <LoginForm onSubmit={loginMutation.mutateAsync} form={form}>
      <Button type="submit" disabled={loginMutation.isPending}>
        Войти
      </Button>
    </LoginForm>
  );
};

export { LoginFormContainer };
