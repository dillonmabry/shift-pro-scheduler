import axios from "axios";
import AuthService from "./AuthService";

const API_URL = process.env.REACT_APP_API_URL;

const getAdministrator = (username) => {
  return axios.get(API_URL + `/administrator/${username}`, {
    headers: AuthService.authHeader(),
  });
};

const saveAdministratorProfile = (username, admin) => {
  return axios.post(API_URL + `/administrators/${username}`, admin, {
    headers: AuthService.authHeader(),
  });
};

const AdministratorService = {
  getAdministrator,
  saveAdministratorProfile,
};

export default AdministratorService;
