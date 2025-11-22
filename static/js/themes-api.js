const templatesApi = import.meta.env.VITE_BACKEND_API + "/themes";

const jsonHeaders = {
    'Content-Type': 'application/json'
};

const handleError = (err) => {
    console.error("HTTP error:", err);
    throw err;
};

export function fetchThemes() {
    return fetch(templatesApi)
        .catch(handleError);
}

export function fetchTheme(themeId) {
    return fetch(`${templatesApi}/${themeId}`, {
        headers: jsonHeaders
    })
        .catch(handleError);
}

export function createTheme(name) {
    return fetch(templatesApi, {
        method: 'POST',
        headers: jsonHeaders,
        body: JSON.stringify({name})
    })
        .catch(handleError);
}

export function updateThemeName(themeId, name) {
    return fetch(`${templatesApi}/${themeId}`, {
        method: 'PATCH',
        headers: jsonHeaders,
        body: JSON.stringify({name})
    })
        .catch(handleError);
}

export function saveTheme(theme) {
    return fetch(templatesApi, {
        method: 'PUT',
        headers: jsonHeaders,
        body: JSON.stringify(theme)
    })
        .catch(handleError);
}

export function deleteTheme(themeId) {
    return fetch(`${templatesApi}/${themeId}`, {
        method: 'DELETE'
    })
        .then(res => {
            if (!res.ok) throw new Error("Failed to delete theme");
            return res;
        })
        .catch(handleError);
}
