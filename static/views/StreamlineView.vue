<script>
import {fetchClasses} from '@/js/classes-api.js';
import {useRouter} from "vue-router";

export default {
  setup() {
    let router = useRouter();
    return {router}
  },
  data() {
    return {
      streamline: [],
    }
  },
  methods: {
    async getData() {
      const response = await fetchClasses();
      let fullList = await response.json();
      this.streamline = fullList
          .map(item => {
            // add the 'hasDrafts' property based on the condition
            return {
              ...item, // spread the existing properties
              hasDrafts: item['drafts'] && item['drafts'] >= 0,
              hasVocabularies: true
            };
          });
    },
    navigateToVocabularies(classItem) {
      this.router.push({
        name: 'class-vocabularies',
        params: {
          classId: classItem['classId']
        }
      });
    },
    // method to shuffle colors randomly
    shuffleColors() {
      const pastelColors = [
        'pastel-blue', 'pastel-pink', 'pastel-yellow', 'pastel-green',
        'pastel-lavender', 'pastel-peach', 'pastel-mint', 'pastel-coral',
        'pastel-lilac', 'pastel-lemon', 'pastel-sky', 'pastel-beige'
      ];
      // shuffle the pastel colors array using the Fisher-Yates algorithm
      for (let i = pastelColors.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [pastelColors[i], pastelColors[j]] = [pastelColors[j], pastelColors[i]];
      }
      return pastelColors;
    },
  },
  computed: {
    hasClasses() {
      return this.streamline && this.streamline.length > 0;
    },
    badgeColor() {
      let colorOrder = this.shuffleColors();
      return colorOrder[this.streamline.length % colorOrder.length];
    },
  },
  mounted() {
    this.getData()
  }
};
</script>

<template>

  <div v-if="hasClasses" class="container p-4">
    <h4 class="pb-4">Vocabulary Streamline</h4>
    <div class="row mx-1" v-for="classItem in streamline">
      <div class="card my-2"
           :key="classItem['classId']"
           @click="navigateToVocabularies(classItem)">
        <div class="card-body">
          <div>
            <div class="class-details">
              <span><strong>{{ classItem['name'] }}</strong></span>
            </div>
          </div>
          <div v-if="classItem.hasVocabularies" class="row class-details">
                <span class="col-8 text-muted">
                  <strong>Vocabularies:</strong> {{ 0 }}
                </span>
            <span v-if="classItem.hasDrafts" class="col-4 text-muted text-end">
                  <span :class="badgeColor">
                    <strong>Drafts:</strong> {{ classItem['drafts'] }}
                  </span>
                </span>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="d-flex justify-content-center align-items-center vh-100">
    <div class="text-center w-50">
      <p class="lead">No vocabulary streamlining group has been registered yet. Please create one.</p>
    </div>
  </div>
</template>

<style scoped>
.card {
  cursor: pointer;
}
</style>
