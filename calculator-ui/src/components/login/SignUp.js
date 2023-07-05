import React, { useState } from "react";
import { TextField, Button } from "@mui/material";
import "./SignInUp.css";
import { useNavigate } from "react-router-dom";
import LockOpenIcon from "@mui/icons-material/LockOpen";
import Avatar from "@mui/material/Avatar";

const SignUp = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const navigate = useNavigate();

  const handleGoSignIn = () => {
    navigate("/sign-in");
  };

  const handleRegister = (event) => {
    event.preventDefault();
    sendData();
    navigate("/sign-in");
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
      <Avatar sx={{ m: 1, color: "#2f3542" }}>
        <LockOpenIcon />
      </Avatar>
      <h2 className="registerTitle">Register User</h2>
      <form onSubmit={handleRegister}>
        <TextField
          className="registerInput"
          label="email@example.com"
          type="email"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
        />
        <TextField
          className="registerInput"
          label="password"
          type="password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
        />
        <TextField
          className="registerInput"
          label="name"
          value={name}
          onChange={(event) => setName(event.target.value)}
        />
        <Button
          sx={{ borderRadius: 20, backgroundColor: "#60a3bc" }}
          type="submit"
          fullWidth
          variant="contained"
          color="primary"
        >
          Sign Up
        </Button>
        <Button
          sx={{ borderRadius: 20, backgroundColor: "#60a3bc" }}
          fullWidth
          variant="contained"
          onClick={handleGoSignIn}
          color="primary"
        >
          I Have An Account
        </Button>
      </form>
    </div>
  );
};

export default SignUp;
