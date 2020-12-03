import './Container.css';
import React from 'react';
import SideMenu from '../sidebar/Sidebar';
import {Layout} from 'antd';
import PropTypes from 'prop-types';

const {Header, Content, Footer} = Layout;

const Container = ({content}) => (
  <Layout style={{minHeight: '100vh'}}>
    <SideMenu />
    <Layout className="site-layout">
      <Header className="site-layout-background" style={{padding: 0}}>
        <div className="logo" />
      </Header>
      <Content>
        <div
          className="site-layout-background"
          style={{padding: 24, minHeight: 360}}
        >
          {content}
        </div>
      </Content>
      <Footer style={{textAlign: 'center'}}>
        Shift Pro | MIT License |{' '}
        <a href="https://github.com/dillonmabry/shift-pro-scheduler">Github</a>
      </Footer>
    </Layout>
  </Layout>
);
Container.propTypes = {
  content: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.object,
  ]),
};
export default Container;
