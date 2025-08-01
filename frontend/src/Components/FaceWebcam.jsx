import React, { useRef, useEffect, useState } from "react";
import * as faceapi from "face-api.js";

export default function FaceWebcam({ onDescriptor }) {
  const videoRef = useRef();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [faceCaptured, setFaceCaptured] = useState(false);

  useEffect(() => {
    async function loadModels() {
      try {
        await Promise.all([
          faceapi.nets.tinyFaceDetector.loadFromUri("/models"),
          faceapi.nets.faceLandmark68Net.loadFromUri("/models"),
          faceapi.nets.faceRecognitionNet.loadFromUri("/models"),
        ]);
        setLoading(false);
      } catch (e) {
        setError("❌ Failed to load face-api models");
        setLoading(false);
      }
    }

    loadModels();
  }, []);

  const startVideo = () => {
    navigator.mediaDevices
      .getUserMedia({ video: true })  
      .then((stream) => {
        videoRef.current.srcObject = stream;
      })
      .catch(() => setError("❌ Cannot access webcam"));
  };

  useEffect(() => {
    if (!loading) {
      startVideo();
    }

    return () => {
      if (videoRef.current && videoRef.current.srcObject) {
        videoRef.current.srcObject.getTracks().forEach((track) => track.stop());
      }
    };
  }, [loading]);

  const captureFace = async () => {
    if (!videoRef.current) return;

    const detection = await faceapi
      .detectSingleFace(videoRef.current, new faceapi.TinyFaceDetectorOptions())
      .withFaceLandmarks()
      .withFaceDescriptor();

    if (detection && detection.descriptor) {
      const descriptor = Array.from(detection.descriptor);
      onDescriptor(descriptor);
      setFaceCaptured(true);
      setError("");
    } else {
      setError("❌ No face detected. Please try again.");
    }
  };

  return (
    <div className="flex flex-col items-center">
      <video
        ref={videoRef}
        autoPlay
        muted
        width={320}
        height={240}
        className="rounded border mb-2"
      />
      <button
      type="button"
        onClick={captureFace}
        disabled={loading}
        className={`px-4 py-2 rounded text-white mb-2 ${
          loading ? "bg-gray-400" : "bg-blue-600 hover:bg-blue-700"
        }`}
      >
        {loading ? "Loading models..." : "Capture Face"}
      </button>
      {error && <p className="text-red-500">{error}</p>}
    </div>
  );
}
