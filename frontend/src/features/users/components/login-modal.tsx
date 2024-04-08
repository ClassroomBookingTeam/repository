import { useNavigate } from "react-router-dom";

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

import { LoginFormContainer } from "../containers/login-form-container";

const LoginModal = () => {
  const navigate = useNavigate();

  return (
    <Dialog open>
      <DialogContent
        // Чтобы юзер случайно не сбросил заполненную форму
        onInteractOutside={(e) => e.preventDefault()}
        className="overflow-y-scroll max-h-screen"
      >
        <DialogHeader>
          <DialogTitle>Авторизация</DialogTitle>
          <DialogDescription>
            Для доступа к системе укажите учетные данные
          </DialogDescription>
        </DialogHeader>

        <LoginFormContainer onSuccess={() => navigate("/")} />
      </DialogContent>
    </Dialog>
  );
};

export { LoginModal };
