import axios from "axios";
import AuthService from "./AuthService";

const API_URL = process.env.REACT_APP_API_URL;

const getShifts = () => {
  return axios.get(API_URL + "/shifts", {
    headers: AuthService.authHeader(),
  });
};

const deleteShift = (id) => {
  return axios.delete(API_URL + `/shift/${id}`, {
    headers: AuthService.authHeader(),
  });
};

const saveShift = (shift) => {
  return axios.post(
    API_URL + '/shifts',
    shift,
    {
      headers: AuthService.authHeader(),
    }
  );
};

const ShiftService = {
  getShifts,
  deleteShift,
  saveShift
};

export default ShiftService;
