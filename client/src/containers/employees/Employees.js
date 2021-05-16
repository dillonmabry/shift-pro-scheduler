import Container from "../../components/container/Container";
import React, { useState, useEffect } from "react";
import { Spin, Empty } from "antd";
import EmployeeService from "../../services/EmployeeService";
import AuthService from "../../services/AuthService";
import NotificationService from "../../services/NotificationService";
import DataTable from "../../components/data-table/DataTable";
import DepartmentService from "../../services/DepartmentService";
import AdministratorService from "../../services/AdministratorService";

const Employees = () => {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [supervisorsOptionsData, setSupervisorsOptionsData] = useState([]);
  const [departmentsOptionsData, setDepartmentsOptionsData] = useState([]);

  const columns = [
    {
      title: "Username",
      dataIndex: "userName",
      width: "20%",
      editable: true,
    },
    {
      title: "First Name",
      dataIndex: "firstName",
      width: "20%",
      editable: true,
    },
    {
      title: "Last Name",
      dataIndex: "lastName",
      width: "20%",
      editable: true,
    },
    {
      title: "Email",
      dataIndex: "email",
      width: "20%",
      editable: true,
    },
    {
      title: "Phone",
      dataIndex: "phone",
      width: "20%",
      editable: true,
    },
    {
      title: "Department",
      dataIndex: ["department", "name"],
      width: "20%",
      editable: true,
      dataType: "complex",
      optionsData: departmentsOptionsData.map((d) => d.name),
      complexOptionsData: departmentsOptionsData,
    },
    {
      title: "Supervisor",
      dataIndex: ["supervisor", "userName"],
      width: "20%",
      editable: true,
      dataType: "complex",
      optionsData: supervisorsOptionsData.map((s) => s.userName),
      complexOptionsData: supervisorsOptionsData,
    },
  ];

  const handleDelete = (key) => {
    return EmployeeService.deleteEmployee(key);
  };
  const handleSave = (row) => {
    return EmployeeService.saveEmployee(row);
  };

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    EmployeeService.getEmployeesBySupervisor(user.username)
      .then(
        (response) => {
          if (response.data) {
            setEmployees(
              response.data.employeeList.map((item) => ({
                ...item,
                key: item.id,
              }))
            );
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
    DepartmentService.getDepartments().then(
      (response) => {
        if (response.data) {
          setDepartmentsOptionsData(
            response.data.departmentsList.map((item) => ({
              ...item,
              key: item.id,
            }))
          );
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
    AdministratorService.getAdministrators().then(
      (response) => {
        if (response.data) {
          setSupervisorsOptionsData(
            response.data.administratorList.map((item) => ({
              ...item,
              key: item.id,
            }))
          );
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
  }, []);

  return (
    <Container
      navItems={["Home", "Employees"]}
      content={
        <div>
          {loading && <Spin />}
          {!loading && (
            <div>
              {employees.length > 0 ? (
                <DataTable
                  dataSource={employees}
                  columns={columns}
                  handleDelete={handleDelete}
                  handleSave={handleSave}
                />
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
