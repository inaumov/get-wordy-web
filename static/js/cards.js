const vocabsAPI = import.meta.env.VITE_BACKEND_API + "/user/my-vocabularies";

const jsonHeaders = {
    'Content-Type': 'application/json'
};

const handleError = (err) => {
    console.error("HTTP error:", err);
    throw err;
};

export function fetchCards(vocabId) {
    return fetch(`${vocabsAPI}/${vocabId}/cards`)
        .catch(handleError);
}

export function fetchCardsForExercise(vocabId, limit = 0) {
    const query = limit > 0 ? `?limit=${limit}` : "";
    return fetch(`${vocabsAPI}/${vocabId}/exercise${query}`)
        .catch(handleError);
}

export function submitResultForExercise(vocabId, cardIds) {
    return fetch(`${vocabsAPI}/${vocabId}/exercise`, {
        method: 'PUT',
        headers: jsonHeaders,
        body: JSON.stringify(cardIds)
    }).catch(handleError);
}

export function deleteCard(vocabId, cardId) {
    return fetch(`${vocabsAPI}/${vocabId}/cards`, {
        method: 'DELETE',
        headers: jsonHeaders,
        body: JSON.stringify({cardId})
    }).catch(handleError);
}

export function resetScore(vocabId, cardId) {
    return fetch(`${vocabsAPI}/${vocabId}/cards/${cardId}/resetScore`, {
        method: 'PUT'
    }).catch(handleError);
}

export function addToVocabulary(vocabId, wordId) {
    return fetch(`${vocabsAPI}/${vocabId}/cards`, {
        method: 'PUT',
        headers: jsonHeaders,
        body: JSON.stringify({wordId})
    }).catch(handleError);
}
