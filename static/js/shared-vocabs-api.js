const myClassesAPI = `${import.meta.env.VITE_BACKEND_API}/user/my-classes`;

const jsonHeaders = new Headers({
    'Content-Type': 'application/json'
});

function apiGet(path) {
    return fetch(`${myClassesAPI}${path}`, {
        method: 'GET',
        headers: jsonHeaders
    }).catch(err => console.error("HTTP error:", err));
}

export const getSharedVocabularies = (classId) =>
    apiGet(`/${classId}/vocabularies`);

export const getSharedVocabulary = (classId, vocabularyId) =>
    apiGet(`/${classId}/vocabularies/${vocabularyId}`);

export const getSharedVocabularyCards = (classId, vocabularyId) =>
    apiGet(`/${classId}/vocabularies/${vocabularyId}/cards`);
