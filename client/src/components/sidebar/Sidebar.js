import React from 'react';
import './Sidebar.css';
import {Layout, Menu} from 'antd';
import {
  CalendarOutlined,
  LogoutOutlined,
  LoginOutlined,
  UserOutlined,
  UsergroupAddOutlined,
} from '@ant-design/icons';
import {NavLink, withRouter} from 'react-router-dom';
import PropTypes from 'prop-types';
import AuthService from '../../services/AuthService';

const {Sider} = Layout;

class SideMenu extends React.Component {
  state = {
    collapsed: false,
  };

  onCollapse = (collapsed) => {
    this.setState({collapsed}); // eslint-disable-line
  };

  onLogOut = () => {
    AuthService.logout();
  };

  componentDidMount() {
    const user = AuthService.getCurrentUser();
    if (user) {
      this.setState({ // eslint-disable-line
        currentUser: user,
        showAdmin: AuthService.getRoles(user.authorities).includes(
            'ROLE_ADMIN',
        ),
        showUser: AuthService.getRoles(user.authorities).includes('ROLE_USER'),
      });
    }
  }

  render() {
    const {collapsed, currentUser, showAdmin, showUser} = this.state;
    const {location} = this.props;
    return (
      <Sider collapsible collapsed={collapsed} onCollapse={this.onCollapse}>
        <div className="logo" />
        <Menu
          theme="dark"
          defaultSelectedKeys={['/']}
          selectedKeys={[location.pathname]}
          mode="inline"
        >
          {(showAdmin || showUser) && (
            <Menu.Item key="/schedule" icon={<CalendarOutlined />}>
              <NavLink to="/schedule">
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
          {(showAdmin || showUser) && (
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
