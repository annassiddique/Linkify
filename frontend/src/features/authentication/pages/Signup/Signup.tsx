import React, { FormEvent, useState } from "react";
import Layout from "../../components/layout/Layout";
import Box from "../../components/Box/Box";
import Input from "../../components/Input/Input";
import Button from "../../components/Button/Button";
import classes from "./Signup.module.scss";
import { Link, useNavigate } from "react-router-dom";
import Separator from "../../components/Separator/Separator";
import useAuthentication from "../../context/AuthenticationContextProvider";
import { usePageTitle } from "../../../../hooks/usePageTitle";

const Signup = () => {
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { signup } = useAuthentication();
  const navigate = useNavigate();
  usePageTitle("Signup");
  const doSignup = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);
    const email = e.currentTarget.email.value;
    const password = e.currentTarget.password.value;
    try {
      await signup(email, password);
      navigate("/");
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
        <h1>Sign up</h1>
        <p>Make the most of your professional life.</p>
        <form onSubmit={doSignup}>
          <Input
            type="email"
            id="email"
            label="Email"
            onFocus={() => setErrorMessage("")}
          />

          <Input
            label="Password"
            type="password"
            id="password"
            onFocus={() => setErrorMessage("")}
          />
          {errorMessage && <p className={classes.error}>{errorMessage}</p>}
          <p className={classes.disclaimer}>
            By clicking Agree & Join or Continue, you agree to Linkify's{" "}
            <a href="">User Agreement</a>, <a href="">Privacy Policy</a>, and{" "}
            <a href="">Cookie Policy</a>.
          </p>
          <Button disabled={isLoading} type="submit">
            Agree & Join
          </Button>
        </form>
        <Separator>Or</Separator>
        <div className={classes.register}>
          Already on Linkify? <Link to="/authentication/login">Sign in</Link>
        </div>
      </Box>
    </Layout>
  );
};

export default Signup;
