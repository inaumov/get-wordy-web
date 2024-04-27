const dictionariesAPI = import.meta.env.VITE_BACKEND_API + "dictionaries";

export function fetchCards(dictionaryId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let cardsRequest = new Request(dictionariesAPI + "/" + dictionaryId + "/cards", initObject);

    return fetch(cardsRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function fetchCard(dictionaryId, cardId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let cardRequest = new Request(dictionariesAPI + "/" + dictionaryId + "/cards/" + cardId, initObject);

    return fetch(cardRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function generateCards(dictionaryId, wordsArr) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(wordsArr),
    };
    let generateRequest = new Request(dictionariesAPI + "/" + dictionaryId + "/generate", initObject);
    return fetch(generateRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function createCard(dictionaryId, cardData) {

    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let request = prepareCardJson(cardData);

    let initObject = {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(request),
    };
    let createRequest = new Request(dictionariesAPI + "/" + dictionaryId + "/cards", initObject);
    return fetch(createRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function updateCard(dictionaryId, cardId, wordId, cardData) {

    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let request = prepareCardJson(cardData);
    request['cardId'] = cardId;
    request['wordId'] = wordId;

    let initObject = {
        method: 'PUT',
        headers: headers,
        body: JSON.stringify(request),
    };

    let updateRequest = new Request(dictionariesAPI + "/" + dictionaryId + "/cards", initObject);
    return fetch(updateRequest)
        .catch(err => console.log("HTTP error: ", err));
}

function prepareCardJson(cardData) {
    let word = cardData.word;
    let partOfSpeech = cardData.partOfSpeech;
    let transcription = cardData.transcription;
    let meaning = cardData.meaning;
    let sentencesArr = cardData.sentences;
    let collocationsArr = cardData.collocations;

    let wordObj = {
        value: word,
        partOfSpeech: partOfSpeech,
        transcription: transcription,
        meaning: meaning
    };

    return {
        word: wordObj,
        sentences: sentencesArr,
        collocations: collocationsArr
    }
}

export function fetchCardsForExercise(dictionaryId, limit) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let cardsRequest = new Request(dictionariesAPI + "/" + dictionaryId + "/exercise?limit=" + limit, initObject);

    return fetch(cardsRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function submitResultForExercise(dictionaryId, cardIds) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'PUT',
        headers: headers,
        body: JSON.stringify(cardIds),
    };
    let generateRequest = new Request(dictionariesAPI + "/" + dictionaryId + "/exercise", initObject);
    return fetch(generateRequest)
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

export function deleteCard(dictionaryId, cardId) {
    let headers = new Headers();

    let initObject = {
        method: 'DELETE',
        headers: headers
    };

    let patchRequest = new Request(dictionariesAPI + "/" + dictionaryId + "/cards/" + cardId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function resetScore(dictionaryId, cardId) {
    let headers = new Headers();

    let initObject = {
        method: 'PUT',
        headers: headers
    };

    let patchRequest = new Request(dictionariesAPI + "/" + dictionaryId + "/cards/" + cardId + "/resetScore", initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}
