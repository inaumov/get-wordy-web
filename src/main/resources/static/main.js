const dictionariesAPI = "api/v1/dictionaries";

addEventListener('load', dictionaries, false);

function dictionaries() {
    let reqHeader = new Headers();
    reqHeader.append('Content-Type', 'text/json');

    let initObject = {
        method: 'GET', headers: reqHeader,
    };

    let dictionariesRequest = new Request(dictionariesAPI, initObject);

    fetch(dictionariesRequest)
        .then(response => response.json())
        .then(data => displayDictionaries(data))
        .catch(err => console.log("HTTP error: ", err));
}

function displayDictionaries(responseJson) {
    // clear the previous content
    let content = document.getElementById("content");
    content.innerHTML = "";
    let items = responseJson; // array is expected here
    for (let i = 0; i < items.length; i++) {
        let dictionaryId = items[i].dictionaryId;
        let caption = items[i].name;
        let cardsTotal = items[i].cardsTotal;
        let logo = items[i].picture;
        if (typeof cardsTotal !== "number") {
            cardsTotal = Math.floor(Math.random() * (125 - 25) + 25);
        }
        let cardElement = document.createElement("div");
        cardElement.id = 'dictionary';
        cardElement.className = 'card text-center';
        cardElement.width = '18rem';

        appendLogo(cardElement, logo);
        appendCaption(cardElement, dictionaryId, caption, cardsTotal);
        content.appendChild(cardElement);
    }
}

function appendLogo(card, link) {
    let img = document.createElement("img");
    img.className = 'card-img-top mx-auto d-block';
    img.src = link;
    img.alt = "...";
    card.appendChild(img);
}

function appendCaption(card, dictionaryId, caption, cardsTotal) {
    let cardBody = document.createElement("div");
    cardBody.className = 'card-body';

    let header = document.createElement("h5");
    let t = document.createTextNode(caption);
    header.className = 'card-title';
    header.appendChild(t);

    let a = document.createElement("a");
    a.className = 'btn btn-primary';
    a.target = '_blank';
    a.setAttribute('href', '/GetWordyApp/cards.html?dictionaryId=' + encodeURIComponent(dictionaryId) + '&dictionaryName=' + encodeURIComponent(caption));
    a.innerHTML = cardsTotal;

    cardBody.appendChild(header);
    cardBody.appendChild(a);

    card.appendChild(cardBody);
}
