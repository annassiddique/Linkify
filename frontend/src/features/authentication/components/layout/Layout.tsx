import React, { ReactNode } from "react";
import classes from "./Layout.module.scss";
import { Link } from "react-router-dom";

const footerList = [
  {
    name: "Accessibility",
    link: "",
    id: 1,
  },
  {
    name: "User Agreementt",
    link: "",
    id: 2,
  },
  {
    name: "Privacy Policy",
    link: "",
    id: 3,
  },
  {
    name: "Cookie Policy",
    link: "",
    id: 4,
  },
  {
    name: "Copyright Policy",
    link: "",
    id: 5,
  },
  {
    name: "Brand Policy",
    link: "",
    id: 6,
  },
  {
    name: "Guest Controls",
    link: "",
    id: 7,
  },
  {
    name: "Community Guidelines",
    link: "",
    id: 8,
  },
  {
    name: "Language",
    link: "",
    id: 9,
  },
];

const Layout = ({
  children,
  className,
}: {
  children: ReactNode;
  className?: string;
}) => {
  return (
    <div className={`${classes.root} ${className}`}>
      <header className={classes.container}>
        <Link to={"/"}>
          <img src="/logo.svg" alt="linkify" className={classes.logo} />
        </Link>
      </header>
      <main className={classes.container}>{children}</main>
      <footer>
        <ul className={classes.container}>
          <li>
            <img src="/logo-dark.svg" alt="linkify" />
            <span> 2024</span>
          </li>
          {footerList &&
            footerList.map((link) => (
              <li key={link.id}>
                <Link to={`${link.link}`}>{link.name}</Link>
              </li>
            ))}
        </ul>
      </footer>
    </div>
  );
};

export default Layout;
