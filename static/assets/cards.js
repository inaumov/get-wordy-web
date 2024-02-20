const cardsAPI = "api/v1/dictionaries";

export function fetchCards(dictionaryId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let cardsRequest = new Request(cardsAPI + "/" + dictionaryId + "/cards", initObject);

    return fetch(cardsRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function fetchCard(dictionaryId, cardId) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let cardRequest = new Request(cardsAPI + "/" + dictionaryId + "/cards/" + cardId, initObject);

    return fetch(cardRequest)
        .catch(err => console.log("HTTP error: ", err));
}

function showCard(cardElement) {
    let details = document.getElementById('all-cards-panel');
    details.innerHTML = "";

    let w = cardElement.word;
    let word = w.value;
    let partOfSpeech = w.partOfSpeech;
    let transcription = w.transcription;
    let meaning = w.meaning;
    let status = cardElement.status;
    let score = cardElement.score;
    let contexts = cardElement.contexts;
    let collocations = cardElement.collocations;

    console.log(cardElement);
}

export function generateCards(dictionaryId, textData) {
    let words = textData.words;
    const valuesArr = !words ? [] : words.split('\n');

    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(valuesArr),
    };
    let generateRequest = new Request(cardsAPI + "/" + dictionaryId + "/generate", initObject);
    return fetch(generateRequest)
        .catch(err => console.log("HTTP error: ", err));
}

export function createCard(dictionaryId, cardData) {
    let word = cardData.word;
    let partOfSpeech = cardData.partOfSpeech;
    let transcription = cardData.transcription;
    let meaning = cardData.meaning;
    let contexts = cardData.contexts;
    let collocations = cardData.collocations;

    const contextArr = !contexts ? [] : contexts.split('\n');
    const collocationArr = !collocations ? [] : collocations.split('\n');

    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let wordObj = {
        value: word,
        partOfSpeech: partOfSpeech,
        transcription: transcription,
        meaning: meaning
    };

    let cardRequest = {
        word: wordObj,
        contexts: contextArr,
        collocations: collocationArr
    };

    let initObject = {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(cardRequest),
    };

    let generateRequest = new Request(cardsAPI + "/" + dictionaryId + "/cards", initObject);
    return fetch(generateRequest)
        .catch(err => console.log("HTTP error: ", err));
}

function printCards(cards) {
    let playGamePanel = document.getElementById('play-game-panel');
    playGamePanel.style.display = "";
    playGamePanel.innerHTML = "";
    let btnGrpDiv = document.createElement("div");
    btnGrpDiv.className = "col-sm-12 text-center p-5"
    btnGrpDiv.setAttribute("role", "group");
    btnGrpDiv.setAttribute("aria-label", "Exercise");
    for (let i = 0; i < cards.length; i++) {
        let btn = document.createElement("button");
        btn.type = "button";
        btn.className = "btn btn-outline-info";
        btn.innerHTML = cards[i].word.value;
        btn.style.marginLeft = "2px";
        btn.style.marginRight = "2px";
        btnGrpDiv.appendChild(btn);
    }
    playGamePanel.appendChild(btnGrpDiv);
}

export function rollDice(dictionaryId) {
    const min = 1;
    const max = 6;
    const die1 = Math.floor(Math.random() * (max - min + 1)) + min;
    const die2 = Math.floor(Math.random() * (max - min + 1)) + min;

    let playGamePanel = document.getElementById('play-game-panel');
    playGamePanel.style.display = "";
    playGamePanel.innerHTML = "";

    let diceGrpDiv = document.createElement("div");
    diceGrpDiv.className = "col-sm-12 text-center p-5"
    let i1 = document.createElement("i");
    i1.className = "bi bi-dice-" + die1 + " h2";
    i1.style.marginRight = "3px";
    let i2 = document.createElement("i");
    i2.className = "bi bi-dice-" + die2 + " h2";
    i2.style.marginLeft = "3px";
    diceGrpDiv.appendChild(i1);
    diceGrpDiv.appendChild(i2);
    playGamePanel.appendChild(diceGrpDiv);

    let youRolled = document.createElement("div");
    youRolled.className = "col-sm-12 text-center"
    youRolled.innerHTML = "You rolled " + (die1 + die2);
    playGamePanel.appendChild(youRolled);

    let playAgain = document.createElement("div");
    playAgain.className = "col-sm-12 text-center p-5"
    let btn = document.createElement("button");
    playAgain.appendChild(btn);
    btn.type = "button";
    btn.className = "btn btn-outline-info";
    btn.innerHTML = "Print cards";
    btn.onclick = function () {
        let limit = die1 + die2;
        const response = fetchCardsForExercise(dictionaryId, limit);
        printCards(response.json());
    }
    playGamePanel.appendChild(playAgain);
}

export function fetchCardsForExercise(dictionaryId, limit) {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: headers,
    };

    let cardsRequest = new Request(cardsAPI + "/" + dictionaryId + "/exercise?limit=" + limit, initObject);

    return fetch(cardsRequest)
        .catch(err => console.log("HTTP error: ", err));
}
