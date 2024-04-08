import { ComponentType } from "react";
import { Navigate } from "react-router-dom";

import { LoginModal } from "../components/login-modal";
import { useCurrentUser } from "../hooks/use-current-user";
import { Role } from "../types/role";

const withAuth = (roles: Role[]) => {
  return <TProps extends object>(Component: ComponentType<TProps>) => {
    const WrappedComponent = (props: TProps) => {
      const { data: user, isLoading } = useCurrentUser();

      if (isLoading) {
        return (
          <div className="mt-10 w-full text-center">
            Проверка прав доступа...
          </div>
        );
      }

      if (!user) {
        return <LoginModal />;
      }

      if (!user.roles.some((role) => roles.includes(role))) {
        return <Navigate to="/" />;
      }

      return <Component {...props} />;
    };

    WrappedComponent.displayName = `withAuth(${
      Component.displayName || Component.name || "Component"
    })`;

    return WrappedComponent;
  };
};

export { withAuth };
