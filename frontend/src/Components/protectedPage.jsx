import { useState } from "react";
import { ping } from "../Services/api";
import { useNavigate } from "react-router-dom"; 

export default function ProtectedPage() {
  const [message, setMessage] = useState("");
  const navigate = useNavigate(); 

  const handlePing = async () => {
    const token = localStorage.getItem("token");
    const res = await ping(token);
    setMessage(res);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    navigate("/login");
  };

  return (
    <div className="max-w-md mx-auto p-4">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Protected Page</h2>
        <button
          onClick={handleLogout}
          className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
        >
          Logout
        </button>
      </div>
      <button
        onClick={handlePing}
        className="bg-purple-500 text-white p-2 rounded"
      >
        Call /ping
      </button>
      {message && <p className="mt-4">{message}</p>}
    </div>
  );
}
