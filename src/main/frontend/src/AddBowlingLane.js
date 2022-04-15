import React from "react";

class AddBowlingLane extends React.Component {
    constructor(props) {
        super(props);
        this.state = {value: ''};

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }


    handleSubmit(event) {
        fetch('http://localhost:8080/api/v1/bowling-lane/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({number: this.state.value})
        }).then(response => {
            console.log(response);
            console.log(response.json());
        });

        this.setState({value: ''})
        event.preventDefault();
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }

    render() {
        return (
            <div>
                <form onSubmit={this.handleSubmit}>
                    <label>Number: </label>
                    <input type={"number"} value={this.state.value} onChange={this.handleChange} />
                    <br/>
                    <input type={"submit"} value={"Add"} />
                </form>
            </div>
        );
    }
}

export default AddBowlingLane;
