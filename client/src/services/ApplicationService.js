import axios from "axios";
import AuthService from "./AuthService";

const API_URL = process.env.REACT_APP_API_URL;

const getApplicationInfo = () => {
  return axios.get(API_URL + "/actuator/info", {
    headers: AuthService.authHeader(),
  });
};

const ApplicationService = {
  getApplicationInfo,
};

export default ApplicationService;
