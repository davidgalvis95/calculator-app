import { calculatorApiService } from "../axios/CalculatorApi";
import useHandleResponse from "./useHandleResponse";

const useOperationsApi = () => {
  const { handle } = useHandleResponse();

  const getRecords = async (
    nextPageToken,
    pageNumber,
    pageSize,
    additionalParams
  ) => {
    try {
      const response = await calculatorApiService().get(
        nextPageToken
          ? nextPageToken
          : `/records?pageNumber=${pageNumber}&pageSize=${pageSize}${additionalParams}`,
          {}
      ).data;
      return handle(response);
    } catch (error) {
      return handle({
        message: "Unknown error",
        error: "Something went wrong",
      });
    }
  };

  const calculateOperation = async (operationBody) => {
    try {
      const response = await calculatorApiService().post(
        "/calculate",
        operationBody
      ).data;
      return handle(response);
    } catch (error) {
      return handle({
        message: "Unknown error",
        error: "Something went wrong",
      });
    }
  };

  const addUserBalance = async () => {
    try {
      const response = await calculatorApiService().post(
        "/user/balance-funding"
      ).data;
      return handle(response);
    } catch (error) {
      return handle({
        message: "Unknown error",
        error: "Something went wrong",
      });
    }
  };

  return {
    getRecords: getRecords,
    calculateOperation: calculateOperation,
    addUserBalance: addUserBalance,
  };
};

export default useOperationsApi;
