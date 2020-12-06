import React from "react";
import { Col, Row, Card, Spin } from "antd";
import "./Profile.css";
import PropTypes from "prop-types";
import Container from "../../components/container/Container";
import EmployeeService from "../../services/EmployeeService";
import AdministratorService from "../../services/AdministratorService";
import AuthService from "../../services/AuthService";

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

class Profile extends React.Component {
  state = {
    userInfo: null,
    loading: true,
  };

  componentDidMount() {
    const user = AuthService.getCurrentUser();
    if (user) {
      if (AuthService.getRoles(user.authorities).includes("ROLE_USER")) {
        EmployeeService.getEmployee(user.username)
          .then(
            (response) => {
              if (response.data) {
                this.setState({
                  //eslint-disable-line
                  userInfo: { ...response.data, username: user.username },
                });
              }
            },
            (error) => {
              this.setState({
                //eslint-disable-line
                content:
                  (error.response &&
                    error.response.data &&
                    error.response.data.message) ||
                  error.message ||
                  error.toString(),
              });
            }
          )
          .then(() => {
            this.setState({
              loading: false,
            });
          });
      }
      if (AuthService.getRoles(user.authorities).includes("ROLE_ADMIN")) {
        AdministratorService.getAdministrator(user.username)
          .then(
            (response) => {
              if (response.data) {
                this.setState({
                  //eslint-disable-line
                  userInfo: { ...response.data, username: user.username },
                });
              }
            },
            (error) => {
              this.setState({
                //eslint-disable-line
                content:
                  (error.response &&
                    error.response.data &&
                    error.response.data.message) ||
                  error.message ||
                  error.toString(),
              });
            }
          )
          .then(() => {
            this.setState({
              loading: false,
            });
          });
      }
    }
  }

  render() {
    return (
      <Container
        content={
          <div>
            {this.state.loading && <Spin />}
            {!this.state.loading && (
              <div>
                {this.state.userInfo && (
                  <Card title="User Profile" style={{ width: "75%" }}>
                    <p className="site-description-item-profile-p">Personal</p>
                    <Row>
                      <Col xs={4} sm={8} md={12}>
                        <DescriptionItem
                          title="Full Name"
                          content={`${this.state.userInfo.firstName} ${this.state.userInfo.lastName}`}
                        />
                      </Col>
                      <Col xs={4} sm={8} md={12}>
                        <DescriptionItem
                          title="Email"
                          content={this.state.userInfo.email}
                        />
                      </Col>
                    </Row>
                    <Row>
                      <Col xs={4} sm={8} md={12}>
                        <DescriptionItem
                          title="User Name"
                          content={this.state.userInfo.username}
                        />
                      </Col>
                      <Col xs={4} sm={8} md={12}>
                        <DescriptionItem
                          title="Phone"
                          content={
                            this.state.userInfo.phone
                              ? this.state.userInfo.phone
                              : "N/A"
                          }
                        />
                      </Col>
                    </Row>
                    <Row>
                      <Col xs={4} sm={8} md={12}>
                        <DescriptionItem
                          title="Department"
                          content={
                            this.state.userInfo.department
                              ? this.state.userInfo.department.name
                              : "N/A"
                          }
                        />
                      </Col>
                      <Col xs={4} sm={8} md={12}>
                        <DescriptionItem
                          title="Supervisor"
                          content={
                            this.state.userInfo.supervisor
                              ? `${this.state.userInfo.supervisor.firstName} ${this.state.userInfo.supervisor.lastName}`
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
  }
}

export default Profile;
