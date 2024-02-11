const dictionariesAPI = "api/v1/dictionaries";

const app = Vue.createApp({
    data() {
        return {
            dictionaries: [],
            build_link: buildLink
        }
    },
    methods: {
        async getData() {
            const response = await fetchDictionaries();
            this.dictionaries = await response.json();
        }
    },
    created() {
        this.getData()
    }
});
app.mount('#content');

function fetchDictionaries() {
    let reqHeader = new Headers();
    reqHeader.append('Content-Type', 'application/json');

    let initObject = {
        method: 'GET', headers: reqHeader,
    };

    let dictionariesRequest = new Request(dictionariesAPI, initObject);

    return fetch(dictionariesRequest)
        .catch(err => console.log("HTTP error: ", err));
}

function buildLink(dictionary) {
    let id = dictionary.dictionaryId;
    let name = encodeURIComponent(dictionary.name);
    return '/GetWordyApp/cards.html?dictionaryId=' + id + '&dictionaryName=' + name;
}
