import { useDispatch } from "react-redux";
import { calculatorApiService } from "../axios/CalculatorApi";
import useHandleResponse from "./useHandleResponse";

const useAdminApi = () => {
  const dispatch = useDispatch();
  const { handle } = useHandleResponse();

  const signUp = async (email, password) => {
    try {
      const response = await calculatorApiService().post(`auth/signup`, {
        email: email,
        password: password,
        status: "ACTIVE",
        roles: ["USER"],
      }).data;
      return handle(response);
    } catch (error) {
      return handle({
        message: "Unknown error",
        error: "Something went wrong",
      });
    }
  };

  const signIn = async (email, password) => {
    try {
      const response = await calculatorApiService().post(`/auth/signin`, {
        email: email,
        password: password,
      }).data;
      if (response.error) {
        return handle(response);
      }
      dispatch({ type: "USER_SIGNED_IN", userMeta: response.payload });
      localStorage.setItem("userMeta", response.payload);
    } catch (error) {
      return handle({
        message: "Unknown error",
        error: "Something went wrong",
      });
    }
  };

  const logOut = async () => {
    try {
      const response = await calculatorApiService().post(`auth/logout`).data;
      if (response.error) {
        return handle(response);
      }
      localStorage.removeItem("userMeta");
      dispatch({ type: "USER_SIGNED_OUT" });
      return {
        message: response.message,
      };
    } catch (error) {
      return handle({
        message: "Unknown error",
        error: "Something went wrong",
      });
    }
  };

  return {
    signUp: signUp,
    signIn: signIn,
    logOut: logOut,
  };
};

export default useAdminApi;
