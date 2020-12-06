import Container from "../../components/container/Container";
import React, { Component } from "react";
import "./Schedule.css";
import { Spin, Empty } from "antd";
import ScheduleService from "../../services/ScheduleService";
import BigCalendar from "../../components/calendar/BigCalendar";
import TabsCard from "../../components/tabs/TabsCard";
import parseISO from "date-fns/parseISO";
import addDays from "date-fns/addDays";
import setHours from "date-fns/setHours";
import setMinutes from "date-fns/setMinutes";
import format from "date-fns/format";

const formatScheduleKey = (start, end, style) => {
  return `${format(parseISO(start), style)} - ${format(parseISO(end), style)}`;
};
export default class Schedule extends Component {
  constructor(props) {
    super(props);

    this.state = {
      tabList: [],
      contentList: {},
      loading: true,
    };
  }

  componentDidMount() {
    ScheduleService.getSchedules()
      .then(
        (response) => {
          if (response.data.scheduleList) {
            const _schedules = {};
            response.data.scheduleList.forEach((schedule) => {
              const _assignments = [];
              const startDate = parseISO(schedule.createdAt);
              schedule.assignments.forEach((assignment) => {
                _assignments.push({
                  id: assignment.id,
                  schedule_id: schedule.id,
                  title: `${assignment.employee.lastName}, ${assignment.employee.firstName}`,
                  allDay: false,
                  start: setMinutes(
                    setHours(
                      addDays(startDate, assignment.dayId),
                      assignment.shift.startTime.split(":")[0]
                    ),
                    assignment.shift.startTime.split(":")[1]
                  ),
                  end: setMinutes(
                    setHours(
                      addDays(startDate, assignment.dayId),
                      assignment.shift.endTime.split(":")[0]
                    ),
                    assignment.shift.endTime.split(":")[1]
                  ),
                });
              });
              _schedules[schedule.id] = <BigCalendar events={_assignments} />;
            });

            this.setState({
              tabList: response.data.scheduleList.map((s) => ({
                key: s.id,
                tab: formatScheduleKey(s.createdAt, s.endDate, "MMM d"),
              })),
              contentList: _schedules,
            });
          }
        },
        (error) => {
          this.setState({
            content:
              (error.response &&
                error.response.data &&
                error.response.data.message) ||
              error.message ||
              error.toString(),
          });
        }
      )
      .then(() => {
        this.setState({
          loading: false,
        });
      });
  }

  render() {
    return (
      <Container
        content={
          <div>
            {this.state.loading && <Spin />}
            {!this.state.loading && (
              <div>
                {this.state.tabList.length > 0 ? (
                  <TabsCard
                    title={"Schedules"}
                    tabList={this.state.tabList}
                    contentList={this.state.contentList}
                  />
                ) : (
                  <Empty />
                )}
              </div>
            )}
          </div>
        }
      ></Container>
    );
  }
}
