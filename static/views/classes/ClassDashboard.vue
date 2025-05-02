<script>
import EditClass from "./EditClass.vue";
import Materials from "./MaterialsTab.vue";
import Attendees from "./AttendeesTab.vue";
import {getClass} from "@/js/classes-api.js";

export default {
  props: ['classId', 'day'],
  name: "ClassDetails",
  components: {Materials, EditClass, Attendees},
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

    <!-- Tabs Navigation -->
    <ul class="nav nav-tabs" id="classDetailsTabs" role="tablist">
      <li class="nav-item" role="presentation">
        <button
            class="nav-link active"
            id="materials-tab"
            data-bs-toggle="tab"
            data-bs-target="#materials"
            type="button"
            role="tab"
            aria-controls="materials"
            aria-selected="true"
        >
          Vocabularies
        </button>
      </li>
      <li class="nav-item" role="presentation">
        <button
            class="nav-link"
            id="attendees-tab"
            data-bs-toggle="tab"
            data-bs-target="#attendees"
            type="button"
            role="tab"
            aria-controls="attendees"
            aria-selected="false"
        >
          Attendees
        </button>
      </li>
    </ul>

    <!-- Tab Content -->
    <div class="tab-content mt-3" id="classDetailsTabContent">
      <div
          class="tab-pane fade show active"
          id="materials"
          role="tabpanel"
          aria-labelledby="materials-tab"
      >
        <Materials v-bind="{classId: this.classId}"/>
      </div>
      <div
          class="tab-pane fade"
          id="attendees"
          role="tabpanel"
          aria-labelledby="attendees-tab"
      >
        <Attendees v-if="classInfo && classInfo.attendees" v-bind="{ classInfo }"/>
      </div>
    </div>
  </div>
  <EditClass v-if="this.isEditMode"
             v-bind="{classInfo: this.classInfo}"
             @success="isEditMode = false"
  />
</template>

<style scoped>

</style>
