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
      updateClassInfo({
        classId: this.classInfo.classId,
        ...updatedClassInfo
      })
          .then(response => {
            if (response.ok) {
              console.log('Class info updated');
              this.$emit('success'); // bubble up to top-level
            }
          })
          .catch(error => console.error('Error updating class info:', error));
    }
  },
  emits: ['classInfoSubmit', 'success']
};
</script>

<template>
  <ClassInfoForm :classInfo="this.classInfo" @classInfoSubmit="onUpdate"/>
</template>
