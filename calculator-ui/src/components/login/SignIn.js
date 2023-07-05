import React, { useState } from "react";
import { TextField, Button } from "@mui/material";
import "./SignInUp.css";
import { useNavigate } from "react-router-dom";
import LockOpenIcon from "@mui/icons-material/LockOpen";
import Avatar from "@mui/material/Avatar";

const SignIn = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleLogin = (event) => {
    event.preventDefault();
    sendData();
    navigate("/calculator");
  };

  const handleGoSignUp = () => {
    navigate("/sign-up");
  };

  async function sendData() {
    // const response = await fetch()
    // const data = await response.json()

    // const jwt = data.jwt
    const jwt = "yhgtfsdtuehhefheu";
    localStorage.setItem("singInJwt", jwt);
  }

  return (
    <div className="container">
      <Avatar sx={{ m: 1 }}>
        <LockOpenIcon />
      </Avatar>
      <h2 className="loginTitle">Iniciar sesión</h2>
      <form onSubmit={handleLogin}>
        <TextField
          className="loginInput"
          label="email@example.com"
          type="email"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
        />
        <TextField
          className="loginInput"
          label="password"
          type="password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
        />
        <Button
          sx={{ borderRadius: 20, backgroundColor: "#60a3bc" }}
          type="submit"
          fullWidth
          variant="contained"
          color="primary"
        >
          Sign In
        </Button>
        <Button
          sx={{ borderRadius: 20, backgroundColor: "#60a3bc" }}
          fullWidth
          variant="contained"
          color="primary"
          onClick={handleGoSignUp}
        >
          Create An Account
        </Button>
      </form>
    </div>
  );
};

export default SignIn;
