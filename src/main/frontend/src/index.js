import React from 'react';
import ReactDOM from "react-dom/client";
import './index.css';
import reportWebVitals from './reportWebVitals';

import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

import Navigation from "./Navigation";
import MainMenu from "./MainMenu";

import CreateReservation from "./CreateReservation";
import DeleteBowlingLane from "./DeleteBowlingLane";
import CreateBowlingLane from "./CreateBowlingLane";
import DeleteReservation from "./DeleteReservation";
import ViewReservations from "./ViewReservations";
import Registration from "./Registration";
import Login from "./Login"

const root = ReactDOM.createRoot(document.getElementById("root"))
root.render(
  <React.StrictMode>
      <Router>
          <Navigation />
            <Routes>
                <Route path={'/'} element={<MainMenu />} />

                <Route path={'/user/register'} element={<Registration />} />
                <Route path={'/user/login'} element={<Login />} />
                <Route path={"/reservation/make"} element={<CreateReservation />} />
                <Route path={"/reservation/delete"} element={<DeleteReservation />} />
                <Route path={"/reservations/view"} element={<ViewReservations />} />
                <Route path={"/bowling-lane/add"} element={<CreateBowlingLane />} />
                <Route path={"/bowling-lane/remove"} element={<DeleteBowlingLane />} />
            </Routes>
      </Router>
  </React.StrictMode>,
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
