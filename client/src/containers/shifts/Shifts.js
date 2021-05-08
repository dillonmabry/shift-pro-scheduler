import Container from "../../components/container/Container";
import React, { useState, useEffect } from "react";
import { Spin, Empty } from "antd";
import ShiftService from "../../services/ShiftService";
import NotificationService from "../../services/NotificationService";
import DataTable from "../../components/data-table/DataTable";

const Shifts = () => {
  const [shifts, setShifts] = useState([]);
  const [loading, setLoading] = useState(true);

  const columns = [
    {
      title: 'Start Time',
      dataIndex: 'startTime',
      width: '50%',
      editable: true,
      dataType: 'time'
    },
    {
      title: 'End Time',
      dataIndex: 'endTime',
      width: '50%',
      editable: true,
      dataType: 'time'
    },
  ];

  const handleDelete = (key) => {
    return ShiftService.deleteShift(key)
  };
  const handleSave = (row) => {
    return ShiftService.saveShift(row);
  };

  useEffect(() => {
    ShiftService.getShifts()
      .then(
        (response) => {
          if (response.data) {
            setShifts(response.data.shiftsList.map(item => ({ ...item, key: item.id })));
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
      navItems={['Home', 'Shifts']}
      content={
        <div>
          {loading && <Spin />}
          {!loading && (
            <div>
              {shifts.length > 0 ? (
                <DataTable
                  dataSource={shifts}
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

export default Shifts;
