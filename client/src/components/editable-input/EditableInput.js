import React from "react";
import { Form, Input, Select } from "antd";
import PropTypes from "prop-types";

const { Option } = Select;

const EditableInput = ({
  title,
  dataIndex,
  handleSave,
  inputRef,
  inputType,
  optionsData,
}) => {
  switch (inputType) {
    case "string":
      return (
        <Form.Item
          style={{
            margin: 0,
          }}
          name={dataIndex}
          rules={[
            {
              required: true,
              message: `${title} is required.`,
            },
          ]}
        >
          <Input ref={inputRef} onPressEnter={handleSave} onBlur={handleSave} />
        </Form.Item>
      );
    case "time":
      return (
        <Form.Item
          style={{
            margin: 0,
          }}
          name={dataIndex}
          rules={[
            {
              required: true,
              message: `${title} is required.`,
            },
          ]}
        >
          <Input
            type="time"
            ref={inputRef}
            onPressEnter={handleSave}
            onBlur={handleSave}
          />
        </Form.Item>
      );
    case "complex":
      return (
        <Form.Item
          style={{
            margin: 0,
          }}
          name={dataIndex}
          rules={[
            {
              required: true,
              message: `${title} is required.`,
            },
          ]}
        >
          <Select ref={inputRef} onChange={handleSave}>
            {optionsData.map((item, index) => (
              <Option value={item} key={index}>
                {item}
              </Option>
            ))}
          </Select>
        </Form.Item>
      );
    default:
      return (
        <Form.Item
          style={{
            margin: 0,
          }}
          name={dataIndex}
          rules={[
            {
              required: true,
              message: `${title} is required.`,
            },
          ]}
        >
          <Input ref={inputRef} onPressEnter={handleSave} onBlur={handleSave} />
        </Form.Item>
      );
  }
};

EditableInput.propTypes = {
  title: PropTypes.string,
  dataIndex: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.string),
    PropTypes.string,
  ]),
  handleSave: PropTypes.func,
  inputRef: PropTypes.object,
  inputType: PropTypes.string,
  optionsData: PropTypes.arrayOf(PropTypes.string),
};

export default EditableInput;
