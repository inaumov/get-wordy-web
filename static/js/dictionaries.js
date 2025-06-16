const vocabsAPI = import.meta.env.VITE_BACKEND_API + "/user/my-vocabularies";

const jsonHeaders = {
    'Content-Type': 'application/json'
};

const handleError = (err) => {
    console.error("HTTP error:", err);
    throw err;
};

export function fetchUserVocabularies() {
    return fetch(vocabsAPI, {
        method: 'GET',
        headers: jsonHeaders
    }).catch(handleError);
}

export function createVocabulary(name) {
    return fetch(vocabsAPI, {
        method: 'POST',
        headers: jsonHeaders,
        body: JSON.stringify({name})
    }).catch(handleError);
}

export function updateName(vocabId, name) {
    return fetch(`${vocabsAPI}/${vocabId}`, {
        method: 'PATCH',
        headers: jsonHeaders,
        body: JSON.stringify({name})
    }).catch(handleError);
}

export function updatePicture(vocabId, pictureUrl, forceRemovePicture = false) {
    const query = forceRemovePicture ? "?forceRemovePicture=true" : "";
    return fetch(`${vocabsAPI}/${vocabId}${query}`, {
        method: 'PATCH',
        headers: jsonHeaders,
        body: JSON.stringify({pictureUrl})
    }).catch(handleError);
}

export function deleteVocabulary(vocabId) {
    return fetch(`${vocabsAPI}/${vocabId}`, {
        method: 'DELETE',
        headers: jsonHeaders
    }).catch(handleError);
}
