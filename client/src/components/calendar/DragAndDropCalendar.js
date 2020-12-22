import React from "react";
import { Calendar, Views, dateFnsLocalizer } from "react-big-calendar";
import format from "date-fns/format";
import parse from "date-fns/parse";
import startOfWeek from "date-fns/startOfWeek";
import getDay from "date-fns/getDay";
import withDragAndDrop from "react-big-calendar/lib/addons/dragAndDrop";
import PropTypes from "prop-types";

const DragAndDropCalendar = withDragAndDrop(Calendar);

const locales = {
  "en-US": require("date-fns/locale/en-US"),
};

const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek,
  getDay,
  locales,
});

const DateCell = ({ range, value, children }) => {
  const now = new Date();
  now.setHours(0, 0, 0, 0);
  return <div className={value < now ? "date-in-past" : ""}>{children}</div>;
};

class DnDCalendar extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      events: this.props.events ? this.props.events : [],
      minDate: this.props.events
        ? new Date(Math.max(...this.props.events.map((e) => new Date(e.start))))
        : null,
      maxDate: this.props.events
        ? new Date(Math.max(...this.props.events.map((e) => new Date(e.end))))
        : null,
      displayDragItemInCell: true,
    };

    this.moveEvent = this.moveEvent.bind(this);
    this.newEvent = this.newEvent.bind(this);
  }

  handleDragStart = (event) => {
    this.setState({ draggedEvent: event });
  };

  dragFromOutsideItem = () => {
    return this.state.draggedEvent;
  };

  onDropFromOutside = ({ start, end, allDay }) => {
    const { draggedEvent } = this.state;

    const event = {
      id: draggedEvent.id,
      title: draggedEvent.title,
      start,
      end,
      allDay: allDay,
    };

    this.setState({ draggedEvent: null });
    this.moveEvent({ event, start, end });
  };

  moveEvent = ({ event, start, end, isAllDay: droppedOnAllDaySlot }) => {
    const { events } = this.state;

    let allDay = event.allDay;

    if (!event.allDay && droppedOnAllDaySlot) {
      allDay = true;
    } else if (event.allDay && !droppedOnAllDaySlot) {
      allDay = false;
    }

    const nextEvents = events.map((existingEvent) => {
      return existingEvent.id == event.id
        ? { ...existingEvent, start, end }
        : existingEvent;
    });

    this.setState({
      events: nextEvents,
    });
  };

  newEvent(event) {
    // let idList = this.state.events.map(a => a.id)
    // let newId = Math.max(...idList) + 1
    // let hour = {
    //   id: newId,
    //   title: 'New Event',
    //   allDay: event.slots.length == 1,
    //   start: event.start,
    //   end: event.end,
    // }
    // this.setState({
    //   events: this.state.events.concat([hour]),
    // })
  }

  render() {
    return (
      <DragAndDropCalendar
        selectable
        localizer={localizer}
        events={this.state.events}
        onEventDrop={this.moveEvent}
        defaultView={Views.MONTH}
        components={{
          dateCellWrapper: DateCell,
        }}
        popup={true}
        dragFromOutsideItem={
          this.state.displayDragItemInCell ? this.dragFromOutsideItem : null
        }
        onDropFromOutside={this.onDropFromOutside}
        handleDragStart={this.handleDragStart}
        startAccessor="start"
        endAccessor="end"
        style={{ height: 650 }}
      />
    );
  }
}

DnDCalendar.propTypes = {
  events: PropTypes.array,
  minDate: PropTypes.instanceOf(Date),
  maxDate: PropTypes.instanceOf(Date),
  displayDragItemInCell: PropTypes.bool,
};

export default DnDCalendar;
