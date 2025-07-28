import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

export default function RegisterForm() {
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    username: "",
    password: "",
    role: "",
  });

  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError("");
  };

  const isValidEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!isValidEmail(form.username)) {
      setError("Please enter a valid email.");
      return;
    }

    if (form.password.length < 6) {
      setError("Password must be at least 6 characters.");
      return;
    }

    try {
      const res = await axios.post("http://localhost:8080/register", form);

      if (res.data?.token) {
        setSuccess("Registered successfully! Check your email for OTP.");
        setError("");

        navigate("/verify-otp", {
          state: { email: form.username },
        });

        setForm({
          firstName: "",
          lastName: "",
          username: "",
          password: "",
          role: "",
        });
      } else {
        setError("Registration failed.");
      }
    } catch (err) {
      const msg =
        err?.response?.data?.message?.toLowerCase() || "Error registering.";

      if (msg.includes("email already exists") || msg.includes("user already exists")) {
        setError("Email already exists. Try logging in.");
      } else {
        setError("Error registering: " + msg);
      }
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="max-w-md mx-auto p-4 bg-white shadow rounded space-y-4"
    >
      <h2 className="text-2xl font-bold">Register</h2>
      {success && <p className="text-green-500">{success}</p>}
      {error && <p className="text-red-500">{error}</p>}

      <input
        name="firstName"
        placeholder="First Name"
        value={form.firstName}
        onChange={handleChange}
        className="w-full p-2 border rounded"
        required
      />

      <input
        name="lastName"
        placeholder="Last Name"
        value={form.lastName}
        onChange={handleChange}
        className="w-full p-2 border rounded"
        required
      />

      <input
        name="username"
        placeholder="Email"
        type="email"
        value={form.username}
        onChange={handleChange}
        className="w-full p-2 border rounded"
        required
      />

      <input
        type="password"
        name="password"
        placeholder="Password (min 6 characters)"
        value={form.password}
        onChange={handleChange}
        className="w-full p-2 border rounded"
        required
      />

      <select
        name="role"
        value={form.role}
        onChange={handleChange}
        className="w-full p-2 border rounded"
        required
      >
        <option value="">Select Role</option>
        <option value="USER">User</option>
        <option value="ADMIN">Admin</option>
      </select>

      <button className="w-full bg-green-500 text-white p-2 rounded">
        Register
      </button>

      <Link
        to="/login"
        className="block w-full text-center border border-gray-300 text-gray-700 p-2 rounded hover:bg-gray-100"
      >
        Already have an account? Log in
      </Link>
    </form>
  );
}
