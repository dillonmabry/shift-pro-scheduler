import React from "react";
import { Form, DatePicker, Button, InputNumber, Radio } from "antd";
import ScheduleService from "../../services/ScheduleService";
import NotificationService from "../../services/NotificationService";
import PropTypes from "prop-types";

const { RangePicker } = DatePicker;

const rangeConfig = {
  rules: [{ type: "array", required: true, message: "Please select time!" }],
};

const CreateSchedule = (props) => {
  const onFinish = (fieldsValue) => {
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
    const optimizer = values["inputOptimizer"];

    if (inputNumber && optimizer && startDate && endDate) {
      props.setLoading(true);
      ScheduleService.postSchedules(optimizer, inputNumber, startDate, endDate)
        .then(
          (response) => {
            if (response.data && response.data.assignmentList.length > 0) {
              props.updateEventsList();
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
          props.setLoading(false);
        });
    }
  };

  return (
    <Form
      name="time_related_controls"
      layout={"inline"}
      onFinish={onFinish}
      initialValues={{
        inputNumber: 1,
        inputOptimizer: "DefaultOptimizer",
      }}
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
        <Form.Item label="Generator Type">
          <Form.Item name="inputOptimizer" noStyle>
            <Radio.Group buttonStyle="solid">
              <Radio.Button value="DefaultOptimizer">Default</Radio.Button>
              <Radio.Button value="PreferenceOptimizer">
                Preferences
              </Radio.Button>
            </Radio.Group>
          </Form.Item>
        </Form.Item>
      </Form.Item>
      <Form.Item>
        <Button type="primary" htmlType="submit">
          Generate
        </Button>
      </Form.Item>
    </Form>
  );
};

CreateSchedule.propTypes = {
  updateEventsList: PropTypes.func,
  setLoading: PropTypes.func,
};

export default CreateSchedule;
