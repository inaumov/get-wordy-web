const myClassesAPI = import.meta.env.VITE_BACKEND_API + "/user/my-classes";

export function getSharedVocabularies(classId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET',
        headers: headers,
    };

    let getRequest = new Request(myClassesAPI + "/" + classId + "/vocabularies", initObject);

    return fetch(getRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function getSharedVocabulary(classId, vocabularyId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let getRequest = new Request(myClassesAPI + "/" + classId + "/vocabularies/" + vocabularyId, initObject);

    return fetch(getRequest)
        .catch(err => console.log("HTTP error: ", err));
}
