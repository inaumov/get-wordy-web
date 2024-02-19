const dictionariesAPI = "api/v1/dictionaries";

export function fetchDictionaries() {
    let reqHeader = new Headers();
    reqHeader.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: reqHeader,
    };

    let dictionariesRequest = new Request(dictionariesAPI, initObject);

    return fetch(dictionariesRequest)
        .catch(err => console.log("HTTP error: ", err));
}
