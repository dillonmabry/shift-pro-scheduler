import axios from "axios";
import AuthService from "./AuthService";

const API_URL = process.env.REACT_APP_API_URL;

const getDepartments = () => {
  return axios.get(API_URL + "/departments", {
    headers: AuthService.authHeader(),
  });
};

const deleteDepartment = (id) => {
  return axios.delete(API_URL + `/department/${id}`, {
    headers: AuthService.authHeader(),
  });
};

const saveDepartment = (dept) => {
  return axios.post(
    API_URL + '/departments',
    dept,
    {
      headers: AuthService.authHeader(),
    }
  );
};

const DepartmentService = {
  getDepartments,
  deleteDepartment,
  saveDepartment
};

export default DepartmentService;
