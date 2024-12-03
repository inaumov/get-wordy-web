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

export function deleteClass(classId) {
    let headers = new Headers();

    let initObject = {
        method: 'DELETE',
        headers: headers
    };

    let deleteRequest = new Request(classAPI + "/" + classId, initObject);

    return fetch(deleteRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function fetchWordsheetList(classId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET',
        headers: headers,
    };

    let wordsheetRequest = new Request(classAPI + '/' + classId + '/wordsheets', initObject);

    return fetch(wordsheetRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function fetchWordsheet(classId, wordsheetId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let wordsheetRequest = new Request(classAPI + '/' + classId + '/wordsheets/' + wordsheetId, initObject);

    return fetch(wordsheetRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function updateName(classId, wordsheetId, name) {
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

    let patchRequest = new Request(classAPI + "/" + classId + "/wordsheets/" + wordsheetId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function updateReadiness(classId, wordsheetId, isReady) {
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

    let patchRequest = new Request(classAPI + "/" + classId + "/wordsheets/" + wordsheetId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function addToWordsheet(classId, wordsheetId, wordData) {

    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(wordData),
    };
    let createRequest = new Request(classAPI + "/" + classId + "/wordsheets/" + wordsheetId + "/words", initObject);

    return fetch(createRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function deleteFromWordsheet(classId, wordsheetId, wordId) {
    let headers = new Headers();

    let initObject = {
        method: 'DELETE',
        headers: headers
    };

    let deleteRequest = new Request(classAPI + "/" + classId + "/wordsheets/" + wordsheetId+ "/words/" + wordId, initObject);

    return fetch(deleteRequest)
        .catch(err => console.log("HTTP error: ", err));
}
