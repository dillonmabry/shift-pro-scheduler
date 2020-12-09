import axios from "axios";
import AuthService from "./AuthService";

const API_URL = process.env.REACT_APP_API_URL;

const getSchedules = () => {
  return axios.get(API_URL + "/schedules", {
    headers: AuthService.authHeader(),
  });
};

const getSchedule = (id) => {
  return axios.get(API_URL + `/schedule/${id}`, {
    headers: AuthService.authHeader(),
  });
};

const deleteSchedule = (id) => {
  return axios.delete(API_URL + `/schedule/${id}`, {
    headers: AuthService.authHeader(),
  });
};

const activateSchedule = (id) => {
  return axios.post(API_URL + `/schedule/${id}`, null, {
    headers: AuthService.authHeader(),
  });
};

const postSchedules = (startDate, endDate, numSchedules) => {
  return axios.post(API_URL + `/administrators/schedules`, null, {
    headers: AuthService.authHeader(),
    params: {
      startDate,
      endDate,
      numSchedules,
    },
  });
};

const ScheduleService = {
  getSchedules,
  getSchedule,
  postSchedules,
  deleteSchedule,
  activateSchedule,
};
export default ScheduleService;
