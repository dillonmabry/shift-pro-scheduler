import React from "react";
import { Calendar, Views, dateFnsLocalizer } from "react-big-calendar";
import format from "date-fns/format";
import parse from "date-fns/parse";
import differenceInDays from "date-fns/differenceInDays";
import addDays from "date-fns/addDays";
import startOfWeek from "date-fns/startOfWeek";
import getDay from "date-fns/getDay";
import withDragAndDrop from "react-big-calendar/lib/addons/dragAndDrop";
import PropTypes from "prop-types";
import AssignmentService from "../../services/AssignmentService";
import NotificationService from "../../services/NotificationService";
import { zipArrays } from "../../utilities/Manipulation";

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

class DnDCalendar extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      events: this.props.events ? this.props.events : [],
      minDate: this.props.events
        ? new Date(Math.min(...this.props.events.map((e) => new Date(e.start))))
        : null,
      maxDate: this.props.events
        ? new Date(Math.max(...this.props.events.map((e) => new Date(e.end))))
        : null,
      displayDragItemInCell: true,
      shiftIds: new Map(),
      view: Views.MONTH,
    };
    const dayIds = new Map(
      [
        ...Array(
          differenceInDays(this.state.maxDate, this.state.minDate) + 1
        ).keys(),
      ].map((i) => [format(addDays(this.state.minDate, i), "MM-dd-yy"), i])
    );
    const shiftTimes = [
      ...new Set(this.state.events.map((e) => e.shift.startTime)),
    ];
    const shifts = zipArrays(Array.from(dayIds.keys()), shiftTimes);

    shifts.forEach((shift) => {
      this.state.shiftIds.set(shift[0] + shift[1], dayIds.get(shift[0]));
    });

    this.moveEvent = this.moveEvent.bind(this);
  }

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
    const dayId = this.state.shiftIds.get(
      format(start, "MM-dd-yy") + format(start, "HH:mm:ss")
    );
    if (!dayId) {
      NotificationService.notify(
        "error",
        "Selected time slot not available, use existing shifts"
      );
      return;
    }
    AssignmentService.saveAssignment({
      id: event.id,
      employee: event.employee,
      dayId: dayId,
      shift: event.shift,
      schedule: event.schedule,
    }).then(
      (response) => {
        if (response.data) {
          const { events } = this.state;
          let allDay = event.allDay;
          if (!event.allDay && droppedOnAllDaySlot) {
            allDay = true;
          } else if (event.allDay && !droppedOnAllDaySlot) {
            allDay = false;
          }
          const nextEvents = events.map((existingEvent) => {
            return existingEvent.id == event.id
              ? { ...existingEvent, start, end, allDay }
              : existingEvent;
          });
          this.setState({
            events: nextEvents,
          });
          NotificationService.notify(
            "success",
            "Successfully updated Assignment"
          );
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
    );
  };

  render() {
    return (
      <DragAndDropCalendar
        selectable
        resizable={false}
        localizer={localizer}
        events={this.state.events}
        onEventDrop={this.moveEvent}
        defaultView={Views.MONTH}
        popup={true}
        onView={(view) => this.setState({ view })}
        resizableAccessor={() => this.state.view !== Views.MONTH}
        draggableAccessor={() => this.state.view !== Views.MONTH}
        onSelecting={() => false}
        onDropFromOutside={this.onDropFromOutside}
        startAccessor="start"
        endAccessor="end"
        style={{ height: 650 }}
      />
    );
  }
}

DnDCalendar.propTypes = {
  events: PropTypes.array,
};

export default DnDCalendar;
