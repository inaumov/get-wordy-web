<script>
import {fetchClasses} from '@/js/classes-api.js';
import {applyCaption} from '@/js/utils.js'
import {useRouter} from "vue-router";

export default {
  props: ['dayOfWeek'],
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
      const response = await fetchClasses(this.dayOfWeek);
      const jsonData = await response.json();
      this.classList = [...this.classList, ...jsonData];
    },
    navigateToWordsheetList(classItem) {
      this.router.push(
          {
            name: 'class-wordsheet-list',
            params: {
              classId: classItem['classId']
            }
          }
      );
      console.log("Selected class: id = ", classItem['classId'], ", name = ", classItem['name'])
    },
    addNewClass() {
      this.router.push({name: 'add-new-class'});
    }
  },
  computed: {
    hasClasses() {
      return this.classList && this.classList.length > 0;
    }
  },
  mounted() {
    this.getData()
    applyCaption('Desna Academy')
  }
};
</script>

<template>
  <div v-if="hasClasses" class="container mt-5">
    <div class="day-groups">
      <h3 class="my-3">{{ dayOfWeek }}</h3>
      <div class="row">
        <div class="col-md-4 card" v-for="classItem in classList"
             :key="classItem['classId']"
             @click="navigateToWordsheetList(classItem)">
          <div class="card-body">
            <span class="class-id">Class ID: {{ classItem['classId'] }}</span>
            <div>
              <div class="class-details">
                <span>{{ classItem['name'] }}</span>
              </div>
              <div v-if="classItem['format']" class="class-details">
                <span><strong>Format</strong> : {{ classItem['format'] }}</span>
              </div>
              <div v-if="classItem['attendees']" class="class-details">
                <span><strong>Attendees</strong> : {{ classItem['attendees'] }}</span>
              </div>
              <div v-if="classItem['level']" class="class-details">
                <span><strong>Level</strong> : {{ classItem['level'] }}</span>
              </div>
              <div v-if="classItem['material']" class="class-details">
                <span><strong>Materials</strong> : {{ classItem['material'] }}</span>
              </div>
              <div v-if="classItem['notes']" class="class-details">
                <span><strong>Notes</strong> : {{ classItem['notes'] }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-4 card" @click="addNewClass()"
             data-bs-toggle="tooltip"
             data-bs-placement="right"
             title="Start a new class"
        >
          <div class="card-body d-flex justify-content-center align-items-center">
            <div>
              <!-- plus icon centered within the card -->
              <i class="bi bi-plus" style="font-size: 2rem;"></i>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="d-flex justify-content-center p-5">
    <p class="lead">No classes registered yet, please create.</p>
  </div>
</template>

<style scoped>
.card {
  cursor: pointer;
  min-height: 178px;
}

.class-id {
  color: grey;
  font-size: 0.75rem;
  right: 10px;
  top: 10px;
}

.day-groups h3 {
  font-size: 1.5rem;
}
</style>
