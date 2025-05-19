<script>
import EditClass from "./EditClass.vue";
import Materials from "./MaterialsTab.vue";
import Attendees from "./AttendeesTab.vue";
import {getClass} from "@/js/classes-api.js";
import ClassVocabularies from "@/views/classes/ClassVocabularies.vue";

export default {
  props: ['classId', 'day'],
  name: "ClassDetails",
  components: {ClassVocabularies, Materials, EditClass, Attendees},
  data() {
    return {
      isEditMode: false,
      classInfo: {
        attendees: []
      }
    }
  },
  methods: {
    async getData() {
      const response = await getClass(this.classId);
      this.classInfo = await response.json();
    },
    showEditForm() {
      this.isEditMode = true;
    },
    hideEditForm() {
      this.isEditMode = false;
    }
  },
  mounted() {
    this.getData()
  },
  computed: {
    className() {
      // access the state passed via the router
      return this.classInfo['name'] || 'Error';
    },
  },
};
</script>

<template>
  <div v-if="!this.isEditMode" class="p-4 d-flex flex-column align-items-start">
    <router-link :to="{name: 'day-classes', params: {day: this.day}}" class="btn btn-secondary" title="Back">Back
    </router-link>
  </div>
  <div v-if="!this.isEditMode" class="p-4 d-flex flex-column align-items-end" style="gap: 20px">
    <button @click="showEditForm" class="btn btn-light">Edit class info</button>
  </div>
  <div v-else class="p-4 d-flex flex-column align-items-start">
    <button @click="hideEditForm" class="btn btn-secondary" title="Back">Back</button>
  </div>

  <div v-if="!this.isEditMode" class="p-4">
    <h4 class="">{{ className }}</h4>
    <!-- Page Content -->
    <Materials class="pb-5" v-bind="{classId: this.classId}"/>
    <div class="d-flex gap-3">
      <!-- Left column: 70% -->
      <ClassVocabularies class="flex-grow-1" style="flex-basis: 70%;" v-bind="{classId: this.classId}"/>
      <!-- Right column: 30% -->
      <Attendees style="flex-basis: 30%; max-width: 30%;" v-if="classInfo && classInfo.attendees" v-bind="{ classInfo }"/>
    </div>
  </div>
  <EditClass v-if="this.isEditMode"
             v-bind="{classInfo: this.classInfo}"
             @success="isEditMode = false"
  />
</template>

<style scoped>

</style>
