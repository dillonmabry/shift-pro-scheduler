import React from "react";
import { Route, Redirect } from "react-router-dom";
import AuthService from "../services/AuthService";
import PropTypes from "prop-types";

const PrivateRoute = ({ component: Component, ...rest }) => {
  const isAuthenticated = AuthService.isAuthenticated();

  if (isAuthenticated === null) {
    return <></>;
  }

  return (
    <Route
      {...rest}
      render={(props) =>
        !isAuthenticated ? <Redirect to="/login" /> : <Component {...props} />
      }
    />
  );
};

PrivateRoute.propTypes = {
  component: PropTypes.elementType,
};

export default PrivateRoute;
