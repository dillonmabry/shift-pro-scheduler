import Container from "../../components/container/Container";
import React from "react";
import { Table, Space, Spin, Empty } from "antd";
import EmployeeService from "../../services/EmployeeService";
import AuthService from "../../services/AuthService";

const { Column } = Table;

class Employees extends React.Component {
  state = {
    employees: [],
    loading: true,
  };

  componentDidMount() {
    const user = AuthService.getCurrentUser();
    if (user) {
      EmployeeService.getEmployeesBySupervisor(user.username)
        .then(
          (response) => {
            if (response.data) {
              const _employees = [];
              response.data.employeeList.forEach((employee) => {
                _employees.push({
                  key: employee.id,
                  firstName: employee.firstName,
                  lastName: employee.lastName,
                  email: employee.email,
                  phone: employee.phone,
                  department: employee.department
                    ? employee.department.name
                    : "N/A",
                });
              });
              this.setState({
                //eslint-disable-line
                employees: _employees,
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
  render() {
    return (
      <Container
        content={
          <div>
            {this.state.loading && <Spin />}
            {!this.state.loading && (
              <div>
                {this.state.employees.length > 0 ? (
                  <Table dataSource={this.state.employees}>
                    <Column
                      title="First Name"
                      dataIndex="firstName"
                      key="firstName"
                    />
                    <Column
                      title="Last Name"
                      dataIndex="lastName"
                      key="lastName"
                    />
                    <Column title="Email" dataIndex="email" key="email" />
                    <Column title="Phone" dataIndex="phone" key="phone" />
                    <Column
                      title="Department"
                      dataIndex="department"
                      key="department"
                    />
                    <Column
                      title="Action"
                      key="action"
                      render={(text, record) => (
                        <Space size="middle">
                          <a>Invite {record.firstName}</a>
                          <a>Delete</a>
                        </Space>
                      )}
                    />
                  </Table>
                ) : (
                  <Empty />
                )}
              </div>
            )}
          </div>
        }
      ></Container>
    );
  }
}

export default Employees;
