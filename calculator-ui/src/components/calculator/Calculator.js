import React, { useState, useEffect } from "react";
import {
  Grid,
  TextField,
  Button,
  Select,
  MenuItem,
  Drawer,
} from "@mui/material";
import "./Calculator.css";
import Sidebar from "../sidebar/Sidebar";
import DehazeIcon from "@mui/icons-material/Dehaze";
import axios from "axios";

const Calculator = () => {
  const availableOperators = ["+", "-", "*", "/", "SQRT", "RAST"];
  const [number1, setNumber1] = useState("");
  const [number2, setNumber2] = useState("");
  const [operator, setOperator] = useState(availableOperators[0]);
  const [result, setResult] = useState("");
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleNumber1Change = (event) => {
    setNumber1(event.target.value);
  };

  const handleSetOperator = () => {
    const currentOperatorIndex = availableOperators.findIndex(
      (op) => operator === op
    );
    if (currentOperatorIndex === availableOperators.length - 1) {
      setOperator(availableOperators[0]);
    } else {
      setOperator(availableOperators[currentOperatorIndex + 1]);
    }
  };

  const handleNumber2Change = (event) => {
    setNumber2(event.target.value);
  };

  const handleCalculate = () => {
    if (!number1 || !number2) {
      setErrorMessage("*Please enter a number");
      setResult("");
    } else if (!operator) {
      setErrorMessage("*Please select an operator");
      setResult("");
    } else {
      let result = 0;

      switch (operator) {
        case "+":
          result = Number(number1) + Number(number2);
          break;
        case "-":
          result = Number(number1) - Number(number2);
          break;
        case "*":
          result = Number(number1) * Number(number2);
          break;
        case "/":
          result = Number(number1) / Number(number2);
          break;
        default:
          break;
      }

      setErrorMessage("");
      setResult(result);
    }
  };

  const handleReset = () => {
    setNumber1("");
    setNumber2("");
    setResult("");
    setOperator("");
    setErrorMessage("");
  };

  return (
    <div>
      <Grid
        container
        className="calculator"
        spacing={2}
        justifyContent="center"
      >
        <Grid item xs={12} textAlign="center">
          <h2 className="title">Calculator</h2>
        </Grid>
        <Grid item xs={12} className="inputs-and-dropdown-container">
          {operator === "RAST" ? (
            <TextField
              className="operandInput"
              label="seed"
              type="number"
              value={number1}
              onChange={handleNumber1Change}
              InputProps={{
                inputProps: { min: 0, max: 1 },
              }}
            />
          ) : (
            <TextField
              className="operandInput"
              label="number"
              type="number"
              value={number1}
              onChange={handleNumber1Change}
            />
          )}
          <Button
            sx={{
              borderRadius: 5,
              backgroundColor: "#e77f67",
              width: "100px",
            }}
            variant="contained"
            fullWidth
            color="primary"
            onClick={handleSetOperator}
          >
            {operator}
          </Button>
          {operator !== "SQRT" ? (
            operator === "RAST" ? (
              <TextField
                className="operandInput"
                label="length"
                textAlign="center"
                type="number"
                value={number2}
                onChange={handleNumber2Change}
              />
            ) : (
              <TextField
                className="operandInput"
                label="number"
                textAlign="center"
                type="number"
                value={number2}
                onChange={handleNumber2Change}
              />
            )
          ) : null}
        </Grid>
        {errorMessage && <span className="error-message">{errorMessage}</span>}
        <Grid item xs={12} className="button-row">
          <Button
            sx={{ borderRadius: 20, width: "200px" }}
            variant="contained"
            fullWidth
            color="primary"
            onClick={handleCalculate}
          >
            Calculate
          </Button>
          <Button
            sx={{ borderRadius: 20, width: "200px" }}
            variant="contained"
            fullWidth
            color="primary"
            onClick={handleReset}
          >
            Reset
          </Button>
        </Grid>
        <Grid
          container
          justifyContent="center"
        >
          {(result || result === 0) && (
            <div className="result-container">
              <h3 className="resultTextTitle">Result</h3>
              <span className="resultText">{result}</span>
            </div>
          )}
        </Grid>

        {/* <Drawer
          anchor="left"
          open="false"
          hideBackdrop="true"
          // variant="permanent"
        > */}
        <Sidebar open={false}/>
        {/* </Drawer> */}
      </Grid>
    </div>
  );
};

export default Calculator;
