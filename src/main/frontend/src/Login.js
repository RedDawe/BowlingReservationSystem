import React from "react";
import { useNavigate } from "react-router-dom";

class LoginComponent extends React.Component {
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
        fetch('http://localhost:8080/api/v1/user/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(this.state)

        })

        this.props.navigate('/');
    }

    handleChange(event) {
        this.setState({[event.target.name]: event.target.value})
    }

    render() {
        return(
            <div>
                <form onSubmit={this.handleSubmit}>
                    <input type={"text"} value={this.state.username} onChange={this.handleChange} name={"username"} />
                    <input type={"text"} value={this.state.password} onChange={this.handleChange} name={"password"} />

                    <input type={"submit"} value={"Login"} />
                </form>
            </div>
        )
    }
}

function Login(props) {
    const navigate = useNavigate();
    return <LoginComponent {...props} navigate={navigate} />
}

export default Login;
