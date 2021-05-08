import React, { useContext, useState, useEffect, useRef } from 'react';
import { Table, Input, Button, Form, Empty, Popconfirm } from 'antd';
const EditableContext = React.createContext(null);
import PropTypes from "prop-types";
import NotificationService from "../../services/NotificationService";

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
    const values = await form.validateFields();
    toggleEdit();
    await handleSave({ ...record, ...values });
  };

  let childNode = children;

  if (editable) {
    childNode = editing ? (
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
        <Input ref={inputRef} onPressEnter={save} onBlur={save} />
      </Form.Item>
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

  const dataColumns = [
    ...props.columns,
    {
      title: '',
      dataIndex: 'Remove',
      // eslint-disable-next-line react/display-name
      render: (_, record) =>
        dataSource.length >= 1 ? (
          <Popconfirm title="Confirm delete?" onConfirm={() => handleDelete(record.key)}>
            <a>Delete</a>
          </Popconfirm>
        ) : null,
    },
  ];

  const handleDelete = (key) => {
    props.handleDelete(key)
      .then(
        () => {
          setDataSource(dataSource.filter((item) => item.key !== key));
          NotificationService.notify(
            "success",
            "Successfully removed item"
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
      );
  };
  const addItem = () => {
    const newData = {
      key: null,
    };
    props.columns.forEach(col => {
      // Str
      if (!col.dataType || col.dataType === 'string') {
        newData[col.dataIndex] = `New ${col.title}`;
        // Time
      } else if (col.dataType === 'time') {
        newData[col.dataIndex] = '00:00:00';
        // Number
      } else if (col.dataType === 'number') {
        newData[col.dataIndex] = 0;
        // Object
      } else if (col.dataType === 'object') {
        newData[col.dataIndex] = null;
      }
    });
    props.handleSave(newData)
      .then(
        (response) => {
          setDataSource([...dataSource, { ...response.data, key: response.data.id }]);
          setCount(count + 1);
          NotificationService.notify(
            "success",
            "Successfully added item"
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
      );
  };
  const saveItem = (row) => {
    props.handleSave(row)
      .then(
        () => {
          const newData = [...dataSource];
          const index = newData.findIndex((item) => row.key === item.key);
          const item = newData[index];
          newData.splice(index, 1, { ...item, ...row });
          setDataSource(newData);
          NotificationService.notify(
            "success",
            "Successfully saved item"
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
      }),
    };
  });
  return (
    <div>
      {dataSource.length > 0 ? (
        <div>
          <Button
            onClick={addItem}
            type="primary"
            style={{
              float: 'left',
              marginBottom: 16,
            }}
          >
            Add New
            </Button>
          <Table
            components={components}
            rowClassName={() => 'editable-row'}
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
}

DataTable.propTypes = {
  dataSource: PropTypes.arrayOf(PropTypes.object),
  columns: PropTypes.arrayOf(PropTypes.object),
  handleSave: PropTypes.func,
  handleDelete: PropTypes.func,
};

EditableCell.propTypes = {
  index: PropTypes.number,
  title: PropTypes.string,
  editable: PropTypes.bool,
  children: PropTypes.node,
  dataIndex: PropTypes.string,
  record: PropTypes.object,
  handleSave: PropTypes.func
};

EditableRow.propTypes = {
  index: PropTypes.number
};

export default DataTable;
