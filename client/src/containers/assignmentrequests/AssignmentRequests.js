import React, { useState, useEffect } from "react";
import Container from "../../components/container/Container";
import { Spin } from "antd";
import AssignmentRequestService from "../../services/AssignmentRequestService";
import ShiftService from "../../services/ShiftService";
import NotificationService from "../../services/NotificationService";
import DataTable from "../../components/data-table/DataTable";
import AuthService from "../../services/AuthService";
import ShiftDayService from "../../services/ShiftDayService";

const AssignmentRequests = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [shiftsOptionsData, setShiftsOptionsData] = useState([]);
  const [shiftDaysOptionsData, setShiftDaysOptionsData] = useState([]);

  const columns = [
    {
      title: "Shift",
      dataIndex: ["shift", "shiftTime"],
      width: "20%",
      editable: true,
      dataType: "complex",
      optionsData: shiftsOptionsData.map((d) => d.shiftTime),
      complexOptionsData: shiftsOptionsData,
      rules: [
        {
          required: true,
          message: "Start shift time is required.",
        },
      ],
    },
    {
      title: "Day",
      dataIndex: ["shiftDay", "name"],
      width: "20%",
      editable: true,
      dataType: "complex",
      optionsData: shiftDaysOptionsData.map((d) => d.name),
      complexOptionsData: shiftDaysOptionsData,
      rules: [
        {
          required: true,
          message: "Shift day is required.",
        },
      ],
    },
  ];

  const handleDelete = (key) => {
    return AssignmentRequestService.deleteAssignmentRequest(key);
  };
  const handleSave = (row) => {
    return AssignmentRequestService.saveAssignmentRequests(row);
  };

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    Promise.all([
      ShiftService.getShifts().then(
        (response) => {
          if (response.data) {
            setShiftsOptionsData(
              response.data.shiftsList.map((item) => ({
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
      ),
      ShiftDayService.getShiftDays().then(
        (response) => {
          if (response.data) {
            setShiftDaysOptionsData(
              response.data.shiftDaysList.map((item) => ({
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
      ),
      AssignmentRequestService.getAssignmentRequests(user.userName).then(
        (response) => {
          if (response.data) {
            setRequests(
              response.data.assignmentRequestList.map((item) => ({
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
      ),
    ]).then(() => {
      setLoading(false);
    });
  }, []);

  return (
    <Container
      navItems={["Home", "Shift Requests"]}
      content={
        <div>
          {loading && <Spin size="large" />}
          {!loading && (
            <div>
              <DataTable
                dataSource={requests}
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

export default AssignmentRequests;
