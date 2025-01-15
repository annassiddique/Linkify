import React, { FormEvent, useState } from "react";
import Layout from "../../components/layout/Layout";
import Box from "../../components/Box/Box";
import Input from "../../components/Input/Input";
import Button from "../../components/Button/Button";
import Separator from "../../components/Separator/Separator";
import { Link, useLocation, useNavigate } from "react-router-dom";
import classes from "./Login.module.scss";
import { usePageTitle } from "../../../../hooks/usePageTitle";
import useAuthentication from "../../context/AuthenticationContextProvider";

const Login = () => {
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  usePageTitle("Login");
  const { login } = useAuthentication();
  const doLogin = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);
    const email = e.currentTarget.email.value;
    const password = e.currentTarget.password.value;

    try {
      await login(email, password);
      const destination = location.state?.from || "/";
      navigate(destination);
    } catch (error) {
      if (error instanceof Error) {
        setErrorMessage(error.message);
      } else {
        setErrorMessage("An unknown error occurred.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Layout className={classes.root}>
      <Box>
        <h1>Sign in</h1>
        <p>Stay updated on your professional world.</p>
        <form onSubmit={doLogin}>
          <Input
            label="Email"
            type="email"
            id="email"
            onFocus={() => setErrorMessage("")}
          />
          <Input
            label="Password"
            type="password"
            id="password"
            onFocus={() => setErrorMessage("")}
          />
          {errorMessage && <p className={classes.error}>{errorMessage}</p>}

          <Button type="submit" disabled={isLoading}>
            {isLoading ? "..." : "Sign in"}
          </Button>
          <Link to="/authentication/request-password-reset">
            Forgot password?
          </Link>
        </form>
        <Separator>Or</Separator>
        <div className={classes.register}>
          New to LinkedIn? <Link to="/authentication/signup">Join now</Link>
        </div>
      </Box>
    </Layout>
  );
};

export default Login;
