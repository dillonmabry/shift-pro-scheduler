import React from "react";
import { Calendar, Views, dateFnsLocalizer } from "react-big-calendar";
import format from "date-fns/format";
import parse from "date-fns/parse";
import differenceInDays from "date-fns/differenceInDays";
import addDays from "date-fns/addDays";
import addMinutes from "date-fns/addMinutes";
import startOfWeek from "date-fns/startOfWeek";
import isWithinInterval from "date-fns/isWithinInterval";
import parseISO from "date-fns/parseISO";
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
      shifts: this.props.shifts ? this.props.shifts : [],
      minDate: this.props.minDate,
      maxDate: this.props.maxDate,
      displayDragItemInCell: true,
      dayIds: new Map(),
      shiftIds: new Map(),
      view: Views.MONTH,
    };
    // Set available shiftIds (shift/day)
    const dayIds = new Map(
      [
        ...Array(
          differenceInDays(this.state.maxDate, this.state.minDate) + 1
        ).keys(),
      ].map((i) => [format(addDays(this.state.minDate, i), "yyyy-MM-dd"), i])
    );
    const shiftTimes = [
      ...new Set(this.state.events.map((e) => e.shift.startTime)),
    ];
    const shifts = zipArrays(Array.from(dayIds.keys()), shiftTimes);
    shifts.forEach((shift) => {
      this.state.dayIds.set(shift[0] + shift[1], dayIds.get(shift[0]));
    });
    // Set available shifts
    this.state.shifts.forEach((shift) => {
      this.state.shiftIds.set(shift.shiftTime, shift.id);
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
    if (
      !isWithinInterval(parseISO(format(start, "yyyy-MM-dd"), "yyyy-MM-dd"), {
        start: this.state.minDate,
        end: addMinutes(this.state.maxDate, -30),
      }) ||
      !this.state.shiftIds.get(
        `${format(start, "HH:mm:ss")} - ${format(end, "HH:mm:ss")}`
      )
    ) {
      NotificationService.notify(
        "error",
        "Selected time slot not available, use existing shifts"
      );
      return;
    }
    AssignmentService.saveAssignment({
      id: event.id,
      employee: event.employee,
      dayId: this.state.dayIds.get(
        format(start, "yyyy-MM-dd") + format(start, "HH:mm:ss")
      ),
      shift: {
        ...event.shift,
        id: this.state.shiftIds.get(
          `${format(start, "HH:mm:ss")} - ${format(end, "HH:mm:ss")}`
        ),
        startTime: format(start, "HH:mm:ss"),
        endTime: format(end, "HH:mm:ss"),
        shiftTime: `${format(start, "HH:mm:ss")} - ${format(end, "HH:mm:ss")}`,
      },
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
        slotPropGetter={(date) => ({
          style: {
            backgroundColor: isWithinInterval(date, {
              start: this.state.minDate,
              end: addMinutes(this.state.maxDate, -30),
            })
              ? "#fff"
              : "#ebebeb",
          },
        })}
        dayPropGetter={(date) => ({
          style: {
            backgroundColor: isWithinInterval(date, {
              start: this.state.minDate,
              end: addDays(this.state.maxDate, -1),
            })
              ? "#fff"
              : "#ebebeb",
          },
        })}
        startAccessor="start"
        endAccessor="end"
        style={{ height: 650 }}
      />
    );
  }
}

DnDCalendar.propTypes = {
  events: PropTypes.arrayOf(PropTypes.object),
  shifts: PropTypes.arrayOf(PropTypes.object),
  minDate: PropTypes.instanceOf(Date),
  maxDate: PropTypes.instanceOf(Date),
};

export default DnDCalendar;
