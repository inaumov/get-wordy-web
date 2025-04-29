<script>
import {fetchDictionaries} from '@/js/dictionaries.js';

export default {
  name: 'DictionariesView',
  data() {
    return {
      vocabularies: [],
      favorites: [],
    }
  },
  methods: {
    async getData() {
      const response = await fetchDictionaries();
      const all = await response.json();
      const [favorites, vocabularies] = all.reduce(
          ([fav, nonFav], item) => {
            item['isFavorite'] ? fav.push(item) : nonFav.push(item);
            return [fav, nonFav];
          },
          [[], []]
      );
      this.vocabularies = vocabularies;
      this.favorites = favorites;
    }
  },
  mounted() {
    this.getData()
  },
  computed: {
    hasVocabularies() {
      return this.vocabularies && this.vocabularies.length > 0;
    },
    hasFavorites() {
      return this.favorites && this.favorites.length > 0;
    }
  }
};
</script>

<template>
  <div v-if="hasFavorites" class="container p-4" id="active-vocabulary">
    <h4 class="pb-4">Favorite words</h4>

    <div id="favorite_words" class="card text-center" v-for="favorite in favorites">
      <img v-bind:src="favorite['picture']" class="card-img-top mx-auto d-block"
           v-bind:alt="favorite['name']">
      <div class="card-body">
        <h5 class="card-title">{{ favorite['name'] }}</h5>
        <router-link class="btn btn-primary"
                     :to="{ name: 'all-cards', params: { dictionaryId : favorite['dictionaryId']}, query: { dictionaryName: favorite['name'] }}">
          {{ favorite['wordsTotal'] }}
        </router-link>
      </div>
    </div>
  </div>

  <div v-if="hasVocabularies" class="container p-4" id="content">
    <h4 class="pb-4">Own vocabularies</h4>

    <div class="row">
      <div class="col" id="settings">
        <div class="button-group d-flex flex-column align-items-end">

          <router-link to="/Settings" class="btn btn-lg">
            <i class="bi bi-gear"></i>
          </router-link>

        </div>
      </div>
    </div>

    <div id="dictionary" class="card text-center" v-for="vcb in vocabularies">
      <img v-bind:src="vcb['picture']" class="card-img-top mx-auto d-block" v-bind:alt="vcb['name']">
      <div class="card-body">
        <h5 class="card-title">{{ vcb['name'] }}</h5>
        <router-link class="btn btn-primary"
                     :to="{ name: 'all-cards', params: { dictionaryId : vcb['dictionaryId']}, query: { dictionaryName: vcb['name'] }}">
          {{ vcb['wordsTotal'] }}
        </router-link>
      </div>
    </div>
  </div>
  <div v-else class="d-flex justify-content-center p-5">
    <p class="lead">No vocabularies has been created so far...</p>
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
