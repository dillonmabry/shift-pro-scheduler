import axios from "axios";
import jwtDecode from "jwt-decode";

const API_URL = process.env.REACT_APP_API_URL;

const register = (username, password) => {
  return axios.post(API_URL + "/users/register", {
    username,
    password,
  });
};

const login = (username, password) => {
  return axios
    .post(API_URL + "/login", {
      username,
      password,
    })
    .then((response) => {
      localStorage.setItem("user", JSON.stringify(response.data));
      return response.data;
    });
};

const logout = () => {
  localStorage.removeItem("user");
};

const getCurrentUser = () => {
  return JSON.parse(localStorage.getItem("user"));
};

const authHeader = () => {
  const user = getCurrentUser();
  if (user && user.accessToken) {
    return { Authorization: "Bearer " + user.accessToken };
  } else {
    return {};
  }
};

const getRoles = (authorities) => {
  return authorities ? authorities.split(",") : null;
};

const isAuthenticated = () => {
  const user = getCurrentUser();
  if (user) {
    const tokenExpiration = jwtDecode(user.accessToken).exp;
    const dateNow = new Date();
    if (tokenExpiration < dateNow.getTime() / 1000) {
      return false;
    } else {
      return true;
    }
  } else {
    return false;
  }
};

const AuthService = {
  register,
  login,
  logout,
  getCurrentUser,
  authHeader,
  isAuthenticated,
  getRoles,
};
export default AuthService;
