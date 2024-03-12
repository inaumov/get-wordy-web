const dictionariesAPI = import.meta.env.VITE_BACKEND_API + "dictionaries";

export function fetchDictionaries() {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET',
        headers: headers,
    };

    let dictionariesRequest = new Request(dictionariesAPI, initObject);

    return fetch(dictionariesRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function updateName(dictionaryId, name) {
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

    let patchRequest = new Request(dictionariesAPI + "/" + dictionaryId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function updatePicture(dictionaryId, picture) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let jsonRequest = {
        picture: picture
    }

    let initObject = {
        method: 'PATCH',
        headers: headers,
        body: JSON.stringify(jsonRequest),
    };

    let patchRequest = new Request(dictionariesAPI + "/" + dictionaryId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function deleteDictionary(dictionaryId) {
    let headers = new Headers();

    let initObject = {
        method: 'DELETE',
        headers: headers
    };

    let patchRequest = new Request(dictionariesAPI + "/" + dictionaryId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}
