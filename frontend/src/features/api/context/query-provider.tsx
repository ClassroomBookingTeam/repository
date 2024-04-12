import { QueryClientProvider } from "@tanstack/react-query";
import { type FC, type ReactNode } from "react";

import { queryClient } from "../entities/query-client";

interface Props {
  children: ReactNode;
}

const QueryProvider: FC<Props> = ({ children }) => {
  return (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
};

export { QueryProvider };
