import React, { useContext, useState, useEffect, useRef } from "react";
import { Table, Button, Form, Empty, Popconfirm } from "antd";
const EditableContext = React.createContext(null);
import PropTypes from "prop-types";
import NotificationService from "../../services/NotificationService";
import EditableInput from "../editable-input/EditableInput";
import { getNestedObject } from "../../utilities/Manipulation";
import LABELS from "../../constants/Labels";

const EditableRow = ({ index, ...props }) => {
  const [form] = Form.useForm();
  return (
    <Form form={form} component={false}>
      <EditableContext.Provider value={form}>
        <tr {...props} />
      </EditableContext.Provider>
    </Form>
  );
};

const EditableCell = ({
  title,
  editable,
  children,
  dataIndex,
  record,
  handleSave,
  inputType,
  rules,
  optionsData,
  complexOptionsData,
  ...restProps
}) => {
  const [editing, setEditing] = useState(false);
  const inputRef = useRef(null);
  const form = useContext(EditableContext);
  useEffect(() => {
    if (editing) {
      inputRef.current.focus();
    }
  }, [editing]);

  const toggleEdit = () => {
    setEditing(!editing);
    form.setFieldsValue({
      [dataIndex]: record[dataIndex],
    });
  };

  const save = async () => {
    const values = await form.validateFields().catch((errorInfo) => {
      errorInfo.errorFields.forEach((error) => {
        error.errors.forEach((err) => {
          NotificationService.notify("error", err);
        });
      });
      setEditing(false);
    });
    if (!values || !record) return;
    toggleEdit();
    // Handle nested accessors
    if (Array.isArray(dataIndex)) {
      const selectedItem = getNestedObject(values, dataIndex);
      const prevItem = getNestedObject(record, dataIndex);
      if (complexOptionsData && selectedItem && selectedItem !== prevItem) {
        const newItem = complexOptionsData.find(
          (option) => option[dataIndex[dataIndex.length - 1]] === selectedItem
        );
        values[dataIndex[0]] = newItem;
        await handleSave({ ...record, ...values });
      }
      // Handle single accessors
    } else {
      if (
        values[dataIndex] &&
        record[dataIndex] &&
        record[dataIndex] !== values[dataIndex]
      )
        await handleSave({ ...record, ...values });
    }
  };

  let childNode = children;
  if (editable) {
    childNode = editing ? (
      <EditableInput
        dataIndex={dataIndex}
        handleSave={save}
        inputRef={inputRef}
        inputType={inputType}
        rules={rules}
        optionsData={optionsData}
      />
    ) : (
      <div
        className="editable-cell-value-wrap"
        style={{
          paddingRight: 24,
        }}
        onClick={toggleEdit}
      >
        {children}
      </div>
    );
  }

  return <td {...restProps}>{childNode}</td>;
};

const DataTable = (props) => {
  const [dataSource, setDataSource] = useState(props.dataSource);
  const [count, setCount] = useState(props.dataSource.length);

  let dataColumns = [
    ...props.columns,
    {
      title: "",
      dataIndex: "Remove",
      // eslint-disable-next-line react/display-name
      render: (_, record) =>
        dataSource.length >= 1 ? (
          <Popconfirm
            title="Confirm delete?"
            onConfirm={() => handleDelete(record.key)}
          >
            <a>Delete</a>
          </Popconfirm>
        ) : null,
    },
  ];

  if (props.label === LABELS.Employees) {
    dataColumns = [
      ...dataColumns,
      {
        title: "",
        dataIndex: "Invite",
        // eslint-disable-next-line react/display-name
        render: (_, record) =>
          dataSource.length >= 1 ? (
            <a href="#" onClick={() => handleInvite(record.userName)}>
              Invite
            </a>
          ) : null,
      },
    ];
  }

  const handleDelete = (key) => {
    props.handleDelete(key).then(
      () => {
        setDataSource(dataSource.filter((item) => item.key !== key));
        NotificationService.notify("success", "Successfully removed item");
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
  };

  const handleInvite = (username) => {
    props.handleInvite(username).then(
      () => {
        NotificationService.notify("success", "Successfully invited employee");
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
  };

  const addItem = () => {
    const newData = {
      key: null,
    };
    props.columns.forEach((col) => {
      // Str
      if (!col.dataType || col.dataType === "string") {
        newData[col.dataIndex] = `New ${col.title}`;
        // Time
      } else if (col.dataType === "time") {
        newData[col.dataIndex] = "00:00:00";
        // Number
      } else if (col.dataType === "number") {
        newData[col.dataIndex] = 0;
        // Object
      } else if (col.dataType === "complex") {
        newData[col.dataIndex] = null;
        // Phone
      } else if (col.dataType === "phone") {
        newData[col.dataIndex] = "999-999-9999";
        // Email
      } else if (col.dataType === "email") {
        newData[col.dataIndex] = "email@example.com";
      }
    });
    props.handleSave(newData).then(
      (response) => {
        setDataSource([
          ...dataSource,
          { ...response.data, key: response.data.id },
        ]);
        setCount(count + 1);
        NotificationService.notify("success", "Successfully added item");
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
  };
  const saveItem = (row) => {
    props.handleSave(row).then(
      () => {
        const newData = [...dataSource];
        const index = newData.findIndex((item) => row.key === item.key);
        const item = newData[index];
        newData.splice(index, 1, { ...item, ...row });
        setDataSource(newData);
        NotificationService.notify("success", "Successfully saved item");
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
  };

  const components = {
    body: {
      row: EditableRow,
      cell: EditableCell,
    },
  };
  const columns = dataColumns.map((col) => {
    if (!col.editable) {
      return col;
    }

    return {
      ...col,
      onCell: (record) => ({
        record,
        editable: col.editable,
        dataIndex: col.dataIndex,
        title: col.title,
        handleSave: saveItem,
        inputType: col.dataType,
        rules: col.rules,
        optionsData: col.optionsData,
        complexOptionsData: col.complexOptionsData,
      }),
    };
  });
  return (
    <div>
      <Button
        onClick={addItem}
        type="primary"
        style={{
          float: "left",
          marginBottom: 16,
        }}
      >
        Add New
      </Button>
      {dataSource.length > 0 ? (
        <div>
          <Table
            components={components}
            rowClassName={() => "editable-row"}
            bordered
            dataSource={dataSource}
            columns={columns}
          />
        </div>
      ) : (
        <Empty />
      )}
    </div>
  );
};

DataTable.propTypes = {
  dataSource: PropTypes.arrayOf(PropTypes.object),
  columns: PropTypes.arrayOf(PropTypes.object),
  handleSave: PropTypes.func,
  handleDelete: PropTypes.func,
  handleInvite: PropTypes.func,
  label: PropTypes.string,
};

EditableCell.propTypes = {
  index: PropTypes.number,
  title: PropTypes.string,
  editable: PropTypes.bool,
  children: PropTypes.node,
  dataIndex: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.string),
    PropTypes.string,
  ]),
  record: PropTypes.object,
  handleSave: PropTypes.func,
  inputType: PropTypes.string,
  rules: PropTypes.arrayOf(
    PropTypes.oneOfType([PropTypes.object, PropTypes.func])
  ),
  optionsData: PropTypes.arrayOf(PropTypes.string),
  complexOptionsData: PropTypes.arrayOf(PropTypes.object),
};

EditableRow.propTypes = {
  index: PropTypes.number,
};

export default DataTable;
