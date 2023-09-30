import {Button, Form, Input} from "antd";
import {useFormik} from "formik";
import {useDispatch, useSelector} from "react-redux";
import {registerAction} from "../redux/actions.ts";
import {RootAppState} from "../../redux/store.ts";

const RegistrationPage = () => {
    const dispatch = useDispatch()

    const {loading, registered} = useSelector((app: RootAppState) => app.auth)

    const formik = useFormik({
        initialValues: {
            username: '',
            email: '',
            password: ''
        },
        onSubmit: (values) => {
            dispatch(registerAction(values))
        }
    })

    if (registered) {
        return (
            <div>you account have been create, please check your email for confirmation</div>
        )
    } else {
        return (

            <Form onFinish={formik.handleSubmit}>
                <Form.Item>
                    <Input
                        placeholder="Username"
                        name="username"
                        onChange={formik.handleChange}
                        value={formik.values.username}
                    />
                </Form.Item>
                <Form.Item>
                    <Input
                        placeholder="email"
                        name="email"
                        onChange={formik.handleChange}
                        value={formik.values.email}
                    />
                </Form.Item>
                <Form.Item>
                    <Input.Password
                        placeholder="**********"
                        name="password"
                        onChange={formik.handleChange}
                        value={formik.values.password}
                    />
                </Form.Item>
                <Form.Item>
                    <Button type="primary" htmlType="submit" disabled={loading}>
                        register
                    </Button>
                </Form.Item>
            </Form>
        )
    }

}

export default RegistrationPage