import React from "react";
import { Route, Redirect } from "react-router-dom";
import AuthService from "../services/AuthService";
import PropTypes from "prop-types";

const PrivateRoute = ({ component: Component, roles, ...rest }) => (
  <Route {...rest} render={props => {
    const isAuthenticated = AuthService.isAuthenticated();
    const currentUser = AuthService.getCurrentUser();
    if (!isAuthenticated || !currentUser) {
      return <Redirect to={{ pathname: '/login' }} />
    }

    if (roles && roles.indexOf(currentUser.authorities) === -1) {
      return <Redirect to={{ pathname: '/' }} />
    }

    return <Component {...props} />
  }} />)

PrivateRoute.propTypes = {
  component: PropTypes.elementType,
  roles: PropTypes.arrayOf(PropTypes.string)
};

export default PrivateRoute;
