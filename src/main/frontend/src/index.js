import React from 'react';
import ReactDOM from "react-dom/client";
import './index.css';
import reportWebVitals from './reportWebVitals';

import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

import Navigation from "./Navigation";
import MainMenu from "./MainMenu";

import MakeReservation from "./MakeReservation";
import RemoveBowlingLane from "./RemoveBowlingLane";
import AddBowlingLane from "./AddBowlingLane";
import DeleteReservation from "./DeleteReservation";
import ViewReservations from "./ViewReservations";
import Registration from "./Registration";

const root = ReactDOM.createRoot(document.getElementById("root"))
root.render(
  <React.StrictMode>
      <Router>
          <Navigation />
            <Routes>
                <Route path={'/'} element={<MainMenu />} />

                <Route path={'/register'} element={<Registration />} />
                <Route path={"/reservation/make"} element={<MakeReservation />} />
                <Route path={"/reservation/delete"} element={<DeleteReservation />} />
                <Route path={"/reservations/view"} element={<ViewReservations />} />
                <Route path={"/bowling-lane/add"} element={<AddBowlingLane />} />
                <Route path={"/bowling-lane/remove"} element={<RemoveBowlingLane />} />
            </Routes>
      </Router>
  </React.StrictMode>,
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
