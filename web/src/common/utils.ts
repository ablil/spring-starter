import axios from "axios";
import { toast } from "react-toastify";
import { createRouter } from "../router/router.tsx";
import { SagaContext } from "../redux/store";
import {getContext} from 'redux-saga/effects'

export function* handleError(err: unknown) {
  if (axios.isAxiosError(err)) {
    yield toast(err.response?.data.error ?? 'An error occured');
  } else {
    yield toast("An error occured");
    console.error(err);
  }
}

export function *navigate(path: string) {
  const router: ReturnType<typeof createRouter> = yield getContext(SagaContext.ROUTER)
  router.navigate(path)
}