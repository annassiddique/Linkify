import { ReactNode } from "react";
import classes from "./Separator.module.scss";

export default function Separator({ children }: { children?: ReactNode }) {
  return <div className={classes.separator}>{children}</div>;
}