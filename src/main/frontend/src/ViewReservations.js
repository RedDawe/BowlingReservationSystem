import React from 'react';
import {Calendar, dateFnsLocalizer} from 'react-big-calendar'
import format from 'date-fns/format'
import parse from 'date-fns/parse'
import startOfWeek from 'date-fns/startOfWeek'
import getDay from 'date-fns/getDay'
import enUS from 'date-fns/locale/en-US'
import 'react-big-calendar/lib/css/react-big-calendar.css'
import './rbc-custom-style.css';

const locales = {
    'en-US': enUS,
}

const localizer = dateFnsLocalizer({
    format,
    parse,
    startOfWeek,
    getDay,
    locales,
})

const MonthEvent = ({event}) => (
    <div>
        <div> {event.peopleComing} </div>
    </div>
);

const eventStyleGetter = (event, start, end, isSelected) => {
    const backgroundColor = event.isMine ? 'green' : 'red';

    return {
        style: {
            backgroundColor: backgroundColor
        }
    };
}


class ViewReservations extends React.Component {
    constructor(props) {
        super(props);
        this.state = {response: [], eventList: [], bowlingLanes: []};

    }

    componentDidMount() {
        this.fetchReservations();
        this.fetchBowlingLanes();
    }

    fetchBowlingLanes() {
        fetch('http://localhost:8080/api/v1/bowling-lane/get')
            .then(response => response.json())
            .then(bowlingLanes => {
                this.setState({bowlingLanes: bowlingLanes});
            })
    }

    fetchReservations() {
        fetch('http://localhost:8080/api/v1/reservation/get-all')
            .then(response => response.json())
            .then(reservations => {
                this.setState({response: reservations}, () => this.addEvents());
            })
    }

    addEvents() {
        const events = this.getReservations();
        this.setState({response: this.state.response, eventList: events});
    }

    getReservations() {
        return this.state.response.map(reservation => {
            return ({
                    start: new Date(reservation.start),
                    end: new Date(reservation.end),
                    peopleComing: reservation.peopleComing,
                    isMine: reservation.isMine,
                    bowlingLane: reservation.bowlingLane,
                }
            )
        })
    }


    myCalendar(number, eventList) {
        return (
            <div key={number} className={'rbc-master-box'}>
                <p className={'bowling-lane-label'}>{'Lane number : ' + number.toString()}</p>
                <Calendar
                    localizer={localizer}
                    events={eventList}
                    startAccessor="start"
                    endAccessor="end"
                    style={{height: 1000, width: 200}}
                    defaultView={'day'}
                    showMultiDayTimes={true}

                    components={{
                        event: MonthEvent,
                    }}
                    eventPropGetter={(eventStyleGetter)}
                />
            </div>
        )
    }

    calendarForEachLane() {
        const calendars = []
        let index = 0;
        this.state.bowlingLanes.forEach(bowlingLane => {
            const i = bowlingLane.number;
            calendars[index++] = this.myCalendar(i, this.state.eventList.filter((event) => event.bowlingLane.number === i))
        })

        return calendars;
    }

    render() {
        return (
            <div>
                {this.calendarForEachLane()}
            </div>
        )
    }
}

export default ViewReservations;
