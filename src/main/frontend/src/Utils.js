
export function fetchRole(setRole) {

    fetch('/api/v1/user/role', {method: 'GET'})
        .then(response => response.json())
        .then(object => setRole(object.roleName))
}
