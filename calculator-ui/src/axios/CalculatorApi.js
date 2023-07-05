import axios from "axios";

export const baseApiUrl = "/api/v1"
export const calculatorApiService = () => {
  const host = process.env.CALCULATOR_API_HOST;
  return axios.create({
    baseURL: `${host}"/api/v1"`,
    responseType: "json",
  });
};
