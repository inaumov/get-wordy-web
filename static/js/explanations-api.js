const vocabulariesAPI = import.meta.env.VITE_BACKEND_API + "/vocabularies";

export function fetchExplanation(vocabId, wordId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let getRequest = new Request(vocabulariesAPI + "/" + vocabId + "/explanations/" + wordId, initObject);
    return fetch(getRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function addExplanation(vocabId, cardData) {

    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(cardData),
    };
    let createRequest = new Request(vocabulariesAPI + "/" + vocabId + "/explanations", initObject);
    return fetch(createRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function editExplanation(vocabId, wordId, cardData) {

    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'PUT',
        headers: headers,
        body: JSON.stringify(cardData),
    };

    let updateRequest = new Request(vocabulariesAPI + "/" + vocabId + "/explanations", initObject);
    return fetch(updateRequest)
        .catch(err => console.log("HTTP error: ", err));
}
