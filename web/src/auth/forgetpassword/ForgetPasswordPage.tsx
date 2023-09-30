import {useFormik} from "formik";
import {Button, Form, Input} from "antd";
import {useDispatch, useSelector} from "react-redux";
import {forgetPasswordAction} from "../redux/actions.ts";
import {RootAppState} from "../../redux/store.ts";

export const ForgetPasswordPage = () => {
    const dispatch = useDispatch()
    const {resetPasswordLinkSent} = useSelector((app: RootAppState) => app.auth)

    const formik = useFormik({
        initialValues: {
            email: ''
        },
        onSubmit: (values) => {
            dispatch(forgetPasswordAction(values.email))
        }
    })

    if (resetPasswordLinkSent) {
        return (<div>
            If there is an account with this email, you should receive reset link via email
        </div>)
    } else {
        return (
            <div>
                <Form onFinish={formik.handleSubmit}>
                    <Form.Item>
                        <Input
                            placeholder="Your email"
                            name="email"
                            onChange={formik.handleChange}
                            value={formik.values.email}
                        />
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" htmlType="submit">
                            send reset link
                        </Button>
                    </Form.Item>
                </Form>
            </div>
        )
    }
}