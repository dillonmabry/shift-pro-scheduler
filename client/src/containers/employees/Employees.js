import Container from "../../components/container/Container";
import React, { useState, useEffect } from "react";
import { Spin } from "antd";
import EmployeeService from "../../services/EmployeeService";
import AuthService from "../../services/AuthService";
import NotificationService from "../../services/NotificationService";
import DataTable from "../../components/data-table/DataTable";
import DepartmentService from "../../services/DepartmentService";
import AdministratorService from "../../services/AdministratorService";
import VALIDATIONS from "../../constants/Regex";

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
      rules: [
        {
          required: true,
          message: "Username is required.",
        },
      ],
    },
    {
      title: "First Name",
      dataIndex: "firstName",
      width: "20%",
      editable: true,
      rules: [
        {
          required: true,
          message: "First Name is required.",
        },
      ],
    },
    {
      title: "Last Name",
      dataIndex: "lastName",
      width: "20%",
      editable: true,
      rules: [
        {
          required: true,
          message: "Last Name is required.",
        },
      ],
    },
    {
      title: "Email",
      dataIndex: "email",
      width: "20%",
      editable: true,
      dataType: "email",
      rules: [
        {
          type: "email",
          message: "Email must be a valid format!",
        },
        {
          required: true,
          message: "Email is required.",
        },
      ],
    },
    {
      title: "Phone",
      dataIndex: "phone",
      width: "20%",
      editable: true,
      dataType: "phone",
      rules: [
        {
          required: true,
          message: "Phone is required.",
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
      ],
    },
    {
      title: "Department",
      dataIndex: ["department", "name"],
      width: "20%",
      editable: true,
      dataType: "complex",
      optionsData: departmentsOptionsData.map((d) => d.name),
      complexOptionsData: departmentsOptionsData,
      rules: [
        {
          required: true,
          message: "Department is required.",
        },
      ],
    },
    {
      title: "Supervisor",
      dataIndex: ["supervisor", "userName"],
      width: "20%",
      editable: true,
      dataType: "complex",
      optionsData: supervisorsOptionsData.map((s) => s.userName),
      complexOptionsData: supervisorsOptionsData,
      rules: [
        {
          required: true,
          message: "Supervisor is required.",
        },
      ],
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
    EmployeeService.getEmployeesBySupervisor(user.userName)
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
              <DataTable
                dataSource={employees}
                columns={columns}
                handleDelete={handleDelete}
                handleSave={handleSave}
              />
            </div>
          )}
        </div>
      }
    ></Container>
  );
};

export default Employees;
