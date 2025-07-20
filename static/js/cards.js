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

export function saveProgress(vocabId, wordIds) {
    return fetch(`${vocabsAPI}/${vocabId}/saveProgress`, {
        method: 'PUT',
        headers: jsonHeaders,
        body: JSON.stringify(wordIds)
    }).catch(handleError);
}

export function deleteCard(vocabId, wordId) {
    return fetch(`${vocabsAPI}/${vocabId}/words`, {
        method: 'DELETE',
        headers: jsonHeaders,
        body: JSON.stringify({wordId})
    }).catch(handleError);
}

export function resetProgress(vocabId, wordId) {
    return fetch(`${vocabsAPI}/${vocabId}/cards/${wordId}/resetProgress`, {
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
