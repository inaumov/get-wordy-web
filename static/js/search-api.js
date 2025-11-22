const searchAPI = import.meta.env.VITE_BACKEND_API + "/words";

export function searchWordData(input) {

    console.log('Search request, input =', input)

    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET',
        headers: headers,
    };

    let searchRequest = new Request(searchAPI + "?input=" + input, initObject);

    return fetch(searchRequest)
        .catch(err => console.log("HTTP error: ", err));
}
