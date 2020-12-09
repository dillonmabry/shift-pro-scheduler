import Container from "../../components/container/Container";
import React, { Component } from "react";
import "./Schedule.css";
import { Spin, Empty, Tag } from "antd";
import ScheduleService from "../../services/ScheduleService";
import BigCalendar from "../../components/calendar/BigCalendar";
import CreateSchedule from "./CreateSchedule";
import TabsCard from "../../components/tabs/TabsCard";
import parseISO from "date-fns/parseISO";
import addDays from "date-fns/addDays";
import setHours from "date-fns/setHours";
import setMinutes from "date-fns/setMinutes";
import format from "date-fns/format";
import AuthService from "../../services/AuthService";
import ScheduleDetail from "./ScheduleDetail";

const formatScheduleKey = (start, end, style) => {
  return `${format(parseISO(start), style)} - ${format(parseISO(end), style)}`;
};

const formatScheduleContent = (schedule, assignments, user) => {
  return (
    <div style={{ textAlign: "left", margin: "15px" }}>
      <div>
        <div style={{ marginBottom: "15px" }}>
          {schedule.isActive ? (
            <Tag color="green">Active</Tag>
          ) : (
            <Tag color="red">Inactive</Tag>
          )}
        </div>
        <div style={{ marginBottom: "15px" }}>
          {AuthService.getRoles(user.authorities).includes("ROLE_ADMIN") && (
            <ScheduleDetail schedule={schedule} />
          )}
        </div>
      </div>
      <BigCalendar events={assignments} />
    </div>
  );
};

const getScheduleList = (scheduleList, user) => {
  const _schedules = {};
  const assignedScheduleList = AuthService.getRoles(
    user.authorities
  ).includes("ROLE_USER")
    ? scheduleList.filter(
      s => s.isActive === true
    ) : scheduleList;
  assignedScheduleList.forEach((schedule) => {
    const _assignments = [];
    const startDate = parseISO(schedule.createdAt);
    const scheduledAssignments = AuthService.getRoles(
      user.authorities
    ).includes("ROLE_USER")
      ? schedule.assignments.filter(
          (a) => a.employee.userName === user.username
        )
      : schedule.assignments;
    scheduledAssignments.forEach((assignment) => {
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
    if (_assignments.length > 0)
      _schedules[schedule.id] = formatScheduleContent(
        schedule,
        _assignments,
        user
      );
  });
  return _schedules;
};

export default class Schedule extends Component {
  constructor(props) {
    super(props);

    this.state = {
      tabList: [],
      contentList: {},
      loading: true,
      showUser: true,
      showAdmin: false,
    };
  }

  componentDidMount() {
    const user = AuthService.getCurrentUser();
    if (user) {
      this.setState({
        showAdmin: AuthService.getRoles(user.authorities).includes(
          "ROLE_ADMIN"
        ),
        showUser: AuthService.getRoles(user.authorities).includes("ROLE_USER"),
      });
      ScheduleService.getSchedules()
        .then(
          (response) => {
            if (
              response.data.scheduleList &&
              response.data.scheduleList.length > 0
            ) {
              const schedules = getScheduleList(
                response.data.scheduleList,
                user
              );
              //eslint-disable-line
              this.setState({
                tabList: response.data.scheduleList
                  .filter((s) => s.id in schedules)
                  .map((s) => ({
                    key: s.id,
                    tab: formatScheduleKey(s.createdAt, s.endDate, "MMM d"),
                  })),
                contentList: schedules,
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
  }

  render() {
    const { loading, tabList, contentList, showAdmin } = this.state;
    return (
      <Container
        content={
          <div>
            {loading && <Spin />}
            {!loading && (
              <div>
                {showAdmin && (
                  <div>
                    <CreateSchedule />
                  </div>
                )}
                <br />
                <div>
                  {tabList.length > 0 ? (
                    <TabsCard
                      title={"Schedules"}
                      tabList={tabList}
                      contentList={contentList}
                    />
                  ) : (
                    <Empty />
                  )}
                </div>
              </div>
            )}
          </div>
        }
      ></Container>
    );
  }
}
