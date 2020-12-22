import React, { useEffect, useState } from "react";
import { Card } from "antd";
import PropTypes from "prop-types";

const TabsCard = (props) => {
  const [state, setState] = useState({
    key: props.tabList.length > 0 ? props.tabList[0].key : "",
  });

  useEffect(() => {
    if (!(state.key in props.contentList)) {
      setState({
        key: props.tabList.length > 0 ? props.tabList[0].key : "",
      });
    }
  }, []);

  const onTabChange = (key, type) => {
    setState({
      [type]: key,
    });
  };

  return (
    <Card
      style={{ width: "100%" }}
      title={props.title}
      tabList={props.tabList}
      onTabChange={(key) => {
        onTabChange(key, "key");
      }}
    >
      {props.contentList[state.key]}
    </Card>
  );
};

TabsCard.propTypes = {
  title: PropTypes.string,
  tabList: PropTypes.array,
  contentList: PropTypes.object,
};

export default TabsCard;
