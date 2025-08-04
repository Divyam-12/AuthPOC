import { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { ping } from "../Services/api";
import { AuthContext } from "../contexts/AuthContext";

export default function ProtectedPage() {
  const [message, setMessage] = useState("");
  const navigate = useNavigate();
  const { logout, role } = useContext(AuthContext);

  const handlePing = async () => {
    try {
      const res = await ping(localStorage.getItem("token"));
      setMessage(res);
    } catch (err) {
      setMessage("Failed to call API");
    }
  };

  const handleLogout = () => {
    logout();
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

      <p className="text-gray-700 mb-2">Role: {role}</p>

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
