import React from "react";
import { Form, DatePicker, Button, InputNumber } from "antd";
import ScheduleService from "../../services/ScheduleService";
import NotificationService from "../../services/NotificationService";
import PropTypes from "prop-types";

const { RangePicker } = DatePicker;

const rangeConfig = {
  rules: [{ type: "array", required: true, message: "Please select time!" }],
};

export default class CreateSchedule extends React.Component {
  state = {
    loading: false,
  };

  onFinish = (fieldsValue) => {
    const rangeValue = fieldsValue["rangePicker"];
    const values = {
      ...fieldsValue,
      rangePicker: [
        rangeValue[0].format("YYYY-MM-DD"),
        rangeValue[1].format("YYYY-MM-DD"),
      ],
    };
    const inputNumber = values["inputNumber"];
    const startDate = values["rangePicker"][0];
    const endDate = values["rangePicker"][1];

    if (inputNumber && startDate && endDate) {
      this.setState({
        loading: true,
      });
      ScheduleService.postSchedules(inputNumber, startDate, endDate)
        .then(
          (response) => {
            if (response.data && response.data.assignmentList.length > 0) {
              this.props.updateEventsList();
              NotificationService.notify(
                "success",
                "Successfully created schedules"
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
          this.setState({
            loading: false,
          });
        });
    }
  };

  render() {
    return (
      <Form
        name="time_related_controls"
        layout={"inline"}
        onFinish={this.onFinish}
        initialValues={{ inputNumber: 1 }}
      >
        <Form.Item name="rangePicker" label="Start-End Dates" {...rangeConfig}>
          <RangePicker />
        </Form.Item>
        <Form.Item>
          <Form.Item label="Number of Schedules">
            <Form.Item name="inputNumber" noStyle>
              <InputNumber min={1} max={10} />
            </Form.Item>
          </Form.Item>
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">
            Generate Schedules
          </Button>
        </Form.Item>
      </Form>
    );
  }
}

CreateSchedule.propTypes = {
  updateEventsList: PropTypes.func,
};
