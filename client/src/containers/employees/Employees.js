import Container from "../../components/container/Container";
import React, { useState, useEffect } from "react";
import { Table, Space, Spin, Empty } from "antd";
import EmployeeService from "../../services/EmployeeService";
import AuthService from "../../services/AuthService";
import NotificationService from "../../services/NotificationService";

const { Column } = Table;

const Employees = () => {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const user = AuthService.getCurrentUser();
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
                userName: employee.userName,
                department: employee.department
                  ? employee.department.name
                  : "N/A",
              });
            });
            setEmployees(_employees);
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
  }, []);

  return (
    <Container
      content={
        <div>
          {loading && <Spin />}
          {!loading && (
            <div>
              {employees.length > 0 ? (
                <Table dataSource={employees}>
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
                        {!record.userName && <a>Invite {record.firstName}</a>}
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
};

export default Employees;
