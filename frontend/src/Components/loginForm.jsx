import { useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";
import FaceWebcam from "./FaceWebcam";

export default function LoginForm({ onLogin }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [faceVector, setFaceVector] = useState(null);

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    
    try {
      // ✅ Step 1: Authenticate username/password
      const response = await axios.post(
        "http://localhost:8080/login",
        {
          username,
          password,
        },
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      console.log("Login response:", response.data);
      const res = response.data;
      // console.log(res);

      // ✅ Step 2: Require face verification
      if (!faceVector) {
        setError("Please capture your face for verification.");
        setLoading(false);
        return;
      }
      
      console.log("step2");
      const faceRes = await axios.post(
        `http://localhost:8080/face/verify?username=${username}`, JSON.stringify(faceVector), {
        headers: { "Content-Type": "application/json", 
          Authorization: `Bearer ${res.token}`,
         }
      });
      console.log("step3");
      
      console.log("Face verification response:", faceRes.data); 

      if (faceRes.data === "Face verified.") {
        localStorage.setItem("token", res.token);
        localStorage.setItem("role", res.role);
        onLogin?.(res.token);
        navigate(res.role === "ADMIN" ? "/users" : "/protected");
      } else {
        setError("Face not recognized. Try again.");
      }
    } catch (err) {
      console.log("Caught error:", err);
      setError(err?.response?.data?.message || "Error logging in.");
        // setError(err.message);
    } finally {
      setLoading(false);
    }
  };

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

      <FaceWebcam onDescriptor={setFaceVector} />

      <button
        type="submit"
        disabled={loading || !faceVector}
        className={`w-full p-2 rounded text-white ${
          loading || !faceVector
            ? "bg-gray-400"
            : "bg-blue-500 hover:bg-blue-600"
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
