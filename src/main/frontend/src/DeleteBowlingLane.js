import React from 'react';

class DeleteBowlingLane extends React.Component{
    constructor(props) {
        super(props);
        this.state = {value: '', couldNotReassign: []};

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }


    handleSubmit(event) {
        fetch('/api/v1/bowling-lane/delete/' + this.state.value, {
            method: 'DELETE'
        })
            .then(response => response.json())
            .then(responseJson => {
                this.setState({value: '', couldNotReassign: responseJson})
            })

        event.preventDefault();
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }

    getCouldNotReassign() {
        return this.state.couldNotReassign.map(reservation => {
            return (
                <div>
                    <br/>
                    {reservation}
                </div>
            )
        })
    }

    render() {
        return (
            <div>
                <form onSubmit={this.handleSubmit}>
                    <label>Number: </label>
                    <input type={"number"} value={this.state.value} onChange={this.handleChange} />
                    <br/>
                    <input type={"submit"} value={"Remove"} />

                    <br/>
                    <div><p>Could not reassign from last call: </p>{this.getCouldNotReassign()}</div>
                </form>
            </div>
        );
    }
}

export default DeleteBowlingLane;
