import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { useState } from "react";
import LoginForm from "./Components/loginForm";
import RegisterForm from "./Components/registerForm";
import ProtectedPage from "./Components/protectedPage";
import OtpVerificationForm from "./Components/OtpVerificationForm";
import AllUsers from "./Components/allUsers";
import {AuthProvider} from "./contexts/AuthContext";
import { useEffect } from "react";

function App() {
  // useEffect(() => {
  //   fetch('http://localhost:8080/register').then(resp=>resp.text())
  //   .then(resp=>{
  //     console.log('Getting Text from API:' +resp);
  //   })
  // }, []);
  const [token, setToken] = useState(localStorage.getItem("token"));

  useEffect(() => {
  const storedToken = localStorage.getItem("token");
  if (storedToken) {
    setToken(storedToken);
  }
}, []);
  return (
    <AuthProvider>
    <Router>
      <div className="min-h-screen bg-gray-100 p-4">
        <Routes>
          <Route path="/register" element={<RegisterForm />} />
          <Route path="/users" element={<AllUsers />} />
          <Route path="/login" element={<LoginForm onLogin={(tok) => setToken(tok)} />} />
          <Route
            path="/protected"
            element={token ? <ProtectedPage /> : <Navigate to="/login" />}
          />
          <Route path="*" element={<Navigate to="/login" />} />
          <Route path="/verify-otp" element={<OtpVerificationForm />} />
        </Routes>
      </div>
    </Router>
    </AuthProvider>
  );
}

export default App;
