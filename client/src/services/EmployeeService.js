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

const deleteEmployee = (id) => {
  return axios.delete(API_URL + `/employee/${id}`, {
    headers: AuthService.authHeader(),
  });
};

const saveEmployee = (emp) => {
  return axios.post(API_URL + "/employees", emp, {
    headers: AuthService.authHeader(),
  });
};

const saveEmployeeProfile = (username, emp) => {
  return axios.post(API_URL + `/employees/${username}`, emp, {
    headers: AuthService.authHeader(),
  });
};

const inviteEmployee = (username) => {
  return axios.post(API_URL + `/users/invite/${username}`, null, {
    headers: AuthService.authHeader(),
  });
};

const EmployeeService = {
  getEmployees,
  getEmployee,
  getEmployeesBySupervisor,
  deleteEmployee,
  saveEmployee,
  saveEmployeeProfile,
  inviteEmployee,
};

export default EmployeeService;
