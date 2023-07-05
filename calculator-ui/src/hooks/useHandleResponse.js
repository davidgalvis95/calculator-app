import { useDispatch } from "react-redux";

const useHandleResponse = () => {
  const dispatch = useDispatch();

  const handle = async (response) => {
    if (response.error) {
      dispatch({
        type: "ERROR",
        title: response.message,
        message: response.error,
      });
      return {
        payload: null,
      };
    } else {
      return response.payload ? response.payload : response.message;
    }
  };

  return {
    handle: handle,
  };
};

export default useHandleResponse;
