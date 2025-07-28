import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";

export default function OtpVerificationForm() {
  const navigate = useNavigate();
  const location = useLocation();
  const email = location.state?.email;

  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const res = await axios.post("http://localhost:8080/verify-otp", null, {
        params: { email, otp },
      });

      if (res.status === 200) {
        navigate("/login");
      } else {
        setError("OTP verification failed");
      }
    } catch (err) {
      setError(err?.response?.data?.message || "Verification failed.");
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="max-w-md mx-auto p-4 bg-white shadow rounded space-y-4"
    >
      <h2 className="text-2xl font-bold">Verify OTP</h2>
      <p className="text-sm text-gray-600">OTP sent to: {email}</p>
      {error && <p className="text-red-500">{error}</p>}
      <input
        type="text"
        placeholder="Enter OTP"
        value={otp}
        onChange={(e) => setOtp(e.target.value)}
        className="w-full p-2 border rounded"
        required
      />
      <button className="w-full bg-blue-500 text-white p-2 rounded">
        Verify
      </button>
    </form>
  );
}
