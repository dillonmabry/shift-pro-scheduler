import axios from "axios";
import AuthService from "./AuthService";

const API_URL = process.env.REACT_APP_API_URL;

const getAssignments = () => {
  return axios.get(API_URL + "/assignments", {
    headers: AuthService.authHeader(),
  });
};

const deleteAssignment = (id) => {
  return axios.delete(API_URL + `/assignment/${id}`, {
    headers: AuthService.authHeader(),
  });
};

const saveAssignment = (assignment) => {
  return axios.post(API_URL + "/assignments", assignment, {
    headers: AuthService.authHeader(),
  });
};

const AssignmentService = {
  getAssignments,
  deleteAssignment,
  saveAssignment,
};

export default AssignmentService;
