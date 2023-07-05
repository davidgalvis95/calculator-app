import axios from 'axios';

// export const baseApiUrl = "/api/v1"
export const calculatorApiService =   axios.create({
    baseURL: `${process.env.CALCULATOR_API_BASE_URL}/api/v1`,
    responseType: "json",
  });
