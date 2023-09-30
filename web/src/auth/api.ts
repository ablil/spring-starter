import axios from "axios";
import {LoginCredential, RegistrationData, ResetPasswordData} from "./types";

export interface AuthApi {
    login: (credentials: LoginCredential) => Promise<void>;
    register: (data: RegistrationData) => Promise<void>;
    forgetPassword: (email: string) => Promise<void>;
    confirmRegistration: (token: string) => Promise<void>
    resetPassword: (data: ResetPasswordData) => Promise<void>
}

export function getAuthApi(): AuthApi {
    return {
        async login({username, password}) {
            const {data} = await axios.post("/auth/login", {
                usernameOrEmail: username,
                password,
            });
            return data;
        },
        async register(form) {
            const {data} = await axios.post('/auth/register', {...form})
            return data
        },
        async forgetPassword(email) {
            const {data} = await axios.post('/auth/forget_password', {email})
            return data
        },
        async confirmRegistration(token) {
            const {data} = await axios.get(`/auth/register/confirm?token=${token}`)
            return data
        },
        async resetPassword(form) {
            const {data} = await axios.post('/auth/reset_password', {...form})
            return data
        }
    };
}
