const defaultAuthState = {
  userMeta: undefined,
};

const USER_SIGNED_IN = "USER_SIGNED_IN";
const USER_SIGNED_OUT = "USER_SIGNED_OUT";
const authReducer = (state = defaultAuthState, action) => {
  switch (action.type) {
    case USER_SIGNED_IN:
      return {
        ...state,
        userMeta: action.userMeta,
      };
    case USER_SIGNED_OUT:
      return {
        ...state,
        userMeta: undefined,
      };
    default:
      return state;
  }
};

export default authReducer;
