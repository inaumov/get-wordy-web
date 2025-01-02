<script>
import {updateClassInfo} from '@/js/classes-api.js';

export default {
  props: ['classInfo'],
  setup(props) {
    console.log("Prop classInfo in setup:", props.classInfo);
    return {
      classInfoDays: props.classInfo['days'] || [],
    };
  },
  data() {
    return {
      days: ["sun", "mon", "tue", "wen", "thu", "fri", "sat"],
      selectedDays: this.classInfoDays || [],
    }
  },
  created() {
    console.log("Prop classInfo in created:", this.classInfo);
  },
  methods: {
    onSubmit: function () {
      // reset any previous validation state
      this.$refs.classDays.classList.remove('is-invalid');
      // validate the selected days
      if (this.selectedDays.length === 0) {
        // if no days are selected, apply the 'is-invalid' class
        this.$refs.classDays.classList.add('is-invalid');
        return;
      }
      let form = document.getElementById('start-class-form');
      let formData = new FormData(form);

      let newItem = {
        name: formData.get('name'),
        classFormat: formData.get('classFormat'),
        classLevel: formData.get('classLevel'),
        material: formData.get('material'),
        notes: formData.get('notes')
      };
      updateClassInfo(newItem)
          .then(response => {
            if (response.ok) {
            }
            console.log("PUT: class info has been requested. Response.status =", response.status);
          });
    }
  },
};

</script>

<template>
  <div class="container mt-5 d-flex justify-content-center">
    <div class="w-100" style="max-width: 800px;">
      <form id="start-class-form" @submit.prevent="onSubmit">

        <!-- first row: class name and class format -->
        <div class="row mb-3">
          <div class="col-6">
            <label for="name" class="form-label">Class name (or time slot)<i>*</i></label>
            <input type="text" v-model="this.classInfo['name']" class="form-control" id="name" name="name"
                   autocomplete="off" required>
          </div>
          <div class="col-6">
            <label for="classFormat" class="form-label">Class format</label>
            <input type="text" v-model="this.classInfo['format']" class="form-control" id="classFormat"
                   name="classFormat"
                   autocomplete="off">
            <small class="form-text text-muted">e.g., Online, In-person, Hybrid, VIP</small>
          </div>
        </div>

        <!-- second row: class level and material -->
        <div class="row mb-3">
          <div class="col-6">
            <label for="classLevel" class="form-label">Class level</label>
            <input type="text" v-model="this.classInfo['level']" class="form-control" id="classLevel" name="classLevel"
                   autocomplete="off">
            <small class="form-text text-muted">e.g., Beginner, Intermediate, Advanced</small>
          </div>
          <div class="col-6">
            <label for="material" class="form-label">Material details</label>
            <input type="text" v-model="this.classInfo['material']" class="form-control" id="material" name="material"
                   autocomplete="off">
          </div>
        </div>

        <!-- third row: class days selection -->
        <div class="row mb-3">
          <div class="col-12">
            <label for="classDays" class="form-label">Select the days the class takes place<i>*</i></label>
            <div ref="classDays" id="classDays" class="d-flex flex-wrap">
              <div v-for="(day, index) in days" :key="index" class="form-check form-check-inline">
                <input
                    type="checkbox"
                    :id="day"
                    :name="day"
                    :value="day"
                    class="form-check-input"
                    v-model="selectedDays"
                >
                <label class="form-check-label" :for="day" style="text-transform: capitalize;">
                  {{ day }}
                </label>
              </div>
            </div>
            <div v-if="selectedDays.length === 0" class="invalid-feedback">
              Please select at least one day.
            </div>
          </div>
        </div>

        <!-- last row: notes (textarea) -->
        <div class="row mb-3">
          <div class="col-12">
            <label for="notes" class="form-label">Any additional notes</label>
            <textarea class="form-control" id="notes" name="notes" rows="5" maxlength="500"
                      v-model="this.classInfo['notes']"
                      autocomplete="off"></textarea>
            <small class="form-text text-muted">Max 500 characters</small>
          </div>
        </div>

        <!-- submit / back Buttons -->
        <div class="row py-4">
          <div class="d-flex flex-column align-items-end">
            <button type="submit" class="btn btn-primary">Save</button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<style>
</style>
