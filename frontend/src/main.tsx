import { Children, StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.scss";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import Feed from "./features/feed/pages/Feed";
import Login from "./features/authentication/pages/Login/Login";
import Signup from "./features/authentication/pages/Signup/Signup";
import ResetPassword from "./features/authentication/pages/ResetPassword/ResetPassword";
import VerifyEmail from "./features/authentication/pages/VerifyEmail/VerifyEmail";
import { AuthenticationContextProvider } from "./features/authentication/context/AuthenticationContextProvider";

const router = createBrowserRouter([
  {
    element: <AuthenticationContextProvider />,
    children: [
      {
        path: "/",
        element: <Feed />,
      },
      {
        path: "/authentication/login",
        element: <Login />,
      },
      {
        path: "/authentication/signup",
        element: <Signup />,
      },
      {
        path: "/authentication/request-password-reset",
        element: <ResetPassword />,
      },
      {
        path: "/authentication/verify-email",
        element: <VerifyEmail />,
      },
    ],
  },
]);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>
);
