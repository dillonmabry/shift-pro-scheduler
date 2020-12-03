import React from 'react';
import {BrowserRouter, Switch, Route} from 'react-router-dom';
import NotFound from './NotFound';
import Register from '../containers/register/Register';
import Login from '../containers/login/Login';
import Schedule from '../containers/schedule/Schedule';
import PrivateRoute from '../security/PrivateRoute';
import Employees from '../containers/employees/Employees';

class Main extends React.Component {
  render() {
    return (
      <BrowserRouter>
        <Switch>
          <Route exact path="/employees" component={Employees} />
          <Route exact path="/login" component={Login} />
          <Route exact path="/register" component={Register} />
          <PrivateRoute exact path="/schedule" component={Schedule} />
          <Route component={NotFound} />
        </Switch>
      </BrowserRouter>
    );
  }
}
export default Main;
