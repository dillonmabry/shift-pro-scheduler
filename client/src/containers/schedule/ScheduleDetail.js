import React from "react";
import { Form, Button, Modal } from "antd";
import PropTypes from "prop-types";
import ScheduleService from "../../services/ScheduleService";
import NotificationService from "../../services/NotificationService";

const ScheduleDetail = (props) => {
  const onActivate = () => {
    Modal.confirm({
      content: "Are you sure you wish to activate this schedule?",
      title: "Confirm Activate",
      cancelText: "No",
      okText: "Yes",
      onOk: () => {
        props.setLoading(true);
        ScheduleService.activateSchedule(props.schedule.id)
          .then(
            () => {
              props.updateEventsList();
              NotificationService.notify(
                "success",
                "Successfully activated schedule"
              );
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
            props.setLoading(false);
          });
      },
      onCancel: () => {},
    });
  };

  const onDelete = () => {
    Modal.confirm({
      content: "Are you sure you wish to delete this schedule?",
      title: "Confirm Delete",
      cancelText: "No",
      okText: "Yes",
      onOk: () => {
        props.setLoading(true);
        ScheduleService.deleteSchedule(props.schedule.id)
          .then(
            () => {
              props.updateEventsList();
              NotificationService.notify(
                "success",
                "Successfully deleted schedule"
              );
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
            props.setLoading(false);
          });
      },
      onCancel: () => {},
    });
  };

  return (
    <Form name="schedule_controls" layout={"inline"}>
      {!props.schedule.isActive && (
        <Form.Item>
          <Button type="primary" htmlType="submit" onClick={onActivate}>
            Mark Active
          </Button>
        </Form.Item>
      )}
      <Form.Item>
        <Button type="primary" htmlType="submit" onClick={onDelete} danger>
          Delete
        </Button>
      </Form.Item>
    </Form>
  );
};

ScheduleDetail.propTypes = {
  schedule: PropTypes.object,
  updateEventsList: PropTypes.func,
  setLoading: PropTypes.func,
};

export default ScheduleDetail;
