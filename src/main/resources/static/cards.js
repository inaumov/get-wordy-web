const cardsAPI = "api/v1/dictionaries";

addEventListener('load', loadCards, false);

function loadCards() {
    let caption = getQueryVariable('dictionaryName');
    let dictionaryId = getQueryVariable('dictionaryId');
    if (!caption) {
        caption = "Cards";
    }
    document.getElementById('caption').textContent = caption;

    let reqHeader = new Headers();
    reqHeader.append('Content-Type', 'text/json');

    let initObject = {
        method: 'GET', headers: reqHeader,
    };

    let cardsRequest = new Request(cardsAPI + "/" + dictionaryId + "/cards", initObject);

    fetch(cardsRequest)
        .then(response => response.json())
        .then(data => displayCards(data))
        .catch(err => console.log("HTTP error: ", err));
}

function displayCards(responseJson) {
    // hide other tabs
    hidePanel('play-game-panel');
    hidePanel('play-game-panel-inner');
    hidePanel('generate-cards-panel');
    hidePanel('add-card-panel');
    hidePanel('custom-alert-panel');

    // clear the previous content
    let content = document.getElementById('all-cards-panel');
    content.innerHTML = "";
    // show table div
    showPanel('all-cards-panel');
    let items = responseJson; // array is expected
    // create a table of cards
    let table = document.createElement('table');
    table.className = 'table';
    let header = document.createElement('thead');
    let body = document.createElement('tbody');
    let headerRow = document.createElement('tr');
    header.appendChild(headerRow);
    const headers = ["Word", "Transcription", "Meaning", "Status", "Score", "Context", "Collocations"];
    appendHeaderRow(headerRow, headers);
    table.appendChild(header);
    table.appendChild(body);

    for (let i = 0; i < items.length; i++) {
        let tableRow = document.createElement("tr");
        appendRow(tableRow, items[i]);
        body.appendChild(tableRow);
    }
    content.appendChild(table);
}

function appendHeaderRow(headerRow, items) {
    for (let i = 0; i < items.length; i++) {
        let columnName = document.createElement('th');
        columnName.appendChild(document.createTextNode(items[i]));
        headerRow.appendChild(columnName);
    }
}

function appendRow(tableRow, cardElement) {
    let wordElement = cardElement.word;
    let word = wordElement.value;
    let partOfSpeech = wordElement.partOfSpeech;
    let transcription = wordElement.transcription;
    let meaning = wordElement.meaning;
    let status = cardElement.status;
    let score = cardElement.score;
    let contexts = cardElement.contexts; // arr is expected
    let collocations = cardElement.collocations; // arr is expected

    tableRow.addEventListener("click", function () {
        loadDetails(cardElement);
    }, false);
    appendTextCell(tableRow, word + " (" + partOfSpeech + ")");
    appendTextCell(tableRow, fixNullValue(transcription));
    appendTextCell(tableRow, meaning);
    appendTextCell(tableRow, status);
    appendTextCell(tableRow, score);
    appendListCell(tableRow, contexts);
    appendListCell(tableRow, collocations);
}

function appendTextCell(row, textValue) {
    let cell = document.createElement("td");
    cell.appendChild(document.createTextNode(textValue));
    row.appendChild(cell);
}

function appendListCell(row, items) {
    let cell = document.createElement("td");
    if (!items) {
        row.appendChild(cell);
        return;
    }
    let ul = document.createElement("ul");
    ul.className = 'list-unstyled';
    for (let i = 0; i < items.length; i++) {
        appendListElement(ul, items[i])
    }
    cell.appendChild(ul);
    row.appendChild(cell);
}

function appendListElement(list, element) {
    let e = document.createElement("li");
    let textNode = document.createTextNode(element);
    e.appendChild(textNode);
    list.appendChild(e);
}

function fixNullValue(textValue) {
    if (!textValue) {
        return "";
    }
    return textValue;
}

function loadDetails(card) {

    let reqHeader = new Headers();
    reqHeader.append('Content-Type', 'text/json');

    let initObject = {
        method: 'GET', headers: reqHeader,
    };

    let cardRequest = new Request(cardsAPI + "/" + dictionaryId + "/cards/" + card.id, initObject);

    fetch(cardRequest)
        .then(response => response.json())
        .then(data => displayCards(data))
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
}

function play() {
    // hide other tabs
    hidePanel('all-cards-panel');
    hidePanel('generate-cards-panel');
    hidePanel('add-card-panel');
    hidePanel('custom-alert-panel');
    hidePanel('play-game-panel-inner');

    // show form
    showPanel('play-game-panel');
}

function generate() {
    // hide other tabs
    hidePanel('all-cards-panel');
    hidePanel('play-game-panel');
    hidePanel('play-game-panel-inner');
    hidePanel('add-card-panel');
    hidePanel('custom-alert-panel');
    // show form
    showPanel('generate-cards-panel');
}

function addCard() {
    // clear the previous content
    hidePanel('all-cards-panel');
    hidePanel('play-game-panel');
    hidePanel('play-game-panel-inner');

    hidePanel('generate-cards-panel');
    hidePanel('custom-alert-panel');
    // show form
    showPanel("add-card-panel");

    let radioGrp = document.getElementById('radioGrp');
    radioGrp.innerHTML = "";

    const partOfSpeechArr = ["noun", "pronoun", "verb", "adjective", "adverb", "phrasal verb"];
    for (let i = 0; i < partOfSpeechArr.length; i++) {
        let div = document.createElement("div");
        div.className = "form-check";
        let partOfSpeech = partOfSpeechArr[i];
        let inputId = "radio" + i;
        let input = document.createElement("input");
        input.type = "radio";
        input.className = "form-check-input";
        input.id = inputId;
        input.name = "pos_radios";
        input.value = partOfSpeech;
        let label = document.createElement("label");
        label.innerHTML = partOfSpeech;
        label.className = "form-check-label";
        label.setAttribute("for", inputId)
        div.appendChild(input);
        div.appendChild(label);
        radioGrp.appendChild(div);
    }

}

function showPanel(panelId) {
    let content = document.getElementById(panelId);
    content.style.display = "";
}

function hidePanel(panelId) {
    let div = document.getElementById(panelId);
    div.style.display = "none";
}

function customAlert(flag) {
    if (flag) {
        showPanel('custom-alert-panel');
    } else {
        alert("Word not added");
    }
}

function postWordsForm() {
    let form = document.getElementById('generate-cards-form');
    let formData = new FormData(form);
    const valuesArr = formData.get('words').split('\n');

    let reqHeader = new Headers();
    reqHeader.append('Content-Type', 'application/json');

    try {
        let initObject = {
            method: 'POST',
            headers: reqHeader,
            body: JSON.stringify(valuesArr),
        };
        let dictionaryId = getQueryVariable('dictionaryId');
        let generateRequest = new Request(cardsAPI + "/" + dictionaryId + "/generate", initObject);
        fetch(generateRequest)
            .then(response => response.json())
            .then(data => displayCards(data))
            .catch(err => console.log("HTTP error: ", err));

    } catch (err) {
        console.log(err.message);
    }
}

function postCardForm() {
    let dictionaryId = getQueryVariable('dictionaryId');
    let form = document.getElementById('add-card-form');
    let formData = new FormData(form);
    const word = formData.get('word');
    const meaning = formData.get('meaning');
    const transcription = formData.get('transcription');
    const pos = formData.get('pos_radios');
    const contextArr = formData.get('context').split('\n');
    const collocationArr = formData.get('collocations').split('\n');

    let reqHeader = new Headers();
    reqHeader.append('Content-Type', 'application/json');

    let wordRequest = {
        value: word,
        partOfSpeech: pos,
        transcription: transcription,
        meaning: meaning
    };

    let cardRequest = {
        word: wordRequest,
        contexts: contextArr,
        collocations: collocationArr
    };

    try {
        let initObject = {
            method: 'POST',
            headers: reqHeader,
            body: JSON.stringify(cardRequest),
        };

        let generateRequest = new Request(cardsAPI + "/" + dictionaryId + "/cards", initObject);
        fetch(generateRequest)
            .then(response => response.json())
            .then(success => customAlert(success))
            .catch(err => console.log("HTTP error: ", err));

    } catch (err) {
        console.log(err.message);
    }
}

function printFiboNumbers() {
    hidePanel('play-game-panel');

    const fiboNumbers = [2, 3, 5, 8, 13, 21];
    let playGamePanel = document.getElementById('play-game-panel-inner');
    playGamePanel.style.display = "";
    playGamePanel.innerHTML = "";
    let btnGrpDiv = document.createElement("div");
    btnGrpDiv.className = "col-sm-12 text-center p-5"
    btnGrpDiv.setAttribute("role", "group");
    btnGrpDiv.setAttribute("aria-label", "Fibonacci Buttons");
    for (let i = 0; i < fiboNumbers.length; i++) {
        let btn = document.createElement("button");
        btn.type = "button";
        btn.className = "btn btn-outline-info";
        btn.innerHTML = fiboNumbers[i].toString();
        btn.style.marginLeft = "2px";
        btn.style.marginRight = "2px";
        btn.onclick = function () {
            alert('Test Fibonacci Buttons: ' + fiboNumbers[i].toString());
        }
        btnGrpDiv.appendChild(btn);
    }
    playGamePanel.appendChild(btnGrpDiv);
}

function rollDice() {
    hidePanel('play-game-panel');

    const min = 1;
    const max = 6;
    const die1 = Math.floor(Math.random() * (max - min + 1)) + min;
    const die2 = Math.floor(Math.random() * (max - min + 1)) + min;

    let playGamePanel = document.getElementById('play-game-panel-inner');
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
    btn.innerHTML = "Play again";
    btn.onclick = function () {
        rollDice();
    }
    playGamePanel.appendChild(playAgain);
}
