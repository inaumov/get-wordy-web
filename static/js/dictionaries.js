const vocabsAPI = import.meta.env.VITE_BACKEND_API + "user/vocabularies";

export function fetchDictionaries() {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET',
        headers: headers,
    };

    let fetchAllRequest = new Request(vocabsAPI, initObject);

    return fetch(fetchAllRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function updateName(vocabId, name) {
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

    let patchRequest = new Request(vocabsAPI + "/" + vocabId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function updatePicture(vocabId, picture, forceRemovePicture) {
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

    let queryString = "?";
    if (forceRemovePicture) {
        queryString += "forceRemovePicture=true";
    }
    let patchRequest = new Request(vocabsAPI + "/" + vocabId + queryString, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function deleteDictionary(vocabId) {
    let headers = new Headers();

    let initObject = {
        method: 'DELETE',
        headers: headers
    };

    let patchRequest = new Request(vocabsAPI + "/" + vocabId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}
