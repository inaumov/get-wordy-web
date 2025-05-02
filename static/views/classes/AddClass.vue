<script>
import {useRouter} from "vue-router";
import {createClass} from '@/js/classes-api.js';
import ClassInfoForm from "@/components/classes/ClassInfoForm.vue";

export default {
  components: {ClassInfoForm},
  setup() {
    let router = useRouter();
    return {router}
  },
  data() {
    return {
      classInfo: {
        name: '',
        format: '',
        scheduleType: 'NONE',
        schedules: [],
        endDate: '',
        notes: ''
      }
    }
  },
  methods: {
    onSubmit(classInfo) {
      createClass(classInfo)
          .then(response => {
            if (response.ok) {
              this.router.push({
                name: 'day-classes'
              });
            }
            console.log("POST new class has been requested. Response.status =", response.status);
          })
    }
  },
  emits: ['classInfoSubmit']
};

</script>

<template>
  <div class="p-4 d-flex flex-column align-items-start">
    <router-link :to="{name: 'day-classes'}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>
  <ClassInfoForm :classInfo="this.classInfo" @classInfoSubmit="onSubmit"/>
</template>

<style>
</style>
