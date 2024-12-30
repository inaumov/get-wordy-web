<script>
import {fetchDictionaries} from '@/js/dictionaries.js';

export default {
  name: 'DictionariesView',
  data() {
    return {
      dictionaries: [],
      favoriteWords: {
        "dictionaryId": 5,
        "name": "Favorite words",
        "picture": "https://cdn-icons-png.flaticon.com/512/4208/4208408.png",
        "cardsTotal": 6
      },
    }
  },
  methods: {
    async getData() {
      const response = await fetchDictionaries();
      this.dictionaries = await response.json();
    }
  },
  mounted() {
    this.getData()
  },
  computed: {
    hasDictionaries() {
      return this.dictionaries && this.dictionaries.length > 0;
    },
    hasFavoriteWords() {
      return true;
    }
  }
};
</script>

<template>
  <div v-if="hasFavoriteWords" class="container p-4" id="active-vocabulary">
    <div id="favorite_words" class="card text-center">
      <img v-bind:src="favoriteWords['picture']" class="card-img-top mx-auto d-block"
           v-bind:alt="favoriteWords['name']">
      <div class="card-body">
        <h5 class="card-title">{{ favoriteWords['name'] }}</h5>
        <router-link class="btn btn-primary"
                     :to="{ name: 'all-cards', params: { dictionaryId : favoriteWords['dictionaryId']}, query: { dictionaryName: favoriteWords['name'] }}">
          {{ favoriteWords['cardsTotal'] }}
        </router-link>
      </div>
    </div>
  </div>

  <div v-if="hasDictionaries" class="container p-4" id="content">
    <h4 class="pb-4">My dictionaries</h4>

    <div id="dictionary" class="card text-center" v-for="dictionary in dictionaries">
      <img v-bind:src="dictionary['picture']" class="card-img-top mx-auto d-block" v-bind:alt="dictionary['name']">
      <div class="card-body">
        <h5 class="card-title">{{ dictionary['name'] }}</h5>
        <router-link class="btn btn-primary"
                     :to="{ name: 'all-cards', params: { dictionaryId : dictionary['dictionaryId']}, query: { dictionaryName: dictionary['name'] }}">
          {{ dictionary['cardsTotal'] }}
        </router-link>
      </div>
    </div>
  </div>
  <div v-else class="d-flex justify-content-center p-5">
    <p class="lead">Loading dictionaries...</p>
  </div>
</template>

<style>

div#favorite_words.card,
div#dictionary.card {
  border-radius: 40px;
  overflow: hidden;
  border: 0;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.06),
  0 2px 4px rgba(0, 0, 0, 0.07);
  transition: all 0.15s ease;
  display: inline-block;
}

div#favorite_words.card:hover,
div#dictionary.card:hover {
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.1),
  0 10px 8px rgba(0, 0, 0, 0.015);
}

div#favorite_words.card .card-body .card-title,
div#dictionary.card .card-body .card-title {
  font-weight: 600;
  font-size: 24px;
}

div#favorite_words.card:hover > img,
div#dictionary.card:hover > img {
  transform: scale(1.2);
}

div#favorite_words.card img,
div#dictionary.card img {
  padding: 75px;
  margin-top: -40px;
  margin-bottom: -40px;
  transition: 0.4s ease;
  cursor: pointer;
  max-width: 256px;
  height: auto;
}

div#favorite_words.card .btn,
div#dictionary.card .btn {
  background: #e9ecef;
  border: 0;
  color: #5535f0;
  width: 98%;
  font-weight: bold;
  border-radius: 20px;
  height: 40px;
  transition: all 0.2s ease;
}

div#favorite_words.card .btn:hover,
div#dictionary.card .btn:hover {
  background: #d63384;
  color: #e9ecef;
}

div#favorite_words.card .btn:focus,
div#dictionary.card .btn:focus {
  background: #d63384;
  outline: 0;
  color: #e9ecef;
}

</style>
