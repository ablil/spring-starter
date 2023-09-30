import { Button, Form, Input } from "antd";
import { useFormik } from "formik";
import { useDispatch } from "react-redux";
import { loginAction } from "../redux/actions";

const LoginPage = () => {
  const dispatch = useDispatch();

  const formik = useFormik({
    initialValues: {
      username: "",
      password: "",
    },
    onSubmit: (values) => {
      dispatch(
        loginAction({ username: values.username, password: values.password })
      );
    },
  });

  return (
    <div>
      <Form onFinish={formik.handleSubmit}>
        <Form.Item>
          <Input
            placeholder="username or email"
            name="username"
            onChange={formik.handleChange}
            value={formik.values.username}
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
          <Button type="primary" htmlType="submit">
            login
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default LoginPage;
