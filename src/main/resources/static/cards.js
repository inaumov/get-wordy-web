const cardsAPI = "api/v1/dictionaries";

const allCardsApp = Vue.createApp({
    data() {
        return {
            cards: []
        }
    },
    methods: {
        async getData() {
            const response = await fetchCards();
            this.cards = await response.json();
        }
    },
    created() {
        this.getData()
    }
});
allCardsApp.mount('#all-cards-panel');

const generateCardsApp = Vue.createApp({
    data() {
        return {
            formData: {
                words: [],
            },
        }
    },
    methods: {
        post_words_form : postGenerateCards
    }
});
generateCardsApp.mount('#generate-cards-panel');

const addCardApp = Vue.createApp({
    data() {
        return {
            parts: ["noun", "pronoun", "verb", "adjective", "adverb", "phrasal verb"],
            formData: {
                word: '',
                partOfSpeech: '',
                transcription: '',
                meaning: '',
                contexts: [],
                collocations: []
            },
        }
    },
    methods: {
        post_card_form : postCardForm
    }
});
addCardApp.mount('#add-card-panel');

function renderCardsPanel() {
    hidePanel('generate-cards-panel');
    hidePanel('add-card-panel');
    hidePanel('custom-alert-panel');
    hidePanel('play-game-panel-inner');
    hidePanel('play-game-panel');
    // show form
    showPanel('all-cards-panel'); // temporarily
}

function fetchCards() {
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

    return fetch(cardsRequest)
        .catch(err => console.log("HTTP error: ", err));
}

function displayCards(responseJson) {
    // hide other tabs
    hidePanel('play-game-panel');
    hidePanel('play-game-panel-inner');
    hidePanel('generate-cards-panel');
    hidePanel('add-card-panel');
    hidePanel('custom-alert-panel');
}

function appendRow(tableRow, cardElement) {
    tableRow.addEventListener("click", function () {
        loadDetails(cardElement);
    }, false);
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
        .then(data => showCard(data))
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

function renderPlayPanel() {
    // hide other tabs
    hidePanel('all-cards-panel');
    hidePanel('generate-cards-panel');
    hidePanel('add-card-panel');
    hidePanel('custom-alert-panel');
    hidePanel('play-game-panel-inner');

    // show form
    showPanel('play-game-panel');
}

function renderGeneratePanel() {
    // hide other tabs
    hidePanel('all-cards-panel');
    hidePanel('play-game-panel');
    hidePanel('play-game-panel-inner');
    hidePanel('add-card-panel');
    hidePanel('custom-alert-panel');
    // show form
    showPanel('generate-cards-panel');
}

function renderAddCardPanel() {
    // clear the previous content
    hidePanel('all-cards-panel');
    hidePanel('play-game-panel');
    hidePanel('play-game-panel-inner');

    hidePanel('generate-cards-panel');
    hidePanel('custom-alert-panel');
    // show form
    showPanel("add-card-panel");
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

function postGenerateCards() {
    let words = this.formData.words;
    const valuesArr = !words ? [] : words.split('\n');

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

    let word = this.formData.word;
    let partOfSpeech = this.formData.partOfSpeech;
    let transcription = this.formData.transcription;
    let meaning = this.formData.meaning;
    let contexts = this.formData.contexts;
    let collocations = this.formData.collocations;

    const contextArr = !contexts ? [] : contexts.split('\n');
    const collocationArr = !collocations ? [] : collocations.split('\n');

    let reqHeader = new Headers();
    reqHeader.append('Content-Type', 'application/json');

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
            let limit = fiboNumbers[i];
            let dictionaryId = getQueryVariable('dictionaryId');
            printCardsForExercise(limit, dictionaryId);
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
    btn.innerHTML = "Print cards";
    btn.onclick = function () {
        let limit = die1 + die2;
        let dictionaryId = getQueryVariable('dictionaryId');
        printCardsForExercise(limit, dictionaryId);
    }
    playGamePanel.appendChild(playAgain);
}

function printCardsForExercise(limit, dictionaryId) {
    let reqHeader = new Headers();
    reqHeader.append('Content-Type', 'text/json');

    let initObject = {
        method: 'GET', headers: reqHeader,
    };

    let cardsRequest = new Request(cardsAPI + "/" + dictionaryId + "/exercise?limit=" + limit, initObject);

    fetch(cardsRequest)
        .then(response => response.json())
        .then(data => alert("Words: " + data.map(card => card.word.value)))
        .catch(err => console.log("HTTP error: ", err));
}
