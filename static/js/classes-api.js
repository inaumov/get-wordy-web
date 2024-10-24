const classAPI = import.meta.env.VITE_BACKEND_API + "classes";

export function fetchClasses() {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET',
        headers: headers,
    };

    let dictionariesRequest = new Request(classAPI, initObject);

    return fetch(dictionariesRequest)
        .catch(err => console.log("HTTP error: ", err));
}
