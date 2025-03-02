<script>
import {useRouter} from "vue-router";
import {createClass} from '@/js/classes-api.js';

export default {
  setup() {
    let router = useRouter();
    return {router}
  },
  data() {
    return {
      days: ["sun", "mon", "tue", "wen", "thu", "fri", "sat"],
      selectedDays: [],
    }
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
        format: formData.get('format'),
        notes: formData.get('notes')
      };
      createClass(newItem)
          .then(response => {
            if (response.ok) {
              this.router.push({
                name: 'day-classes'
              });
            }
            console.log("POST new class has been requested. Response.status =", response.status);
          });
    }
  },
};

</script>

<template>
  <div class="p-4 d-flex flex-column align-items-start">
    <router-link :to="{name: 'schedule'}" class="btn btn-secondary" title="Back">Back</router-link>
  </div>
  <div class="container p-4 d-flex justify-content-center">
    <div class="w-100" style="max-width: 800px;">
      <h2 class="mb-4">Create class</h2>
      <form id="start-class-form" @submit.prevent="onSubmit">

        <!-- first row: class name and class format -->
        <div class="row mb-3">
          <div class="col-6">
            <label for="name" class="form-label">Name<i>*</i></label>
            <input type="text" class="form-control" id="name" name="name" autocomplete="off" required>
          </div>
          <div class="col-6">
            <label for="format" class="form-label">Format</label>
            <input type="text" class="form-control" id="format" name="format" autocomplete="off">
            <small class="form-text text-muted">e.g., Online, In-person, Hybrid, VIP</small>
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
                      autocomplete="off"></textarea>
            <small class="form-text text-muted">Max 500 characters</small>
          </div>
        </div>

        <!-- submit / back Buttons -->
        <div class="row py-4">
          <div class="col-6 text-end">
            <button type="submit" class="btn btn-primary">Save</button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<style>
</style>
