<script>
import EditClass from "./EditClass.vue";
import Actions from "./NewVocabularyActions.vue";
import Participants from "./Participants.vue";
import Vocabularies from "@/views/classes/ClassVocabularies.vue";
import {classActivation, deleteClass, getClass} from "@/js/classes-api.js";
import ClassInfoCard from "@/views/classes/ClassInfoCard.vue";
import {useRouter} from "vue-router";

export default {
  props: ['classId', 'day'],
  name: "ClassDetails",
  components: {ClassInfoCard, Vocabularies, Actions, EditClass, Participants},
  setup() {
    let router = useRouter();
    return {router}
  },
  data() {
    return {
      isEditMode: false,
      classInfo: {
        participants: []
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
    },
    async onActivation() {
      let active = !this.classInfo.isActive;
      const response = await classActivation(this.classId, active);
      if (response.ok) {
        await this.getData() // reload because of schedule reset
      }
      if (!response.ok) {
        const err = await response.json();
        console.log("Failed to send class activation request", err)
      }
    },
    async onDelete() {
      const response = await deleteClass(this.classId);
      if (response.ok) {
        this.navigateToClasses();
      }
      if (!response.ok) {
        const err = await response.json();
        console.log("Failed to delete a class", err)
      }
    },
    navigateToClasses() {
      this.router.push({
        name: 'day-classes',
        params: {day: this.day}
      });
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
  <div v-if="!this.isEditMode" class="p-4 d-flex justify-content-start">
    <router-link :to="{name: 'day-classes', params: {day: this.day}}" class="btn btn-secondary" title="Back">
      Back
    </router-link>
  </div>
  <div v-else class="px-4 d-flex justify-content-start">
    <button @click="hideEditForm" class="btn btn-secondary" title="Back">Back</button>
  </div>
  <ClassInfoCard v-if="!isEditMode" :class-info="this.classInfo" class="px-4"
      @edit="showEditForm"
      @activation="onActivation"
      @delete="onDelete"
  />

  <div v-if="!this.isEditMode" class="p-4">
    <!-- Page Content -->
    <Actions class="pb-5" v-bind="{classId: this.classId}"/>
    <div class="d-flex gap-5">
      <!-- Left column: 70% -->
      <Vocabularies class="flex-grow-1" style="flex-basis: 70%;" v-bind="{classId: this.classId}"/>
      <!-- Right column: 30% -->
      <Participants style="flex-basis: 30%; max-width: 30%;" v-if="classInfo && classInfo.participants"
                    v-bind="{ classInfo }"/>
    </div>
  </div>
  <EditClass v-if="this.isEditMode"
             v-bind="{classInfo: this.classInfo}"
             @success="isEditMode = false"
  />
</template>

<style scoped>

</style>
