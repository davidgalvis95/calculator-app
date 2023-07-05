const defaultAuthState = {
    title: undefined,
    message: undefined,
  };
  
  const ERROR = "ERROR";
  const CLEAR_ERROR = "CLEAR_ERROR";
  const errorReducer = (state = defaultAuthState, action) => {
    switch (action.type) {
      case ERROR:
        return {
          ...state,
          title: action.title,
          userMeta: action.message,
        };
      case CLEAR_ERROR:
        return {
          ...state,
          title: undefined,
          userMeta: undefined,
        };
      default:
        return state;
    }
  };

  export default errorReducer;