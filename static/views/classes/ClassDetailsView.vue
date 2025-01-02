<script>
import ClassInfo from "./ClassInfoTab.vue";
import Materials from "./MaterialsTab.vue";
import Attendees from "./AttendeesTab.vue";
import {getClass} from "@/js/classes-api.js";

export default {
  props: ['classId'],
  name: "ClassDetails",
  components: {Materials, ClassInfo, Attendees},
  data() {
    return {
      classInfo: {}
    }
  },
  methods: {
    async getData() {
      const response = await getClass(this.classId);
      this.classInfo = await response.json();
    },
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
  <div class="p-4 d-flex flex-column align-items-start">
    <router-link :to="{name: 'classes'}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>

  <div class="container mt-4">
    <h4 class="pb-4">{{ className }}</h4>

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
          Materials
        </button>
      </li>
      <li class="nav-item" role="presentation">
        <button
            class="nav-link"
            id="class-info-tab"
            data-bs-toggle="tab"
            data-bs-target="#class-info"
            type="button"
            role="tab"
            aria-controls="class-info"
            aria-selected="false"
        >
          Class Info
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
        <Materials/>
      </div>
      <div
          class="tab-pane fade"
          id="class-info"
          role="tabpanel"
          aria-labelledby="class-info-tab"
      >
        <ClassInfo v-bind="{classInfo: this.classInfo}"/>
      </div>
      <div
          class="tab-pane fade"
          id="attendees"
          role="tabpanel"
          aria-labelledby="attendees-tab"
      >
        <Attendees/>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
