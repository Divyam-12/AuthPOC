import { useState } from "react";
import { login } from "../Services/api";
import { useNavigate, Link } from "react-router-dom";
import axios from "axios";

export default function LoginForm({ onLogin }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
  e.preventDefault();
  setError("");
  setLoading(true);

  try {
    const response = await axios.post("http://localhost:8080/login", {
      username,
      password,
    });
    const res = response.data;

    if (res.token) {
      localStorage.setItem("token", res.token);
      localStorage.setItem("role", res.role);

      onLogin?.(res.token);

      if (res.role === "ADMIN") {
        navigate("/users");
      } else {
        navigate("/protected");
      }
    } else {
      setError("Invalid credentials.");
    }
  } catch (err) {
    setError(err?.response?.data?.message || "Error logging in.");
  } finally {
    setLoading(false);
  }
};

//   navigate("/users");
//     } else {
//       setError("Invalid credentials.");
//     }
//   } catch (err) {
//     setError(err.message || "Error logging in.");
//   } finally {
//     setLoading(false);
//   }
// };

  return (
    <form
      onSubmit={handleSubmit}
      className="max-w-md mx-auto p-4 bg-white shadow rounded space-y-4"
    >
      <h2 className="text-2xl font-bold">Login</h2>
      {error && <p className="text-red-500">{error}</p>}

      <input
        type="text"
        placeholder="Username"
        value={username}
        onChange={(e) => {
          setUsername(e.target.value);
          setError("");
        }}
        className="w-full p-2 border rounded"
        required
      />

      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => {
          setPassword(e.target.value);
          setError("");
        }}
        className="w-full p-2 border rounded"
        required
      />

      <button
        type="submit"
        disabled={loading}
        className={`w-full p-2 rounded text-white ${
          loading ? "bg-gray-400" : "bg-blue-500 hover:bg-blue-600"
        }`}
      >
        {loading ? "Logging in..." : "Login"}
      </button>

      <Link
        to="/register"
        className="block w-full text-center border border-gray-300 text-gray-700 p-2 rounded hover:bg-gray-100"
      >
        New user? Register
      </Link>
    </form>
  );
}
