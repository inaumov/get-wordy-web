<script>
import {fetchDictionaries} from '@/assets/dictionaries.js';
import {applyCaption} from '@/js/utils.js'

export default {
  data() {
    return {
      dictionaries: [
        {
          dictionaryId: '',
          picture: '',
          name: '',
          cardsTotal: ''
        }
      ],
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
    applyCaption('My dictionaries')
  }
};
</script>

<template>
  <div v-if="dictionaries[0].dictionaryId" class="container p-4" id="content">
    <div id="dictionary" class="card text-center" v-for="dictionary in dictionaries">
      <img v-bind:src="dictionary.picture" class="card-img-top mx-auto d-block" v-bind:alt="dictionary.name">
      <div class="card-body">
        <h5 class="card-title">{{ dictionary.name }}</h5>
        <router-link class="btn btn-primary"
                     :to="{ name: 'all-cards', params: { dictionaryId : dictionary.dictionaryId}, query: { dictionaryName: dictionary.name }}">
          {{ dictionary.cardsTotal }}
        </router-link>
      </div>
    </div>
  </div>
  <div v-else class="d-flex justify-content-center p-5">
    <p>Loading dictionaries...</p>
  </div>
</template>

<style scoped>

div#dictionary.card {
  border-radius: 40px;
  overflow: hidden;
  border: 0;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.06),
  0 2px 4px rgba(0, 0, 0, 0.07);
  transition: all 0.15s ease;
  display: inline-block;
}

div#dictionary.card:hover {
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.1),
  0 10px 8px rgba(0, 0, 0, 0.015);
}

div#dictionary.card .card-body .card-title {
  font-weight: 600;
  font-size: 24px;
}

div#dictionary.card:hover > img {
  transform: scale(1.2);
}

div#dictionary.card img {
  padding: 75px;
  margin-top: -40px;
  margin-bottom: -40px;
  transition: 0.4s ease;
  cursor: pointer;
  max-width: 256px;
  height: auto;
}

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

div#dictionary.card .btn:hover {
  background: #d63384;
  color: #e9ecef;
}

div#dictionary.card .btn:focus {
  background: #d63384;
  outline: 0;
  color: #e9ecef;
}

</style>
