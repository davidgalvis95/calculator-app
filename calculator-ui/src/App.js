import React from "react";
import RoutesComponent from "./Routes";
import "./App.css";
import Modal from "./components/modal/Modal";

function App() {

  const setIsOpen = () => {
    console.log("is Open");
  }
  return (
    <div className="container">
      {/* {true && <Modal setIsOpen={setIsOpen} message={"An error has occurred"} title={"404 Error"} />} */}
      <RoutesComponent />
    </div>
  );
}

export default App;
