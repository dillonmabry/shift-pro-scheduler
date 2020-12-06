import axios from "axios";
import AuthService from "./AuthService";

const API_URL = process.env.REACT_APP_API_URL;

const getEmployees = () => {
  return axios.get(API_URL + "/employees", {
    headers: AuthService.authHeader(),
  });
};

const getEmployeesBySupervisor = (supervisor) => {
  return axios.get(API_URL + `/employees/${supervisor}`, {
    headers: AuthService.authHeader(),
  });
};

const getEmployee = (username) => {
  return axios.get(API_URL + `/employee/${username}`, {
    headers: AuthService.authHeader(),
  });
};

const EmployeeService = {
  getEmployees,
  getEmployee,
  getEmployeesBySupervisor,
};

export default EmployeeService;
