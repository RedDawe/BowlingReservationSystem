import React from 'react';
import { Calendar, dateFnsLocalizer } from 'react-big-calendar'
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

const firstEvent = {
    start: new Date('2022-04-12T09:00'),
    end: new Date('2022-04-12T10:00'),
    // title: 'HOLA HOLA KAPITANE'
}

const myEventsList = [firstEvent];

const MonthEvent = ({ event }) => (
    <div className={'event.someProp ? styles.specialEvent : styles.event'}>
        {/*<div className={'styles.eventTime'}>{event.start.toString()}</div>*/}
        {/*<div className={'styles.eventName'}>{event.end.toString()}</div>*/}
        <div>There are definitely some poeple coming</div>
    </div>
);

const eventStyleGetter = (event, start, end, isSelected) => {
    return {
        style: {
            backgroundColor: 'green',
            // backgroundColor: '#' + event.hexColor,
            // borderRadius: '0px',
            // opacity: 0.8,
            // color: 'black',
            // border: '0px',
            // display: 'block'
        }
    };
}

const MyCalendar = props => (
    <div>
        <Calendar
            localizer={localizer}
            events={myEventsList}
            startAccessor="start"
            endAccessor="end"
            // titleAccesor="title"
            style={{ height: 1000, width: 400 }}
            defaultView={'day'}
            // date={"2022-03-12"}

            components={{
                event: MonthEvent,
            }}
            // formats={{ eventTimeRangeFormat: () => null }}
            eventPropGetter={(eventStyleGetter)}
        />
    </div>
)

class ViewReservations extends React.Component {
    constructor(props) {
        super(props);
        this.state = {response:[]};

    }

    componentDidMount() {
        this.fetchReservations();
    }

    fetchReservations() {
        fetch('http://localhost:8080/api/v1/reservation/get-all')
            .then(response => response.json())
            .then(reservations => this.setState({response: reservations}))
    }

    getReservations() {
        return this.state.response.map(reservation => {
            return (
                <div key={reservation.id}>
                    { reservation.peopleComing }
                </div>


            )
        })
    }



    render() {
        return (
            <div>
                { this.getReservations() }
                <MyCalendar />
            </div>
        )
    }
}

export default ViewReservations;
