import axios from "axios";

export const calculatorApiService = () => {
  const host = process.env.CALCULATOR_API_BASE_URL;
  return axios.create({
    baseURL: `${host}/api/v1`,
    responseType: "json",
  });
};
