import React, { useState, useEffect } from "react";
import { Col, Row, Card, Spin } from "antd";
import "./Profile.css";
import PropTypes from "prop-types";
import Container from "../../components/container/Container";
import EmployeeService from "../../services/EmployeeService";
import AdministratorService from "../../services/AdministratorService";
import AuthService from "../../services/AuthService";
import NotificationService from "../../services/NotificationService";
import ROLES from "../../constants/Roles";

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

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    if (AuthService.getRoles(user.authorities).includes(ROLES.User)) {
      EmployeeService.getEmployee(user.username)
        .then(
          (response) => {
            if (response.data) {
              setUserInfo({ ...response.data, username: user.username });
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
              setUserInfo({ ...response.data, username: user.username });
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

  return (
    <Container
      navItems={['Home', 'Profile']}
      content={
        <div>
          {loading && <Spin />}
          {!loading && (
            <div>
              {userInfo && (
                <Card title="Employee Info" style={{ width: "75%" }}>
                  <Row>
                    <Col xs={4} sm={8} md={12}>
                      <DescriptionItem
                        title="Full Name"
                        content={`${userInfo.firstName} ${userInfo.lastName}`}
                      />
                    </Col>
                    <Col xs={4} sm={8} md={12}>
                      <DescriptionItem title="Email" content={userInfo.email} />
                    </Col>
                  </Row>
                  <Row>
                    <Col xs={4} sm={8} md={12}>
                      <DescriptionItem
                        title="Username"
                        content={userInfo.username}
                      />
                    </Col>
                    <Col xs={4} sm={8} md={12}>
                      <DescriptionItem
                        title="Phone"
                        content={userInfo.phone ? userInfo.phone : "N/A"}
                      />
                    </Col>
                  </Row>
                  <Row>
                    <Col xs={4} sm={8} md={12}>
                      <DescriptionItem
                        title="Department"
                        content={
                          userInfo.department ? userInfo.department.name : "N/A"
                        }
                      />
                    </Col>
                    <Col xs={4} sm={8} md={12}>
                      <DescriptionItem
                        title="Supervisor"
                        content={
                          userInfo.supervisor
                            ? `${userInfo.supervisor.firstName} ${userInfo.supervisor.lastName}`
                            : "N/A"
                        }
                      />
                    </Col>
                  </Row>
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
