const templatesAPI = import.meta.env.VITE_BACKEND_API + "/templates";

const jsonHeaders = {
    'Content-Type': 'application/json'
};

const handleError = (err) => {
    console.error("HTTP error:", err);
    throw err;
};

export function fetchTemplates() {
    return fetch(templatesAPI)
        .catch(handleError);
}

export function fetchTemplate(templateId) {
    return fetch(`${templatesAPI}/${templateId}`, {
        headers: jsonHeaders
    }).catch(handleError);
}

export function updateTemplateName(templateId, name) {
    return fetch(`${templatesAPI}/${templateId}`, {
        method: 'PATCH',
        headers: jsonHeaders,
        body: JSON.stringify({name})
    }).catch(handleError);
}
