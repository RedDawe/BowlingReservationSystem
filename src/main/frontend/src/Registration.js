import React from "react";
import { useNavigate } from "react-router-dom";

class RegistrationComponent extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            username: '',
            password: ''
        }

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(event) {
        fetch('/api/v1/user/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(this.state)

        })

        this.props.navigate('/user/login');
    }

    handleChange(event) {
        this.setState({[event.target.name]: event.target.value})
    }

    render() {
        return(
            <div>
                <form onSubmit={this.handleSubmit}>
                    <input type={"text"} value={this.state.username} onChange={this.handleChange} name={"username"} />
                    <input type={"password"} value={this.state.password} onChange={this.handleChange} name={"password"} />

                    <input type={"submit"} value={"Register"} />
                </form>
                <br/>
                <p>
                    <h3>Username requires:</h3>
                    1. 3 to 255 characters <br/>
                    2. Only consists of upper and lowercase letters, digits and .@

                    <h3>Password requires:</h3>
                    1. 8 to 50 characters <br/>
                    2. Only consists of upper and lowercase letters, digits and @$!%*#?& <br/>
                    3. At least one digit <br/>
                    4. At least one letter (can be either upper or lowercase) <br/>
                </p>
            </div>
        )
    }
}

function Registration(props) {
    const navigate = useNavigate();
    return <RegistrationComponent {...props} navigate={navigate} />
}

export default Registration;
