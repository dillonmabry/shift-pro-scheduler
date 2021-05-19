import React from "react";
import { Form, Input, Select } from "antd";
import PropTypes from "prop-types";

const { Option } = Select;

const EditableInput = ({
  dataIndex,
  handleSave,
  inputRef,
  inputType,
  rules,
  optionsData,
}) => {
  if (inputType === "complex") {
    return (
      <Form.Item
        style={{
          margin: 0,
        }}
        name={dataIndex}
        rules={rules}
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
  }
  return (
    <Form.Item
      style={{
        margin: 0,
      }}
      name={dataIndex}
      rules={rules}
    >
      <Input
        type={inputType}
        ref={inputRef}
        onPressEnter={handleSave}
        onBlur={handleSave}
      />
    </Form.Item>
  );
};

EditableInput.propTypes = {
  dataIndex: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.string),
    PropTypes.string,
  ]),
  handleSave: PropTypes.func,
  inputRef: PropTypes.object,
  inputType: PropTypes.string,
  rules: PropTypes.arrayOf(
    PropTypes.oneOfType([PropTypes.object, PropTypes.func])
  ),
  optionsData: PropTypes.arrayOf(PropTypes.string),
};

export default EditableInput;
