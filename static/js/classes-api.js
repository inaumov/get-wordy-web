const classAPI = import.meta.env.VITE_BACKEND_API + "classes";
const wordsheetAPI = import.meta.env.VITE_BACKEND_API + "wordsheets";

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

    // let wordsheetRequest = new Request(classAPI + '/' + classId + '/wordsheets', initObject);
    let wordsheetRequest = new Request(wordsheetAPI, initObject);

    return fetch(wordsheetRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function fetchWordsheet(classId, wordsheetId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    // let wordsheetRequest = new Request(classAPI + '/' + classId + '/wordsheets/' + wordsheetId, initObject);
    let wordsheetRequest = new Request(wordsheetAPI + "/" + wordsheetId, initObject);

    return fetch(wordsheetRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function updateName(classId, worksheetId, name) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let jsonRequest = {
        name: name
    }

    let initObject = {
        method: 'PATCH',
        headers: headers,
        body: JSON.stringify(jsonRequest),
    };

    let patchRequest = new Request(classAPI + "/" + classId + "/wordsheet/" + worksheetId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function updateReadiness(classId, worksheetId, isReady) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let jsonRequest = {
        isShared: isReady
    }

    let initObject = {
        method: 'PATCH',
        headers: headers,
        body: JSON.stringify(jsonRequest),
    };

    let patchRequest = new Request(classAPI + "/" + classId + "/wordsheet/" + worksheetId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}
