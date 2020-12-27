import Container from "../../components/container/Container";
import React, { useState, useEffect, useRef } from "react";
import "./Schedule.css";
import { Spin, Empty, Tag } from "antd";
import ScheduleService from "../../services/ScheduleService";
import BigCalendar from "../../components/calendar/BigCalendar";
// import DnDCalendar from "../../components/calendar/DragAndDropCalendar";
import CreateSchedule from "./CreateSchedule";
import TabsCard from "../../components/tabs/TabsCard";
import parseISO from "date-fns/parseISO";
import addDays from "date-fns/addDays";
import setHours from "date-fns/setHours";
import setMinutes from "date-fns/setMinutes";
import format from "date-fns/format";
import AuthService from "../../services/AuthService";
import ScheduleDetail from "./ScheduleDetail";
import NotificationService from "../../services/NotificationService";

const Schedule = () => {
  const [loading, setLoading] = useState(true);
  const [tabList, setTabList] = useState([]);
  const [contentList, setContentList] = useState({});
  const userInfoRef = useRef(null);

  const formatScheduleKey = (start, end, style) => {
    return `${format(parseISO(start), style)} - ${format(
      parseISO(end),
      style
    )}`;
  };

  const formatScheduleContent = (schedule, assignments) => {
    return (
      <div style={{ textAlign: "left", margin: "15px" }}>
        <div>
          <div style={{ marginBottom: "15px" }}>
            {schedule.isActive ? (
              <Tag color="blue">Live</Tag>
            ) : (
              <Tag color="orange">Draft</Tag>
            )}
          </div>
          <div style={{ marginBottom: "15px" }}>
            {AuthService.getRoles(userInfoRef.current.authorities).includes(
              "ROLE_ADMIN"
            ) && (
              <ScheduleDetail
                schedule={schedule}
                updateEventsList={updateEventsList}
                setLoading={setLoading}
              />
            )}
          </div>
        </div>
        <BigCalendar events={assignments} />
      </div>
    );
  };

  const getScheduleList = (scheduleList) => {
    const _schedules = {};
    const assignedScheduleList = AuthService.getRoles(
      userInfoRef.current.authorities
    ).includes("ROLE_USER")
      ? scheduleList.filter((s) => s.isActive === true)
      : scheduleList.filter(
          (s) => s.administrator.userName === userInfoRef.current.username
        );
    assignedScheduleList.forEach((schedule) => {
      const _assignments = [];
      const startDate = parseISO(schedule.startDate);
      const scheduledAssignments = AuthService.getRoles(
        userInfoRef.current.authorities
      ).includes("ROLE_USER")
        ? schedule.assignments.filter(
            (a) => a.employee.userName === userInfoRef.current.username
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
        _schedules[schedule.id] = formatScheduleContent(schedule, _assignments);
    });
    return _schedules;
  };

  const updateEventsList = () => {
    if (userInfoRef.current) {
      ScheduleService.getSchedules()
        .then(
          (response) => {
            if (
              response.data.scheduleList &&
              response.data.scheduleList.length > 0
            ) {
              const schedules = getScheduleList(response.data.scheduleList);
              setTabList(
                response.data.scheduleList
                  .filter((s) => s.id in schedules)
                  .map((s) => ({
                    key: s.id,
                    tab: formatScheduleKey(s.startDate, s.endDate, "MMM d"),
                  }))
              );
              setContentList(schedules);
            } else {
              setTabList([]);
              setContentList({});
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
        )
        .then(() => {
          setLoading(false);
        });
    }
  };

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    userInfoRef.current = user;
    updateEventsList();
  }, []);

  return (
    <Container
      content={
        <div>
          {loading && <Spin />}
          {!loading && (
            <div>
              {AuthService.getRoles(userInfoRef.current.authorities).includes(
                "ROLE_ADMIN"
              ) && (
                <div>
                  <CreateSchedule
                    updateEventsList={updateEventsList}
                    setLoading={setLoading}
                  />
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
};

export default Schedule;
