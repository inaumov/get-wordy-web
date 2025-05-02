<script>
import {updateClassInfo} from '@/js/classes-api.js';
import ClassInfoForm from "@/components/classes/ClassInfoForm.vue";

export default {
  components: {ClassInfoForm},
  props: ['classInfo'],
  setup(props) {
    console.log("Prop classInfo in setup:", props.classInfo);
    return {};
  },
  data() {
  },
  methods: {
    onUpdate(updatedClassInfo) {
      updateClassInfo(updatedClassInfo)
          .then(response => {
            if (response.ok) {
              console.log('Class info updated');
            }
          })
          .catch(error => console.error('Error updating class info:', error));
    }
  },
  emits: ['classInfoSubmit']
};
</script>

<template>
  <ClassInfoForm :classInfo="this.classInfo" @classInfoSubmit="onUpdate"/>
</template>
