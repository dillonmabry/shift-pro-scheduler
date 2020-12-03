import axios from 'axios';
import AuthService from './AuthService';

const API_URL = process.env.REACT_APP_API_URL;

const getSchedules = () => {
  return axios.get(API_URL + '/schedules', {
    headers: AuthService.authHeader(),
  });
};

const getSchedule = (id) => {
  return axios.get(API_URL + `/schedule/${id}`, {
    headers: AuthService.authHeader(),
  });
};

const ScheduleService = {
  getSchedules,
  getSchedule,
};
export default ScheduleService;
