import "./Login.css";
import Container from "../../components/container/Container";
import React from "react";
import { Form, Input, Button, Card } from "antd";
import { UserOutlined, LockOutlined } from "@ant-design/icons";
import NotificationService from "../../services/NotificationService";
import { Link } from "react-router-dom";
import PropTypes from "prop-types";
import AuthService from "../../services/AuthService";

const Login = (props) => {
  const onFinish = (values) => {
    AuthService.login(values.username, values.password)
      .then(() => {
        NotificationService.notify("success", "Successfully logged in");
        props.history.push("/");
      })
      .catch((err) => {
        if (err.response) {
          NotificationService.notify(
            "error",
            "Something went wrong with your login please try again"
          );
        }
      });
  };

  return (
    <Container
      content={
        <Card>
          <Form name="normal_login" className="login-form" onFinish={onFinish}>
            <Form.Item
              name="username"
              rules={[
                {
                  required: true,
                  message: "Please input your Username!",
                },
              ]}
            >
              <Input
                prefix={<UserOutlined className="site-form-item-icon" />}
                placeholder="Username"
              />
            </Form.Item>
            <Form.Item
              name="password"
              rules={[
                {
                  required: true,
                  message: "Please input your Password!",
                },
              ]}
            >
              <Input.Password
                prefix={<LockOutlined className="site-form-item-icon" />}
                type="password"
                placeholder="Password"
              />
            </Form.Item>
            <Form.Item>
              <Button
                type="primary"
                htmlType="submit"
                className="login-form-button"
              >
                Log in
              </Button>
              <Link to="/register">Register now</Link> Or{" "}
              <Link to="/forgot-password">Forgot password</Link>
            </Form.Item>
          </Form>
        </Card>
      }
    ></Container>
  );
};

Login.propTypes = {
  history: PropTypes.object,
};

export default Login;
