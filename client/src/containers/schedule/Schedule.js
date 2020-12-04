import Container from '../../components/container/Container';
import React, {Component} from 'react';
import ScheduleService from '../../services/ScheduleService';

export default class Schedule extends Component {
  constructor(props) {
    super(props);

    this.state = {
      content: '',
    };
  }

  componentDidMount() {
    ScheduleService.getSchedules().then(
        (response) => {
          this.setState({
            content: 'schedule',
          });
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
        },
    );
  }

  render() {
    return <Container content={this.state.content}></Container>;
  }
}
