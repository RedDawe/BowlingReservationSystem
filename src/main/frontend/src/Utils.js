
export function fetchRole(setRole) {

    fetch('http://localhost:8080/api/v1/user/role', {method: 'GET'})
        .then(response => response.json())
        .then(object => setRole(object.roleName))
}
