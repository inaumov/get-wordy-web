const classAPI = import.meta.env.VITE_BACKEND_API + "/classes";

const jsonHeaders = {
    'Content-Type': 'application/json'
};

const handleError = (err) => {
    console.error("HTTP error:", err);
    throw err;
};

export function fetchClasses(dayOfWeek) {
    const queryString = dayOfWeek ? `?filter=${encodeURIComponent(dayOfWeek)}` : '';
    return fetch(`${classAPI}${queryString}`)
        .catch(handleError);
}

export function createClass(classInfo) {
    return fetch(classAPI, {
        method: 'POST',
        headers: jsonHeaders,
        body: JSON.stringify(classInfo)
    }).catch(handleError);
}

export function updateClassInfo(classInfo) {
    return fetch(classAPI, {
        method: 'PUT',
        headers: jsonHeaders,
        body: JSON.stringify(classInfo)
    }).catch(handleError);
}

export function getClass(classId) {
    return fetch(`${classAPI}/${classId}`)
        .catch(handleError);
}

export function deleteClass(classId) {
    return fetch(`${classAPI}/${classId}`, {
        method: 'DELETE'
    }).catch(handleError);
}

export function classActivation(classId, isActive) {
    return fetch(`${classAPI}/${classId}`, {
        method: 'PATCH',
        headers: jsonHeaders,
        body: JSON.stringify({isActive})
    }).catch(handleError);
}

export function getVocabularies(classId) {
    return fetch(`${classAPI}/${classId}/vocabularies`)
        .catch(handleError);
}

export function getVocabulary(classId, vocabularyId) {
    return fetch(`${classAPI}/${classId}/vocabularies/${vocabularyId}`)
        .catch(handleError);
}

export function updateVocabularyName(classId, vocabularyId, name) {
    return fetch(`${classAPI}/${classId}/vocabularies/${vocabularyId}`, {
        method: 'PATCH',
        headers: jsonHeaders,
        body: JSON.stringify({name})
    }).catch(handleError);
}

export function publish(classId, vocabularyId, isReady) {
    return fetch(`${classAPI}/${classId}/vocabularies/${vocabularyId}`, {
        method: 'PATCH',
        headers: jsonHeaders,
        body: JSON.stringify({isShared: isReady})
    }).catch(handleError);
}

export function addToVocabulary(classId, vocabularyId, wordData) {
    return fetch(`${classAPI}/${classId}/vocabularies/${vocabularyId}`, {
        method: 'POST',
        headers: jsonHeaders,
        body: JSON.stringify(wordData)
    }).catch(handleError);
}

export function removeFromVocabulary(classId, vocabularyId, wordId) {
    return fetch(`${classAPI}/${classId}/vocabularies/${vocabularyId}`, {
        method: 'DELETE',
        headers: jsonHeaders,
        body: JSON.stringify({wordId})
    }).catch(handleError);
}
