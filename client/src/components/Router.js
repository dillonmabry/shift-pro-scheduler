import React from "react";
import { BrowserRouter, Switch, Route } from "react-router-dom";
import NotFound from "./NotFound";
import Register from "../containers/register/Register";
import Login from "../containers/login/Login";
import Schedule from "../containers/schedule/Schedule";
import PrivateRoute from "../security/PrivateRoute";
import Employees from "../containers/employees/Employees";
import Profile from "../containers/profile/Profile";
import Shifts from "../containers/shifts/Shifts";
import Departments from "../containers/departments/Departments";
import ROLES from "../constants/Roles";
import AssignmentRequests from "../containers/assignmentrequests/AssignmentRequests";
import About from "../containers/about/About";

class Main extends React.Component {
  render() {
    return (
      <BrowserRouter>
        <Switch>
          <PrivateRoute
            exact
            path="/"
            component={Schedule}
            roles={[ROLES.User, ROLES.Admin]}
          />
          <PrivateRoute
            exact
            path="/profile"
            component={Profile}
            roles={[ROLES.User, ROLES.Admin]}
          />
          <PrivateRoute
            exact
            path="/employees"
            component={Employees}
            roles={[ROLES.Admin]}
          />
          <PrivateRoute
            exact
            path="/shifts"
            component={Shifts}
            roles={[ROLES.Admin]}
          />
          <PrivateRoute
            exact
            path="/departments"
            component={Departments}
            roles={[ROLES.Admin]}
          />
          <PrivateRoute
            exact
            path="/shiftrequests"
            component={AssignmentRequests}
            roles={[ROLES.User]}
          />
          <PrivateRoute
            exact
            path="/about"
            component={About}
            roles={[ROLES.User, ROLES.Admin]}
          />
          <Route exact path="/login" component={Login} />
          <Route exact path="/register" component={Register} />
          <Route component={NotFound} />
        </Switch>
      </BrowserRouter>
    );
  }
}
export default Main;
