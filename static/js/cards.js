const vocabsAPI = import.meta.env.VITE_BACKEND_API + "/user/my-vocabularies";

const jsonHeaders = {
    'Content-Type': 'application/json'
};

export function fetchCards(vocabId) {
    return fetch(`${vocabsAPI}/${vocabId}/cards`)
        .catch(err => console.error("HTTP error:", err));
}

export function fetchCardsForExercise(vocabId, limit = 0) {
    const query = limit > 0 ? `?limit=${limit}` : "";
    return fetch(`${vocabsAPI}/${vocabId}/exercise${query}`)
        .catch(err => console.error("HTTP error:", err));
}

export function submitResultForExercise(vocabId, cardIds) {
    return fetch(`${vocabsAPI}/${vocabId}/exercise`, {
        method: 'PUT',
        headers: jsonHeaders,
        body: JSON.stringify(cardIds)
    }).catch(err => console.error("HTTP error:", err));
}

export function deleteCard(vocabId, cardId) {
    return fetch(`${vocabsAPI}/${vocabId}/cards/${cardId}`, {
        method: 'DELETE'
    }).catch(err => console.error("HTTP error:", err));
}

export function resetScore(vocabId, cardId) {
    return fetch(`${vocabsAPI}/${vocabId}/cards/${cardId}/resetScore`, {
        method: 'PUT'
    }).catch(err => console.error("HTTP error:", err));
}
