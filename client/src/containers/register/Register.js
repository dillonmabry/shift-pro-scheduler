import "./Register.css";
import React from "react";
import Container from "../../components/container/Container";
import { Form, Input, Button, Card } from "antd";
import { UserOutlined, LockOutlined } from "@ant-design/icons";
import NotificationService from "../../services/NotificationService";
import PropTypes from "prop-types";
import AuthService from "../../services/AuthService";

const Register = (props) => {
  const [form] = Form.useForm();

  const onFinish = (values) => {
    AuthService.register(values.username, values.password)
      .then(() => {
        NotificationService.notify("success", "Successfully registered");
        props.history.push("/");
      })
      .catch((err) => {
        if (err.response) {
          NotificationService.notify(
            "error",
            "Something went wrong with your registration please try again"
          );
        }
      });
  };

  return (
    <Container
      navItems={["Home", "Register"]}
      content={
        <Card>
          <Form
            layout="horizontal"
            form={form}
            name="register"
            onFinish={onFinish}
            scrollToFirstError
          >
            <Form.Item
              name="username"
              rules={[
                {
                  required: true,
                  message: "Please input your desired Username!",
                },
              ]}
            >
              <Input
                placeholder="Enter registration Username"
                prefix={<UserOutlined className="site-form-item-icon" />}
              />
            </Form.Item>

            <Form.Item
              name="password"
              rules={[
                {
                  required: true,
                  message: "Please input your password!",
                },
              ]}
              hasFeedback
            >
              <Input.Password
                type="password"
                placeholder="Enter desired password"
                prefix={<LockOutlined className="site-form-item-icon" />}
              />
            </Form.Item>

            <Form.Item
              name="confirm"
              dependencies={["password"]}
              hasFeedback
              rules={[
                {
                  required: true,
                  message: "Please confirm your password!",
                },
                ({ getFieldValue }) => ({
                  validator(rule, value) {
                    if (!value || getFieldValue("password") === value) {
                      return Promise.resolve();
                    }
                    return Promise.reject(
                      new Error(
                        "The two passwords that you entered do not match!"
                      )
                    );
                  },
                }),
              ]}
            >
              <Input.Password
                type="password"
                placeholder="Confirm password"
                prefix={<LockOutlined className="site-form-item-icon" />}
              />
            </Form.Item>

            <Form.Item>
              <Button
                type="primary"
                htmlType="submit"
                className="register-form-button"
              >
                Register
              </Button>
            </Form.Item>
          </Form>
        </Card>
      }
    ></Container>
  );
};

Register.propTypes = {
  history: PropTypes.object,
};

export default Register;
