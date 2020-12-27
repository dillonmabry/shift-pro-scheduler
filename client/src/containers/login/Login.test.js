import React from "react";
import { MemoryRouter } from "react-router-dom";
import { render } from "@testing-library/react";
import Login from "./Login";

test("current user is active in login", () => {
  const root = document.createElement("div");
  document.body.appendChild(root);

  render(
    <MemoryRouter>
      <Login />
    </MemoryRouter>,
    root
  );
  expect(document.body.textContent.toLowerCase()).toContain("login");
  expect(document.body.textContent.toLowerCase()).toContain("forgot");
  expect(document.body.textContent.toLowerCase()).toContain("register");
});
