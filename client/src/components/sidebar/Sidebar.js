import React from "react";
import "./Sidebar.css";
import { Layout, Menu, Avatar } from "antd";
import {
  CalendarOutlined,
  LogoutOutlined,
  LoginOutlined,
  UserOutlined,
  UsergroupAddOutlined,
} from "@ant-design/icons";
import { NavLink, withRouter } from "react-router-dom";
import PropTypes from "prop-types";
import AuthService from "../../services/AuthService";
import Generation from "../../utilities/Generation";
import Colors from "../../constants/Colors";

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

class SideMenu extends React.Component {
  state = {
    collapsed: true,
    currentUser: null,
    showAdmin: false,
    showUser: false,
  };

  onCollapse = (collapsed) => {
    this.setState({ collapsed }); // eslint-disable-line
  };

  onLogOut = () => {
    AuthService.logout();
  };

  componentDidMount() {
    const user = AuthService.getCurrentUser();
    if (AuthService.isAuthenticated() && user) {
      this.setState({
        // eslint-disable-line
        currentUser: user,
        showAdmin: AuthService.getRoles(user.authorities).includes(
          "ROLE_ADMIN"
        ),
        showUser: AuthService.getRoles(user.authorities).includes("ROLE_USER"),
      });
    }
  }

  render() {
    const { collapsed, currentUser, showAdmin, showUser } = this.state;
    const { location } = this.props;
    return (
      <Sider collapsible collapsed={collapsed} onCollapse={this.onCollapse}>
        <div className="logo">
          {displayAvatar(currentUser, this.state.collapsed)}
        </div>
        <Menu
          theme="dark"
          defaultSelectedKeys={["/"]}
          selectedKeys={[location.pathname]}
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
          {currentUser && (
            <Menu.Item key="/profile" icon={<UserOutlined />}>
              <NavLink to="/profile">
                <span>Profile</span>
              </NavLink>
            </Menu.Item>
          )}
          {!currentUser && (
            <Menu.Item key="/login" icon={<LoginOutlined />}>
              <NavLink to="/login">
                <span>Login</span>
              </NavLink>
            </Menu.Item>
          )}
          {currentUser && (
            <Menu.Item key="/logout" icon={<LogoutOutlined />}>
              <NavLink to="/login" onClick={this.onLogOut}>
                <span>Logout</span>
              </NavLink>
            </Menu.Item>
          )}
        </Menu>
      </Sider>
    );
  }
}

SideMenu.propTypes = {
  location: PropTypes.shape({
    pathname: PropTypes.string,
  }),
};

export default withRouter(SideMenu);
