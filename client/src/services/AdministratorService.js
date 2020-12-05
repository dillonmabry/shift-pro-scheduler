import axios from "axios";
import AuthService from "./AuthService";

const API_URL = process.env.REACT_APP_API_URL;

const getAdministrator = (username) => {
  return axios.get(API_URL + `/administrator/${username}`, {
    headers: AuthService.authHeader(),
  });
};

const AdministratorService = {
  getAdministrator,
};

export default AdministratorService;
