const themesApi = import.meta.env.VITE_BACKEND_API + "/themes";

const jsonHeaders = {
    'Content-Type': 'application/json'
};

const handleError = (err) => {
    console.error("HTTP error:", err);
    throw err;
};

export function fetchThemes() {
    return fetch(themesApi)
        .catch(handleError);
}

export function fetchTheme(themeId) {
    return fetch(`${themesApi}/${themeId}`, {
        headers: jsonHeaders
    })
        .catch(handleError);
}

export function createTheme(themeName) {
    return fetch(themesApi, {
        method: 'POST',
        headers: jsonHeaders,
        body: JSON.stringify({name: themeName})
    })
        .catch(handleError);
}

export function generateTheme(themeId, candidatesLimit) {
    return fetch(`${themesApi}/${themeId}/generate-draft?candidatesLimit=${candidatesLimit}`, {
        method: 'POST',
        headers: jsonHeaders
    })
        .catch(handleError);
}

export function getDraft(themeId) {
    return fetch(`${themesApi}/${themeId}/candidate-words`)
        .catch(handleError);
}

export function removeCandidateWord(themeId, lemma, partOfSpeech) {
    return fetch(`${themesApi}/${themeId}/candidate-words`, {
        method: 'DELETE',
        headers: jsonHeaders,
        body: JSON.stringify({lemma, partOfSpeech})
    })
        .catch(handleError);
}

export function getWords(themeId) {
    return fetch(`${themesApi}/${themeId}/words`)
        .catch(handleError);
}

export function updateThemeName(themeId, name) {
    return fetch(`${themesApi}/${themeId}`, {
        method: 'PATCH',
        headers: jsonHeaders,
        body: JSON.stringify({name})
    })
        .catch(handleError);
}

export function confirmTheme(themeId) {
    return fetch(`${themesApi}/${themeId}/confirm`, {
        method: 'PUT',
        headers: jsonHeaders
    })
        .catch(handleError);
}

export function deleteTheme(themeId) {
    return fetch(`${themesApi}/${themeId}`, {
        method: 'DELETE'
    })
        .then(res => {
            if (!res.ok) throw new Error("Failed to delete theme");
            return res;
        })
        .catch(handleError);
}

export function addToTheme(themeId, wordId) {
    return fetch(`${themesApi}/${themeId}/words`, {
        method: 'POST',
        headers: jsonHeaders,
        body: JSON.stringify({wordId})
    })
        .catch(handleError);
}

export function removeFromTheme(themeId, wordId) {
    return fetch(`${themesApi}/${themeId}/words`, {
        method: 'DELETE',
        headers: jsonHeaders,
        body: JSON.stringify({wordId})
    })
        .catch(handleError);
}
