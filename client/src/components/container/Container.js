import "./Container.css";
import React from "react";
import SideMenu from "../sidebar/Sidebar";
import { Layout, Breadcrumb } from "antd";
import PropTypes from "prop-types";
import { Link } from "react-router-dom";

const { Header, Content, Footer } = Layout;

const Container = ({ navItems, content }) => (
  <Layout style={{ minHeight: "100vh" }}>
    <SideMenu />
    <Layout className="site-layout">
      <Header className="site-layout-background" style={{ padding: 0 }}>
        <div className="logo" />
      </Header>
      <Content style={{ padding: "0 50px" }}>
        {navItems && (
          <Breadcrumb style={{ margin: "16px 0" }}>
            {navItems.map((item) => {
              return <Breadcrumb.Item key={item}>{item}</Breadcrumb.Item>;
            })}
          </Breadcrumb>
        )}
        <div className="site-layout-background" style={{ minHeight: 360 }}>
          {content}
        </div>
      </Content>
      <Footer style={{ textAlign: "center" }}>
        Shift Pro Scheduler |{" "}
        <a
          href="https://github.com/dillonmabry/shift-pro-scheduler"
          target="_blank"
          rel="noopener noreferrer"
        >
          Contribute
        </a>{" "}
        | <Link to="/about">About</Link>
      </Footer>
    </Layout>
  </Layout>
);
Container.propTypes = {
  navItems: PropTypes.arrayOf(PropTypes.string),
  content: PropTypes.oneOfType([PropTypes.string, PropTypes.object]),
};
export default Container;
