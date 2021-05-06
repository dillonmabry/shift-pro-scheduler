import axios from "axios";
import AuthService from "./AuthService";

const API_URL = process.env.REACT_APP_API_URL;

const getShifts = () => {
  return axios.get(API_URL + "/shifts", {
    headers: AuthService.authHeader(),
  });
};

const ShiftService = {
  getShifts,
};

export default ShiftService;
