import axios from "axios";
import AuthService from "./AuthService";

const API_URL = process.env.REACT_APP_API_URL;

const getShiftDays = () => {
  return axios.get(API_URL + "/shiftdays", {
    headers: AuthService.authHeader(),
  });
};

const ShiftDayService = {
  getShiftDays,
};

export default ShiftDayService;
