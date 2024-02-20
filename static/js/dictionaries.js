const dictionariesAPI = "api/v1/dictionaries";

export function fetchDictionaries() {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let dictionariesRequest = new Request(dictionariesAPI, initObject);

    return fetch(dictionariesRequest)
        .catch(err => console.log("HTTP error: ", err));
}
