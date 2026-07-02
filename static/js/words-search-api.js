const apiBase = import.meta.env.VITE_BACKEND_API;

const jsonHeaders = {
    "Content-Type": "application/json"
};

const handleError = (err) => {
    console.error("HTTP error:", err);
    throw err;
};

/**
 * Generic helper for GET requests
 */
function getRequest(url) {
    return fetch(url, {method: "GET", headers: jsonHeaders})
        .catch(handleError);
}

/**
 * Search words explanations
 * GET /words?input=car
 */
export function search(input) {
    const url = `${apiBase}/words?input=${encodeURIComponent(input)}`;
    return getRequest(url);
}
