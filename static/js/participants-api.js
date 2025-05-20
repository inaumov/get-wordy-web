const classAPI = import.meta.env.VITE_BACKEND_API + "/classes";

export function assignUser(classId, userIdentity) {
    let headers = new Headers();
    headers.append("Content-Type", "application/json");

    let requestBody = {
        userIdentity: userIdentity
    };
    console.log("Assign user request = ", requestBody);

    let initObject = {
        method: "POST",
        headers: headers,
        body: JSON.stringify(requestBody),
    };

    let request = new Request(`${classAPI}/${classId}/assign`, initObject);
    return fetch(request)
        .catch((err) => console.log("HTTP error: ", err));
}

export function revokeUser(classId, userIdentity) {
    let headers = new Headers();
    headers.append("Content-Type", "application/json");

    let requestBody = {
        userIdentity: userIdentity
    };
    console.log("Revoke user request = ", requestBody);

    let initObject = {
        method: "DELETE",
        headers: headers,
        body: JSON.stringify(requestBody),
    };

    let request = new Request(`${classAPI}/${classId}/revoke`, initObject);
    return fetch(request)
        .catch((err) => console.log("HTTP error: ", err));
}
