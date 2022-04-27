import React from "react";

class CreateReservation extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            date: '',
            start: '',
            end: '',
            peopleComing: '',
            bowlingLaneNumber: ''
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(event) {
        console.log(this.state);
        console.log(JSON.stringify(this.state));

        const date = this.state.date;
        const start = this.state.start;
        const end = this.state.end;
        const peopleComing = this.state.peopleComing;
        const bowlingLaneNumber = this.state.bowlingLaneNumber;

        const datePlus1 = new Date(new Date(date).getTime() + 86400000).toISOString().split('T')[0];

        console.log(datePlus1);

        const base64 = require('base-64');
        const username = 'manager';
        const password = 'password1';
        fetch('http://localhost:8080/api/v1/reservation/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                Authorization: 'Basic ' + base64.encode(username + ":" + password)
            },
            body: JSON.stringify({
                start: date + 'T' + start,
                end: (start < end ? date + 'T' + end : datePlus1 + 'T' + end),
                peopleComing: peopleComing,
                bowlingLane: {number: bowlingLaneNumber}
            })

        }).then(response => response.json())
            .then(json => console.log(json));

        event.preventDefault();
    }

    handleChange(event) {
        this.setState({[event.target.name]: event.target.value})
    }

    render() {
        return (
            <div>
                <form onSubmit={this.handleSubmit}>
                    <label>Start: </label>
                    <input type={"date"} value={this.state.date} onChange={this.handleChange} name={"date"} />
                    <input type={"time"} value={this.state.start} onChange={this.handleChange} name={"start"} />

                    <br />
                    <label>End: </label>
                    <input type={"time"} value={this.state.end} onChange={this.handleChange} name={"end"} />

                    <br />
                    <label>Number of people coming:</label>
                    <input type={"number"} value={this.state.peopleComing}
                           onChange={this.handleChange} name={"peopleComing"} />

                    <br />
                    <label>Preferred lane: </label>
                    <input type={"number"} value={this.state.bowlingLaneNumber}
                           onChange={this.handleChange} name={"bowlingLaneNumber"} />

                    <br />
                    <br />
                    <input type={"submit"} value={"Make reservation"} />
                </form>
            </div>
        )
    }
}

export default CreateReservation;
