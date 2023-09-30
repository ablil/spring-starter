import {createAction, createSlice, PayloadAction} from "@reduxjs/toolkit";

type AppInitialState = {
    loggedIn: boolean
}

const initialState: AppInitialState = {
    loggedIn: false
}
export const initAction = createAction("app/init")

const app = createSlice({
    name: 'app',
    initialState,
    reducers: {
        setLoggedIn: (state, {payload}: PayloadAction<boolean>) => {
            state.loggedIn = payload
        }
    },
});

export const appActions = app.actions
export const appReducer = app.reducer
