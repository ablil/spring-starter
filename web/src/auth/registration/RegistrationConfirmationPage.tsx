import {useEffect} from 'react';
import {useSearchParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {confirmRegistrationAction} from "../redux/actions.ts";
import {RootAppState} from "../../redux/store.ts";
import {authActions} from "../redux/slice.tsx";

export const RegistrationConfirmationPage = () => {
    const dispatch = useDispatch();

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const [searchParams, _] = useSearchParams();

    const {registrationConfirmed} = useSelector((app: RootAppState) => app.auth)

    useEffect(() => {
        const token = searchParams.get('token')
        const confirmed = searchParams.get('confirmed')
        if (token) {
            dispatch(confirmRegistrationAction(token))
        }
        if (confirmed) {
            dispatch(authActions.setRegistrationConfirmed(true))
        }
    }, [searchParams]);

    if (registrationConfirmed) {
        return (<div>
            your account have been confirmed you may login
        </div>)
    } else {
        return (<div>there was an error confirming your account</div>)
    }
};