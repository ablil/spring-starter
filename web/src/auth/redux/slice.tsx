import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {forgetPasswordAction, loginAction, registerAction} from "./actions";

type InitialStateType = {
    loading: boolean;
    authenticated: boolean;
    registered: boolean;
    registrationConfirmed: boolean
    resetPasswordLinkSent: boolean
    passwordReset: boolean
};

const initialState: InitialStateType = {
    loading: false,
    authenticated: false,
    registered: false,
    resetPasswordLinkSent: false,
    registrationConfirmed: false,
    passwordReset: false,
};

const authSlice = createSlice({
    name: "auth",
    initialState,
    reducers: {
        setLoading: (state, {payload}: PayloadAction<boolean>) => {
            state.loading = payload;
        },
        setRegistered: (state, {payload}: PayloadAction<boolean>) => {
            state.registered = payload
        },
        setForgetPasswordLinkSent: (state, {payload}: PayloadAction<boolean>) => {
            state.resetPasswordLinkSent = payload
        },
        setRegistrationConfirmed: (state, {payload}: PayloadAction<boolean>) => {
            state.registrationConfirmed = payload
        },
        setPasswordReset: (state, {payload}: PayloadAction<boolean>) => {
            state.passwordReset = payload
        }
    },
    extraReducers: (builder) => {
        builder.addMatcher((action) => [loginAction, registerAction, forgetPasswordAction].includes(action), (state) => {
            state.loading = true;
        });
    },
});

export const authActions = authSlice.actions
export const authReducer = authSlice.reducer;
