import { useState } from "react";
import { Link } from "react-router-dom";
import { register } from "../Services/api";
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

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  // const handleSubmit = async (e) => {
  //   e.preventDefault();
  //   try {
  //     const res = await register(form);
  //     if (res.token) {
  //       setSuccess("Registered successfully! You can now log in.");
  //       setError("");
  //     } else {
  //       setError("Registration failed.");
  //     }
  //   } catch {
  //     setError("Error registering.");
  //   }
  // };
  const handleSubmit = async (e) => {
  e.preventDefault();
  try {
    const res = await axios.post("http://localhost:8080/register", form); // Update URL as per your backend
    if (res.data?.token) {
      setSuccess("Registered successfully! You can now log in.");
      setError("");
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
    const msg = err?.response?.data?.message || "Error registering.";
    setError(msg);
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
      />
      <input
        name="lastName"
        placeholder="Last Name"
        value={form.lastName}
        onChange={handleChange}
        className="w-full p-2 border rounded"
      />
      <input
        name="username"
        placeholder="Username"
        value={form.username}
        onChange={handleChange}
        className="w-full p-2 border rounded"
      />
      <input
        type="password"
        name="password"
        placeholder="Password"
        value={form.password}
        onChange={handleChange}
        className="w-full p-2 border rounded"
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
