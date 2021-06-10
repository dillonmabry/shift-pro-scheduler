import axios from "axios";
import AuthService from "./AuthService";

const API_URL = process.env.REACT_APP_API_URL;

const getAssignmentRequests = (username) => {
  return axios.get(API_URL + `/assignmentrequests/${username}`, {
    headers: AuthService.authHeader(),
  });
};

const deleteAssignmentRequest = (id) => {
  return axios.delete(API_URL + `/assignmentrequest/${id}`, {
    headers: AuthService.authHeader(),
  });
};

const saveAssignmentRequests = (request) => {
  return axios.post(API_URL + "/assignmentrequests", request, {
    headers: AuthService.authHeader(),
  });
};

const AssignmentRequestService = {
  getAssignmentRequests,
  deleteAssignmentRequest,
  saveAssignmentRequests,
};

export default AssignmentRequestService;
