import React, { useState, useEffect } from "react";
import Container from "../../components/container/Container";
import { Spin, Empty } from "antd";
import DepartmentService from "../../services/DepartmentService";
import NotificationService from "../../services/NotificationService";
import DataTable from "../../components/data-table/DataTable";

const Departments = () => {
  const [departments, setDepartments] = useState([]);
  const [loading, setLoading] = useState(true);

  const columns = [
    {
      title: "Name",
      dataIndex: "name",
      width: "80%",
      editable: true,
    },
  ];

  const handleDelete = (key) => {
    return DepartmentService.deleteDepartment(key);
  };
  const handleSave = (row) => {
    return DepartmentService.saveDepartment(row);
  };

  useEffect(() => {
    DepartmentService.getDepartments()
      .then(
        (response) => {
          if (response.data) {
            setDepartments(
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
      )
      .then(() => {
        setLoading(false);
      });
  }, []);

  return (
    <Container
      navItems={["Home", "Departments"]}
      content={
        <div>
          {loading && <Spin />}
          {!loading && (
            <div>
              {departments.length > 0 ? (
                <DataTable
                  dataSource={departments}
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

export default Departments;
