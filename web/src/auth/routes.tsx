import {RouteObject} from "react-router-dom";
import LoginPage from "./login/LoginPage";
import RegistrationPage from "./registration/RegistrationPage.tsx";
import {ForgetPasswordPage} from "./forgetpassword/ForgetPasswordPage.tsx";
import {RegistrationConfirmationPage} from "./registration/RegistrationConfirmationPage.tsx";
import {ResetPasswordPage} from "./forgetpassword/ResetPasswordPage.tsx";

export const authRoutes: RouteObject[] = [
    {
        path: '/login',
        element: <LoginPage/>
    },
    {
        path: '/register',
        element: <RegistrationPage/>
    },
    {
        path: '/forgetpassword',
        element: <ForgetPasswordPage/>
    },
    {
        path: '/register/confirm',
        element: <RegistrationConfirmationPage/>
    }, {
        path: '/resetpassword',
        element: <ResetPasswordPage/>
    }
]