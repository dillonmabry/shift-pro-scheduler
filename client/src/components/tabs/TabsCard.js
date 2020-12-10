import React from "react";
import { Card } from "antd";
import PropTypes from "prop-types";

class TabsCard extends React.Component {
  state = {
    key: this.props.tabList.length > 0 ? this.props.tabList[0].key : "", //eslint-disable-line
  };

  componentDidUpdate() {
    if (!(this.state.key in this.props.contentList)) {
      this.setState({
        key: this.props.tabList.length > 0 ? this.props.tabList[0].key : "",
      });
    }
  }

  onTabChange = (key, type) => {
    this.setState({ [type]: key }); //eslint-disable-line
  };

  render() {
    return (
      <Card
        style={{ width: "100%" }}
        title={this.props.title}
        tabList={this.props.tabList}
        onTabChange={(key) => {
          this.onTabChange(key, "key");
        }}
      >
        {this.props.contentList[this.state.key]}
      </Card>
    );
  }
}

TabsCard.propTypes = {
  title: PropTypes.string,
  tabList: PropTypes.array,
  contentList: PropTypes.object,
};

export default TabsCard;
