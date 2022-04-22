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
    }

    handleChange(event) {
        this.setState({[event.target.name]: event.target.value})
    }

    render() {
        return(
            <div>
                <form action={'http://localhost:8080/login'} method={'POST'}>
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
