import useHandleResponse from "./useHandleResponse";
import { calculatorApiService } from "../axios/CalculatorApi";

const useAdminApi = () => {
  const { handle } = useHandleResponse();

  const getUsers = async (pageNumber, pageSize, userStatus) => {
    try {
      const response = await calculatorApiService().get(
        `/user/all?pageNumber=${pageNumber}&pageSize=${pageSize}&userStatus=${userStatus}`
      ).data;
      return handle(response);
    } catch (error) {
      return handle({
        message: "Unknown error",
        error: "Something went wrong",
      });
    }
  };

  const deactivateUser = async (userId) => {
    try {
      const response = await calculatorApiService().post(
        `user/deactivate/${userId}`
      ).data;
      return handle(response);
    } catch (error) {
      return handle({
        message: "Unknown error",
        error: "Something went wrong",
      });
    }
  };

  const activateUser = async (userId) => {
    try {
      const response = await calculatorApiService().post(
        `user/activate/${userId}`
      ).data;
      return handle(response);
    } catch (error) {
      return handle({
        message: "Unknown error",
        error: "Something went wrong",
      });
    }
  };

  const setAsAdmin = async (userId) => {
    try {
      return await calculatorApiService().post(`user/upgrade/${userId}`).data;
    } catch (error) {
      return handle({
        message: "Unknown error",
        error: "Something went wrong",
      });
    }
  };

  return {
    getUsers: getUsers,
    deactivateUser: deactivateUser,
    activateUser: activateUser,
    setAsAdmin: setAsAdmin,
  };
};

export default useAdminApi;
