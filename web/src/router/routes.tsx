import App from "../App";

import { RouteObject } from "react-router-dom";
import { authRoutes } from "../auth/routes";

export type Route = RouteObject & {
  private?: boolean;
}
export const routes: Route[] = [
  {
    path: "/",
    element: <App />,
    errorElement: <div>Ooops ! page not found</div>,
  },
  {
    path: "/home",
    element: (<div>home page</div>),
    private: true
  },
  ...authRoutes,
];
