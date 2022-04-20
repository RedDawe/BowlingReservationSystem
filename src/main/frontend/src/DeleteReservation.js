import React from 'react';

class DeleteReservation extends React.Component {
    constructor(props) {
        super(props);
        this.state = {response:[]};

        this.handleClick = this.handleClick.bind(this);
    }

    componentDidMount() {
        this.fetchReservations();
    }

    handleClick(event) {
        fetch('http://localhost:8080/api/v1/reservation/delete/'
            + event.target.getAttribute('data-key'), {
            method: 'DELETE'
        }).then(response => {
            console.log(response);
        });
    }

    fetchReservations() {
        const base64 = require('base-64');
        const username = 'manager';
        const password = 'password1';

        fetch('http://localhost:8080/api/v1/reservation/get-my', {
            headers: {
                Authorization: 'Basic ' + base64.encode(username + ":" + password)
            }
        })
            .then(response => response.json())
            .then(reservations => this.setState({response: reservations}))
    }

    getReservations() {
        return this.state.response.map(reservation => {
            return (
                <div key={reservation.id}>
                    <div>
                        <p> Start: { reservation.start.replace('T', ' -> ') }</p>
                        <p> End: { reservation.end.replace('T', ' -> ') }</p>
                        <p> PeopleComing: { reservation.peopleComing }</p>
                        <p> Bowling Lane: { reservation.bowlingLane.number }</p>

                        <button data-key={ reservation.id } onClick={ this.handleClick }>Delete</button>
                    </div>
                </div>
            )
        })
    }

    render() {
        return (
            <div>
                { this.getReservations() }
            </div>
        )
    }
}

export default DeleteReservation;
