import axios from "axios";

export const axiosInstance = axios.create({
  timeout: 3000,
  withCredentials: true,
  baseURL: "http://localhost", // TODO: proxy
});
