import { createAction } from "@reduxjs/toolkit";
import {LoginCredential, RegistrationData, ResetPasswordData} from "../types";

export const loginAction = createAction<LoginCredential>(
  "auth/login"
);

export const registerAction = createAction<RegistrationData>("auth/register")

export const forgetPasswordAction = createAction<string>('auth/forgetpassword')

export const confirmRegistrationAction = createAction<string>('auth/confirmregistration')

export const resetPasswordAction = createAction<ResetPasswordData>('auth/resetpassword')