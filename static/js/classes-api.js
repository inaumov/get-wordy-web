const classAPI = import.meta.env.VITE_BACKEND_API + "classes";

export function fetchClasses(dayOfWeek) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET',
        headers: headers,
    };

    let queryString = dayOfWeek ? "?filter=" + dayOfWeek : '';
    let classesRequest = new Request(classAPI + queryString, initObject);

    return fetch(classesRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function createClass(classInfo) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let request = classInfo;
    console.log('New class request = ', request);

    let initObject = {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(request),
    };
    let createRequest = new Request(classAPI, initObject);
    return fetch(createRequest)
        .catch(err => console.log("HTTP error: ", err));

}

export function fetchWordsheetList(classId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET',
        headers: headers,
    };

    let wordsheetRequest = new Request(classAPI + '/' + classId, initObject);

    return fetch(wordsheetRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function fetchWordSheet(classId, wordsheetId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let cardsRequest = new Request(classAPI + '/' + classId + '/wordsheet/' + wordsheetId, initObject);

    return fetch(cardsRequest)
        .catch(err => console.log("HTTP error: ", err));
}
