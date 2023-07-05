import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';

import SignIn from './components/login/SignIn';
import Calculator from './components/calculator/Calculator';
import {UsersTable} from './components/table/users/UsersTable'
import {HistoryTable} from './components/table/history/HistoryTable'
import SignUp from './components/login/SignUp';

const RoutesComponent = () => {
  return (
    <Router>
      <Routes>
        {/* <Route path="/sign-in" element={<SignIn />} /> */}
        {/* <Route path="/sign-up" element={<SignUp />} /> */}
        {/* <Route path="/" element={<Calculator />} /> */}
        {/* {/* <Route path="/history" element={<History />} /> */}
        <Route path="/" element={<HistoryTable />} />
        {/* <Route path="/" element={<UsersTable />} /> */}
        {/* <Route path="/" element={<Users />} /> */}
        {/* <Route path="/" element={<SignIn />} /> */}

      </Routes>
    </Router>
  );
};

export default RoutesComponent;
