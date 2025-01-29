const vocabsAPI = import.meta.env.VITE_BACKEND_API + "user/vocabularies";

export function fetchCards(vocabId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let cardsRequest = new Request(vocabsAPI + "/" + vocabId + "/cards", initObject);

    return fetch(cardsRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function fetchCardsForExercise(vocabId, limit) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let cardsRequest = new Request(vocabsAPI + "/" + vocabId + "/exercise?limit=" + limit, initObject);

    return fetch(cardsRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function submitResultForExercise(vocabId, cardIds) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'PUT',
        headers: headers,
        body: JSON.stringify(cardIds),
    };
    let putRequest = new Request(vocabsAPI + "/" + vocabId + "/exercise", initObject);
    return fetch(putRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function toReadableStatus(status) {
    if (status === 'TO_LEARN') {
        return 'To learn'
    } else if (status === 'LEARNT') {
        return 'Done'
    } else if (status === 'POSTPONED') {
        return 'Postponed'
    } else if (status === 'EDIT') {
        return 'Edit'
    } else {
        return status
    }
}

export function deleteCard(vocabId, cardId) {
    let headers = new Headers();

    let initObject = {
        method: 'DELETE',
        headers: headers
    };

    let patchRequest = new Request(vocabsAPI + "/" + vocabId + "/cards/" + cardId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function resetScore(vocabId, cardId) {
    let headers = new Headers();

    let initObject = {
        method: 'PUT',
        headers: headers
    };

    let patchRequest = new Request(vocabsAPI + "/" + vocabId + "/cards/" + cardId + "/resetScore", initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}
