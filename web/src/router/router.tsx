import {createBrowserRouter} from "react-router-dom";
import {Route, routes} from "./routes";
import {PrivateRoute} from "./PrivateRoute.tsx";


export function createRouter(routes: Route[]) {
    return createBrowserRouter(routes.map((route => ({
        ...route,
        element: route.private ? <PrivateRoute component={route.element}/> : route.element
    }))))
}

export const router = createRouter(routes)