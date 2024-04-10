import NiceModal from "@ebay/nice-modal-react";
import { setDefaultOptions } from "date-fns";
import { ru } from "date-fns/locale";
import { lazy, Suspense } from "react";
import { createBrowserRouter, Outlet, RouterProvider } from "react-router-dom";
import { QueryParamProvider } from "use-query-params";
import { ReactRouter6Adapter } from "use-query-params/adapters/react-router-6";

import { Toaster } from "@/components/ui/sonner";

import { QueryProvider } from "./features/api";
import { Layout } from "./layout/layout";

setDefaultOptions({ locale: ru });

const router = createBrowserRouter([
  {
    path: "/",
    element: (
      <QueryParamProvider adapter={ReactRouter6Adapter}>
        <Layout>
          <Suspense fallback="Загрузка...">
            <Outlet />
          </Suspense>
        </Layout>
      </QueryParamProvider>
    ),
    children: [
      {
        index: true,
        path: "/",
        Component: lazy(() => import("./routes")),
      },
      {
        path: "/users/current/events/",
        Component: lazy(() => import("./routes/users/current/events")),
      },
      {
        path: "/users/current/appointments/",
        Component: lazy(() => import("./routes/users/current/appointments")),
      },
      {
        path: "/rooms/:id/",
        Component: lazy(() => import("./routes/rooms/[id]")),
      },
      {
        path: "*",
        element: <p>Страница не найдена</p>,
      },
    ],
  },
]);

const App = () => {
  return (
    <QueryProvider>
      <NiceModal.Provider>
        <RouterProvider router={router} />
        <Toaster position="top-center" />
      </NiceModal.Provider>
    </QueryProvider>
  );
};

export { App };
