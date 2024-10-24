<script>
import {fetchClasses} from '@/js/classes-api.js';
import {applyCaption} from '@/js/utils.js'
import {useRouter} from "vue-router";

export default {
  name: 'ClassListView',
  setup() {
    let router = useRouter();
    return {router}
  },
  data() {
    return {
      classList: [],
    }
  },
  methods: {
    async getData() {
      const response = await fetchClasses();
      this.classList = await response.json();
    },
    navigateToDictionaries() {
      this.router.push({name: 'dictionaries'});
    },
  },

  mounted() {
    this.getData()
    applyCaption('Desna Academy')
  }
};
</script>

<template>
  <div class="container mt-5">
    <div v-for="el in classList" :key="el.group" class="age-group">
      <h3>{{ el.group }}</h3>
      <ul class="list-group mb-4">
        <li
            v-for="classItem in el.classes"
            :key="classItem['classId']"
            class="list-group-item position-relative"
            @click="navigateToDictionaries"
            style="cursor: pointer;"
        >
          <span class="class-id">Class ID: {{ classItem['classId'] }}</span>
          <div>
            <span><strong>Class:</strong> {{ classItem['name'] }}</span><br>
            <span><strong>Format:</strong> {{ classItem['classFormat'] }}</span><br>
            <span><strong>Level:</strong> {{ classItem['classLevel'] }}</span>
          </div>
          <div class="class-info">
            <span><strong>Description:</strong> {{ classItem['courseDescription'] }}</span><br>
            <span><strong>Dictionaries in Total:</strong> {{ classItem['dictionariesTotal'] }}</span>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<style>
.class-id {
  color: grey;
  font-size: 0.75rem;
  right: 10px;
  top: 10px;
}

.class-info {
  display: flex;
  justify-content: space-between;
}

.age-group h3 {
  font-size: 1.5rem;
}
</style>
