import { FC, ReactNode } from "react";

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

import { User } from "../types/user";

interface Props {
  user: User;
  children: ReactNode;
}

const CurrentUserButton: FC<Props> & {
  Action: typeof DropdownMenuItem;
} = ({ user, children }) => {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger className="font-semibold text-sm block whitespace-nowrap overflow-hidden text-ellipsis">
        {user.short_name}
      </DropdownMenuTrigger>
      <DropdownMenuContent>{children}</DropdownMenuContent>
    </DropdownMenu>
  );
};

CurrentUserButton.Action = DropdownMenuItem;

export { CurrentUserButton };
