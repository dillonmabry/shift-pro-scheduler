import React from "react";
import { BrowserRouter, Switch, Route } from "react-router-dom";
import NotFound from "./NotFound";
import Register from "../containers/register/Register";
import Login from "../containers/login/Login";
import Schedule from "../containers/schedule/Schedule";
import PrivateRoute from "../security/PrivateRoute";
import Employees from "../containers/employees/Employees";
import Profile from "../containers/profile/Profile";

class Main extends React.Component {
  render() {
    return (
      <BrowserRouter>
        <Switch>
          <PrivateRoute exact path="/" component={Schedule} />
          <PrivateRoute exact path="/profile" component={Profile} />
          <PrivateRoute exact path="/employees" component={Employees} />
          <Route exact path="/login" component={Login} />
          <Route exact path="/register" component={Register} />
          <Route component={NotFound} />
        </Switch>
      </BrowserRouter>
    );
  }
}
export default Main;
