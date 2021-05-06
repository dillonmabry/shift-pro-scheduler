import Container from "../../components/container/Container";
import React, { useState, useEffect } from "react";
import { Table, Space, Spin, Empty } from "antd";
import ShiftService from "../../services/ShiftService";
import NotificationService from "../../services/NotificationService";

const { Column } = Table;

const Shifts = () => {
  const [shifts, setShifts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    ShiftService.getShifts()
      .then(
        (response) => {
          if (response.data) {
            setShifts(response.data.shiftsList);
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
              {shifts.length > 0 ? (
                <Table dataSource={shifts}>
                  <Column
                    title="Start Time"
                    dataIndex="startTime"
                    key="startTime"
                  />
                  <Column
                    title="End Time"
                    dataIndex="endTime"
                    key="endTime"
                  />
                  <Column
                    title="Action"
                    key="action"
                    render={(text, record) => (
                      <Space size="middle">
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

export default Shifts;
