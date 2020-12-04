import { notification } from "antd";

const notify = (type, message) => {
  notification[type]({
    message: message,
  });
};

const NotificationService = {
  notify,
};

export default NotificationService;
