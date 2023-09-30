import { all, fork } from "redux-saga/effects";
import { authWatcher } from "../auth/redux/sagas";

export function* watchAll() {
  yield all([authWatcher].map((watcher) => fork(watcher)));
}
