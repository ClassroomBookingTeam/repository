import { FC, ReactNode } from "react";

import { useCurrentUser } from "../hooks/use-current-user";

interface Props {
  roles: string[];
  children: ReactNode;
}

const RestrictedContainer: FC<Props> = ({ roles, children }) => {
  const { data: user } = useCurrentUser();

  if (user && user.roles.some((role) => roles.includes(role))) {
    return children;
  }
};

export { RestrictedContainer };
