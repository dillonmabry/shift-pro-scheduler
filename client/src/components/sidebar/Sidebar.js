import React, { useState, useEffect, useRef } from "react";
import "./Sidebar.css";
import { Layout, Menu, Avatar } from "antd";
import {
  CalendarOutlined,
  LogoutOutlined,
  LoginOutlined,
  UserOutlined,
  UsergroupAddOutlined,
  ScheduleOutlined,
  ApartmentOutlined,
} from "@ant-design/icons";
import { NavLink, withRouter } from "react-router-dom";
import PropTypes from "prop-types";
import AuthService from "../../services/AuthService";
import Generation from "../../utilities/Generation";
import Colors from "../../constants/Colors";
import ROLES from "../../constants/Roles";

const { Sider } = Layout;

const displayAvatar = (user, collapsed) => {
  if (collapsed) {
    return user && user.username ? (
      <Avatar
        style={{
          color: "#fff",
          backgroundColor:
            Colors.avatarColors[
              Generation.numberFromText(user.username[0]) %
                Colors.avatarColors.length
            ],
        }}
        size={32}
      >
        {user.username[0].toUpperCase()}
      </Avatar>
    ) : (
      <Avatar icon={<UserOutlined />} size={32} />
    );
  }
  return user && user.username ? (
    <Avatar
      style={{
        color: "#fff",
        backgroundColor:
          Colors.avatarColors[
            Generation.numberFromText(user.username[0]) %
              Colors.avatarColors.length
          ],
      }}
      size={50}
    >
      {user.username.toUpperCase()}
    </Avatar>
  ) : (
    <Avatar icon={<UserOutlined />} size={50} />
  );
};

const SideMenu = (props) => {
  const [collapsed, setCollapsed] = useState(true);
  const [showAdmin, setShowAdmin] = useState(false);
  const [showUser, setShowUser] = useState(false);
  const userInfoRef = useRef(null);

  const onCollapse = (collapsed) => {
    setCollapsed(collapsed);
  };

  const onLogOut = () => {
    AuthService.logout();
  };

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    userInfoRef.current = user;
    if (AuthService.isAuthenticated() && userInfoRef.current) {
      setShowUser(
        AuthService.getRoles(userInfoRef.current.authorities).includes(
          ROLES.User
        )
      );
      setShowAdmin(
        AuthService.getRoles(userInfoRef.current.authorities).includes(
          ROLES.Admin
        )
      );
    }
  }, []);

  return (
    <Sider collapsible collapsed={collapsed} onCollapse={onCollapse}>
      <div className="logo">
        {displayAvatar(userInfoRef.current, collapsed)}
      </div>
      <Menu
        theme="dark"
        defaultSelectedKeys={["/"]}
        selectedKeys={[props.pathname]}
        mode="inline"
      >
        {(showAdmin || showUser) && (
          <Menu.Item key="/" icon={<CalendarOutlined />}>
            <NavLink to="/">
              <span>Schedule</span>
            </NavLink>
          </Menu.Item>
        )}
        {showAdmin && (
          <Menu.Item key="/employees" icon={<UsergroupAddOutlined />}>
            <NavLink to="/employees">
              <span>Employees</span>
            </NavLink>
          </Menu.Item>
        )}
        {showAdmin && (
          <Menu.Item key="/shifts" icon={<ScheduleOutlined />}>
            <NavLink to="/shifts">
              <span>Shifts</span>
            </NavLink>
          </Menu.Item>
        )}
        {showAdmin && (
          <Menu.Item key="/departments" icon={<ApartmentOutlined />}>
            <NavLink to="/departments">
              <span>Departments</span>
            </NavLink>
          </Menu.Item>
        )}
        {userInfoRef.current && (
          <Menu.Item key="/profile" icon={<UserOutlined />}>
            <NavLink to="/profile">
              <span>Profile</span>
            </NavLink>
          </Menu.Item>
        )}
        {!userInfoRef.current && (
          <Menu.Item key="/login" icon={<LoginOutlined />}>
            <NavLink to="/login">
              <span>Login</span>
            </NavLink>
          </Menu.Item>
        )}
        {userInfoRef.current && (
          <Menu.Item key="/logout" icon={<LogoutOutlined />}>
            <NavLink to="/login" onClick={onLogOut}>
              <span>Logout</span>
            </NavLink>
          </Menu.Item>
        )}
      </Menu>
    </Sider>
  );
};

SideMenu.propTypes = {
  pathname: PropTypes.string,
};

export default withRouter(SideMenu);
