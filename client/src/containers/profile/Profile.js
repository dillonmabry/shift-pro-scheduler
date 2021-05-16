import React, { useState, useEffect } from "react";
import { Form, Input, Button, Card, Spin } from "antd";
import "./Profile.css";
import PropTypes from "prop-types";
import Container from "../../components/container/Container";
import EmployeeService from "../../services/EmployeeService";
import AdministratorService from "../../services/AdministratorService";
import AuthService from "../../services/AuthService";
import NotificationService from "../../services/NotificationService";
import ROLES from "../../constants/Roles";
import VALIDATIONS from "../../constants/Regex";

const DescriptionItem = ({ title, content }) => (
  <div className="site-description-item-profile-wrapper">
    <p className="site-description-item-profile-p-label">{title}:</p>
    {content}
  </div>
);

DescriptionItem.propTypes = {
  title: PropTypes.string,
  content: PropTypes.string,
};

const Profile = () => {
  const [userInfo, setUserInfo] = useState(null);
  const [loading, setLoading] = useState(true);

  const formItemLayout = {
    labelCol: {
      xs: { span: 24 },
      sm: { span: 8 },
    },
    wrapperCol: {
      xs: { span: 24 },
      sm: { span: 16 },
    },
  };
  const tailFormItemLayout = {
    wrapperCol: {
      xs: {
        span: 24,
        offset: 0,
      },
      sm: {
        span: 16,
        offset: 8,
      },
    },
  };
  const [form] = Form.useForm();

  const onFinish = (values) => {
    const formData = {
      ...userInfo,
      firstName: values.firstName,
      lastName: values.lastName,
      phone: values.phone,
      email: values.email,
    };
    handleSave(formData);
  };

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    if (AuthService.getRoles(user.authorities).includes(ROLES.User)) {
      EmployeeService.getEmployee(user.username)
        .then(
          (response) => {
            if (response.data) {
              setUserInfo({
                ...response.data,
                username: user.username,
                authorities: user.authorities,
              });
            }
          },
          (error) => {
            NotificationService.notify(
              "error",
              (error.response &&
                error.response.data &&
                error.response.data.message) ||
                error.message ||
                error.toString()
            );
          }
        )
        .then(() => {
          setLoading(false);
        });
    }
    if (AuthService.getRoles(user.authorities).includes(ROLES.Admin)) {
      AdministratorService.getAdministrator(user.username)
        .then(
          (response) => {
            if (response.data) {
              setUserInfo({
                ...response.data,
                username: user.username,
                authorities: user.authorities,
              });
            }
          },
          (error) => {
            NotificationService.notify(
              "error",
              (error.response &&
                error.response.data &&
                error.response.data.message) ||
                error.message ||
                error.toString()
            );
          }
        )
        .then(() => {
          setLoading(false);
        });
    }
  }, []);

  const handleSave = (profileData) => {
    if (AuthService.getRoles(userInfo.authorities).includes(ROLES.Admin)) {
      AdministratorService.saveAdministratorProfile(
        userInfo.username,
        profileData
      ).then(
        (response) => {
          if (response.data) {
            setUserInfo({ ...response.data });
            NotificationService.notify("success", "Successfully saved profile");
          }
        },
        (error) => {
          NotificationService.notify(
            "error",
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
              error.message ||
              error.toString()
          );
        }
      );
    }
    if (AuthService.getRoles(userInfo.authorities).includes(ROLES.User)) {
      EmployeeService.saveEmployeeProfile(userInfo.username, profileData).then(
        (response) => {
          if (response.data) {
            setUserInfo({ ...response.data });
            NotificationService.notify("success", "Successfully saved profile");
          }
        },
        (error) => {
          NotificationService.notify(
            "error",
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
              error.message ||
              error.toString()
          );
        }
      );
    }
  };

  return (
    <Container
      navItems={["Home", "Profile"]}
      content={
        <div>
          {loading && <Spin />}
          {!loading && (
            <div>
              {userInfo && (
                <Card style={{ width: "75%" }}>
                  <Form
                    {...formItemLayout}
                    form={form}
                    name="profile"
                    onFinish={onFinish}
                    initialValues={{
                      username: userInfo.username ? userInfo.username : "",
                      firstName: userInfo.firstName ? userInfo.firstName : "",
                      lastName: userInfo.lastName ? userInfo.lastName : "",
                      email: userInfo.email ? userInfo.email : "",
                      phone: userInfo.phone ? userInfo.phone : "",
                      department: userInfo.department
                        ? userInfo.department.name
                        : "",
                      supervisor: userInfo.supervisor
                        ? userInfo.supervisor.firstName +
                          ", " +
                          userInfo.supervisor.lastName
                        : "",
                    }}
                    scrollToFirstError
                  >
                    <Form.Item name="username" label="Username">
                      <Input disabled={true} />
                    </Form.Item>

                    <Form.Item
                      name="firstName"
                      label="First Name"
                      rules={[
                        {
                          required: true,
                          message: "Please input your First Name!",
                          whitespace: true,
                        },
                      ]}
                    >
                      <Input />
                    </Form.Item>
                    <Form.Item
                      name="lastName"
                      label="Last Name"
                      rules={[
                        {
                          required: true,
                          message: "Please input your Last Name!",
                          whitespace: true,
                        },
                      ]}
                    >
                      <Input />
                    </Form.Item>

                    <Form.Item
                      name="email"
                      label="Email"
                      rules={[
                        {
                          type: "email",
                          message: "Email must be a valid format!",
                        },
                        {
                          required: true,
                          message: "Email is required.",
                        },
                      ]}
                    >
                      <Input />
                    </Form.Item>

                    <Form.Item
                      name="phone"
                      label="Phone Number"
                      rules={[
                        {
                          required: true,
                          message: "Please input your phone number!",
                        },
                        () => ({
                          validator(rule, value) {
                            if (!value || VALIDATIONS.Phone.test(value)) {
                              return Promise.resolve();
                            }
                            return Promise.reject(
                              new Error("Phone number must be a valid format!")
                            );
                          },
                        }),
                      ]}
                    >
                      <Input />
                    </Form.Item>

                    <Form.Item name="department" label="Department">
                      <Input disabled={true} />
                    </Form.Item>

                    <Form.Item name="supervisor" label="Supervisor">
                      <Input disabled={true} />
                    </Form.Item>

                    <Form.Item {...tailFormItemLayout}>
                      <Button type="primary" htmlType="submit">
                        Save
                      </Button>
                    </Form.Item>
                  </Form>
                </Card>
              )}
            </div>
          )}
        </div>
      }
    ></Container>
  );
};

export default Profile;
