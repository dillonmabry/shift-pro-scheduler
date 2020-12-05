import React from "react";
import { Calendar, dateFnsLocalizer, Views } from "react-big-calendar";
import format from "date-fns/format";
import parse from "date-fns/parse";
import startOfWeek from "date-fns/startOfWeek";
import getDay from "date-fns/getDay";
import PropTypes from "prop-types";

const locales = {
  "en-US": require("date-fns/locale/en-US"),
};

const allViews = Object.keys(Views).map((k) => Views[k]);

const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek,
  getDay,
  locales,
});

const BigCalendar = (props) => (
  <div>
    <Calendar
      popup
      showMultiDayTimes
      views={allViews}
      localizer={localizer}
      events={props.events}
      startAccessor="start"
      endAccessor="end"
      style={{ height: 650 }}
    />
  </div>
);

BigCalendar.propTypes = {
  events: PropTypes.array,
};

export default BigCalendar;
