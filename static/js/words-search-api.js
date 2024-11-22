const searchAPI = import.meta.env.VITE_BACKEND_API + "words";

export function searchWordData(value) {

    console.log('Search word request, input =', value)

    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET',
        headers: headers,
    };

    let searchRequest = new Request(searchAPI + "?value=" + value, initObject);

    return fetch(searchRequest)
        .catch(err => console.log("HTTP error: ", err));
}
