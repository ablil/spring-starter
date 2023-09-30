import { configureStore } from "@reduxjs/toolkit";
import createSagaMiddleware from "redux-saga";
import { appReducer } from "./appSlice";
import { watchAll } from "./sagas";
import { authReducer } from "../auth/redux/slice";
import { getAuthApi } from "../auth/api";
import { router } from "../router/router.tsx";

export enum SagaContext {
  AUTH_API = "authApi",
  ROUTER = "router"
}

const sagaMiddleware = createSagaMiddleware({
  context: {
    [SagaContext.AUTH_API]: getAuthApi(),
    [SagaContext.ROUTER]: router,
  },
});

export const store = configureStore({
  reducer: {
    app: appReducer,
    auth: authReducer,
  },
  middleware: (getDefaultMiddlewares) =>
    getDefaultMiddlewares().concat(sagaMiddleware),
});

sagaMiddleware.run(watchAll);


export type RootAppState = ReturnType<typeof store.getState>