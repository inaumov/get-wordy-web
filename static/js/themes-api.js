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

export function updateThemeName(themeId, name) {
    return fetch(`${themesApi}/${themeId}`, {
        method: 'PATCH',
        headers: jsonHeaders,
        body: JSON.stringify({name})
    })
        .catch(handleError);
}

export function createTheme(theme) {
    return fetch(themesApi, {
        method: 'PUT',
        headers: jsonHeaders,
        body: JSON.stringify(theme)
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
    return fetch(`${themesApi}/${themeId}`, {
        method: 'PUT',
        headers: jsonHeaders,
        body: JSON.stringify({wordId})
    })
        .catch(handleError);
}

export function removeFromTheme(themeId, wordId) {
    return fetch(`${themesApi}/${themeId}/words/${wordId}`, {
        method: 'DELETE',
        headers: jsonHeaders
    })
        .catch(handleError);
}
