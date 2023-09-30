import {PayloadAction} from "@reduxjs/toolkit";
import {call, getContext, put, takeLatest} from "redux-saga/effects";
import {handleError, navigate} from "../../common/utils";
import {SagaContext} from "../../redux/store";
import {AuthApi} from "../api";
import {LoginCredential, RegistrationData, ResetPasswordData} from "../types";
import {
    confirmRegistrationAction,
    forgetPasswordAction,
    loginAction,
    registerAction,
    resetPasswordAction
} from "./actions";
import {authActions} from "./slice";
import {appActions} from "../../redux/appSlice";

export function* authWatcher() {
    yield takeLatest(loginAction.type, loginWorker);
    yield takeLatest(registerAction.type, registrationWorker);
    yield takeLatest(forgetPasswordAction.type, forgetPasswordWorker);
    yield takeLatest(confirmRegistrationAction.type, confirmRegistrationWorker);
    yield takeLatest(resetPasswordAction.type, resetPasswordWorker);
}

function* loginWorker({payload}: PayloadAction<LoginCredential>) {
    try {
        const api: AuthApi = yield getContext(SagaContext.AUTH_API);
        yield call(api.login, payload);
        yield put(appActions.setLoggedIn(true));
        yield call(navigate, "/home");
    } catch (err: unknown) {
        yield handleError(err);
    } finally {
        yield authActions.setLoading(false);
    }
}

function* registrationWorker({payload}: PayloadAction<RegistrationData>) {
    try {
        const api: AuthApi = yield getContext(SagaContext.AUTH_API)
        yield call(api.register, payload)
        yield put(authActions.setRegistered(true))
    } catch (err: unknown) {
        yield handleError(err)
    } finally {
        yield authActions.setLoading(false)
    }
}

function* forgetPasswordWorker({payload: email}: PayloadAction<string>) {
    try {
        const api: AuthApi = yield getContext(SagaContext.AUTH_API)
        yield call(api.forgetPassword, email)
        yield put(authActions.setForgetPasswordLinkSent(true))
    } catch (err: unknown) {
        yield handleError(err)
    } finally {
        yield authActions.setLoading(false)
    }
}

function* confirmRegistrationWorker({payload: token}: PayloadAction<string>) {
    try {
        const api: AuthApi = yield getContext(SagaContext.AUTH_API)
        yield call(api.confirmRegistration, token)
        yield put(authActions.setRegistrationConfirmed(true))
    } catch (err: unknown) {
        yield handleError(err)
    } finally {
        yield authActions.setLoading(false)
    }
}

function* resetPasswordWorker({payload}: PayloadAction<ResetPasswordData>) {
    try {
        const api: AuthApi = yield getContext(SagaContext.AUTH_API)
        yield call(api.resetPassword, payload)
        yield put(authActions.setPasswordReset(true))
    } catch (err: unknown) {
        yield handleError(err)
    } finally {
        yield authActions.setLoading(false)
    }
}