import {Navigate} from "react-router-dom";
import {FC, ReactNode} from "react";
import {useSelector} from "react-redux";
import {RootAppState} from "../redux/store.ts";

type Props = {
    component: ReactNode
}
export const PrivateRoute: FC<Props> = ({component}) => {

    const loggedIn = useSelector((app: RootAppState) => app.app.loggedIn)

    if (loggedIn) {
        return component
    } else {
        return <Navigate to={"/login"}/>
    }
}