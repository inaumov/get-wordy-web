function getQueryVariable(paramName) {
    const searchParams = new URLSearchParams(window.location.search);
    if (!searchParams.has(paramName)) {
        alert('No query variable found: ' + paramName);
        return null;
    }
    return searchParams.get(paramName);
}