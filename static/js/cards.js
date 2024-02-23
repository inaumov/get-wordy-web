const cardsAPI = "api/v1/dictionaries";

export function fetchCards(dictionaryId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let cardsRequest = new Request(cardsAPI + "/" + dictionaryId + "/cards", initObject);

    return fetch(cardsRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function fetchCard(dictionaryId, cardId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let cardRequest = new Request(cardsAPI + "/" + dictionaryId + "/cards/" + cardId, initObject);

    return fetch(cardRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function generateCards(dictionaryId, textData) {
    let words = textData.words;
    const valuesArr = !words ? [] : words.split('\n');

    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(valuesArr),
    };
    let generateRequest = new Request(cardsAPI + "/" + dictionaryId + "/generate", initObject);
    return fetch(generateRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function createCard(dictionaryId, cardData) {
    let word = cardData.word;
    let partOfSpeech = cardData.partOfSpeech;
    let transcription = cardData.transcription;
    let meaning = cardData.meaning;
    let contexts = cardData.contexts;
    let collocations = cardData.collocations;

    const contextArr = !contexts ? [] : contexts.split('\n');
    const collocationArr = !collocations ? [] : collocations.split('\n');

    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let wordObj = {
        value: word,
        partOfSpeech: partOfSpeech,
        transcription: transcription,
        meaning: meaning
    };

    let cardRequest = {
        word: wordObj,
        contexts: contextArr,
        collocations: collocationArr
    };

    let initObject = {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(cardRequest),
    };

    let generateRequest = new Request(cardsAPI + "/" + dictionaryId + "/cards", initObject);
    return fetch(generateRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function fetchCardsForExercise(dictionaryId, limit) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let cardsRequest = new Request(cardsAPI + "/" + dictionaryId + "/exercise?limit=" + limit, initObject);

    return fetch(cardsRequest)
        .catch(err => console.log("HTTP error: ", err));
}
