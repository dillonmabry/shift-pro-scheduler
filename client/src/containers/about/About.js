import { Spin, Card } from "antd";
import React, { useState, useEffect } from "react";
import Container from "../../components/container/Container";
import ApplicationService from "../../services/ApplicationService";

const About = () => {
  const [applicationInfo, setApplicationInfo] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    ApplicationService.getApplicationInfo()
      .then(
        (response) => {
          if (response.data) {
            setApplicationInfo(response.data);
          }
        },
        (error) => {
          NotificationService.notify(
            "error",
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
              error.message ||
              error.toString()
          );
        }
      )
      .then(() => {
        setLoading(false);
      });
  }, []);

  return (
    <Container
      navItems={["Home", "About Software"]}
      content={
        <div>
          {loading && <Spin size="large" />}
          {!loading && (
            <div>
              {applicationInfo && (
                <Card style={{ width: "75%" }}>
                  <p>
                    <strong>Version:</strong> {applicationInfo.app?.version}
                  </p>
                  <p>
                    <strong>License Info:</strong> MIT
                  </p>
                  <p>
                    <strong>Dependency Info:</strong> Google OR-Tools by Google
                    is licensed under{" "}
                    <a href="https://creativecommons.org/licenses/by/4.0/">
                      CC BY 4.0
                    </a>
                  </p>
                </Card>
              )}
            </div>
          )}
        </div>
      }
    ></Container>
  );
};

export default About;
