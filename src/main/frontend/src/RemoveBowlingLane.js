import React from 'react';

class RemoveBowlingLane extends React.Component{
    constructor(props) {
        super(props);
        this.state = {value: ''};

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }


    handleSubmit(event) {
        fetch('http://localhost:8080/api/v1/bowling-lane/remove/' + this.state.value, {
            method: 'DELETE'
        }).then(response => {
            console.log(response);
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
                    <label>Number:</label>
                    <input type={"number"} value={this.state.value} onChange={this.handleChange} />
                    <br/>
                    <input type={"submit"} value={"Remove"} />
                </form>
            </div>
        );
    }
}

export default RemoveBowlingLane;
