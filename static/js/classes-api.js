const classAPI = import.meta.env.VITE_BACKEND_API + "classes";
const templatesAPI = import.meta.env.VITE_BACKEND_API + "templates";

export function fetchClasses(dayOfWeek) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET',
        headers: headers,
    };

    let queryString = dayOfWeek ? "?filter=" + dayOfWeek : '';
    let classesRequest = new Request(classAPI + queryString, initObject);

    return fetch(classesRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function createClass(classInfo) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let request = classInfo;
    console.log('New class request = ', request);

    let initObject = {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(request),
    };
    let createRequest = new Request(classAPI, initObject);
    return fetch(createRequest)
        .catch(err => console.log("HTTP error: ", err));

}

export function updateClassInfo(classInfo) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let request = classInfo;
    console.log('Update class info request = ', request);

    let initObject = {
        method: 'PUT',
        headers: headers,
        body: JSON.stringify(request),
    };
    let createRequest = new Request(classAPI, initObject);
    return fetch(createRequest)
        .catch(err => console.log("HTTP error: ", err));

}

export function getClass(classId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET',
        headers: headers,
    };

    let classByIdRequest = new Request(classAPI + "/" + classId, initObject);

    return fetch(classByIdRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function deleteClass(classId) {
    let headers = new Headers();

    let initObject = {
        method: 'DELETE',
        headers: headers
    };

    let deleteRequest = new Request(classAPI + "/" + classId, initObject);

    return fetch(deleteRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function getVocabularies(classId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET',
        headers: headers,
    };

    let getRequest = new Request(classAPI + '/' + classId + '/vocabularies', initObject);

    return fetch(getRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function getVocabulary(classId, vocabularyId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let getRequest = new Request(classAPI + '/' + classId + '/vocabularies/' + vocabularyId, initObject);

    return fetch(getRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function updateVocabularyName(classId, vocabularyId, name) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let jsonRequest = {
        name: name
    }

    let initObject = {
        method: 'PATCH',
        headers: headers,
        body: JSON.stringify(jsonRequest),
    };

    let patchRequest = new Request(classAPI + "/" + classId + "/vocabularies/" + vocabularyId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function publish(classId, vocabularyId, isReady) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let jsonRequest = {
        isShared: isReady
    }

    let initObject = {
        method: 'PATCH',
        headers: headers,
        body: JSON.stringify(jsonRequest),
    };

    let patchRequest = new Request(classAPI + "/" + classId + "/vocabularies/" + vocabularyId, initObject);

    return fetch(patchRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function addToVocabulary(classId, vocabularyId, wordData) {

    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(wordData),
    };
    let createRequest = new Request(classAPI + "/" + classId + "/vocabularies/" + vocabularyId, initObject);

    return fetch(createRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function removeFromVocabulary(classId, vocabularyId, wordId) {
    let headers = new Headers();

    let initObject = {
        method: 'DELETE',
        headers: headers,
        body: JSON.stringify({
            wordId: wordId
        }),
    };

    let deleteRequest = new Request(classAPI + "/" + classId + "/vocabularies/" + vocabularyId, initObject);

    return fetch(deleteRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function fetchTemplates() {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET',
        headers: headers,
    };

    let dictionariesRequest = new Request(templatesAPI, initObject);

    return fetch(dictionariesRequest)
        .catch(err => console.log("HTTP error: ", err));
}
