const vocabsAPI = import.meta.env.VITE_BACKEND_API + "/user/my-vocabularies";

export function fetchUserVocabularies() {
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

    let initObject = {
        method: 'PATCH',
        headers: headers,
        body: JSON.stringify({name}),
    };

    let patchRequest = new Request(vocabsAPI + "/" + vocabId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function updatePicture(vocabId, pictureUrl, forceRemovePicture) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'PATCH',
        headers: headers,
        body: JSON.stringify({pictureUrl}),
    };

    let queryString = "?";
    if (forceRemovePicture) {
        queryString += "forceRemovePicture=true";
    }
    let patchRequest = new Request(vocabsAPI + "/" + vocabId + queryString, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function deleteVocabulary(vocabId) {
    let headers = new Headers();

    let initObject = {
        method: 'DELETE',
        headers: headers
    };

    let patchRequest = new Request(vocabsAPI + "/" + vocabId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}
