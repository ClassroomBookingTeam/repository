import { ComponentPropsWithoutRef, FC } from "react";

import { cn } from "@/lib/utils";

import { Header } from "./header";

export type LayoutProps = ComponentPropsWithoutRef<"div">;

const Layout: FC<LayoutProps> = ({ children, className, ...props }) => {
  return (
    <div
      className={cn("w-[1024px] max-w-[90vw] m-auto mb-8", className)}
      {...props}
    >
      <Header className="mt-4 mb-8" />
      <main>{children}</main>
    </div>
  );
};

export { Layout };
