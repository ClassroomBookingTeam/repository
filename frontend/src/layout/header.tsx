import { ComponentPropsWithoutRef, FC } from "react";

import { CurrentUserButton } from "@/features/users";
import { cn } from "@/lib/utils";

import { Navigation } from "./navigation";

export type HeaderProps = Omit<ComponentPropsWithoutRef<"header">, "children">;

const Header: FC<HeaderProps> = ({ className, ...props }) => {
  return (
    <header
      className={cn("w-100 flex justify-between gap-5", className)}
      {...props}
    >
      <Navigation />
      <CurrentUserButton />
    </header>
  );
};

export { Header };
