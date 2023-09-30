import {Button, Form, Input} from "antd";
import {useEffect} from 'react';
import {useNavigate, useSearchParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {useFormik} from "formik";
import {resetPasswordAction} from "../redux/actions.ts";
import {RootAppState} from "../../redux/store.ts";

export const ResetPasswordPage = () => {
    const {passwordReset, loading} = useSelector((app: RootAppState) => app.auth)

    const dispatch = useDispatch()
    const navigate = useNavigate()
    const [searchParams] = useSearchParams()

    const formik = useFormik({
        initialValues: {
            password: '',
        },
        onSubmit: (values) => {
            dispatch(resetPasswordAction({...values, token: searchParams.get('token')!}))
        }
    })

    useEffect(() => {
        const token = searchParams.get('token')
        if (!token) {
            navigate('/404')
        }
    }, []);

    if (passwordReset) {
        return (
            <div>Your password has been reset, you may login now</div>
        )
    } else {
        return (
            <Form onFinish={formik.handleSubmit}>
                <Form.Item>
                    <Input.Password
                        placeholder="New password"
                        name="password"
                        onChange={formik.handleChange}
                        value={formik.values.password}
                    />
                </Form.Item>
                <Form.Item>
                    <Button type="primary" htmlType="submit" disabled={loading}>
                        reset password
                    </Button>
                </Form.Item>
            </Form>
        )
    }
};