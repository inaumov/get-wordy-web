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
    }
  },
  methods: {
    onSubmit: function () {
      let form = document.getElementById('start-class-form');
      let formData = new FormData(form);

      let newItem = {
        name: formData.get('name'),
        classFormat: formData.get('classFormat'),
        classLevel: formData.get('classLevel'),
        material: formData.get('material'),
        notes: formData.get('notes')
      };
      createClass(newItem)
          .then(response => {
            if (response.ok) {
              this.router.push({
                name: 'class-list'
              });
            }
            console.log("POST new class has been requested. Response.status =", response.status);
          });
    }
  },
};

</script>

<template>
  <div class="container mt-5 d-flex justify-content-center">
    <div class="w-100" style="max-width: 800px;">
      <h2 class="mb-4">Create class</h2>
      <form id="start-class-form" @submit.prevent="onSubmit">

        <!-- first row: class name and class format -->
        <div class="row mb-3">
          <div class="col-6">
            <label for="name" class="form-label">Class name<i>*</i></label>
            <input type="text" class="form-control" id="name" name="name" autocomplete="off" required>
          </div>
          <div class="col-6">
            <label for="classFormat" class="form-label">Class format</label>
            <input type="text" class="form-control" id="classFormat" name="classFormat" autocomplete="off">
            <small class="form-text text-muted">e.g., Online, In-person, Hybrid, VIP</small>
          </div>
        </div>

        <!-- second row: class level and material -->
        <div class="row mb-3">
          <div class="col-6">
            <label for="classLevel" class="form-label">Class level</label>
            <input type="text" class="form-control" id="classLevel" name="classLevel" autocomplete="off">
            <small class="form-text text-muted">e.g., Beginner, Intermediate, Advanced</small>
          </div>
          <div class="col-6">
            <label for="material" class="form-label">Material details</label>
            <input type="text" class="form-control" id="material" name="material" autocomplete="off">
          </div>
        </div>

        <!-- third row: notes (textarea) -->
        <div class="row mb-3">
          <div class="col-12">
            <label for="notes" class="form-label">Enter any additional notes (Max 500 characters)</label>
            <textarea class="form-control" id="notes" name="notes" rows="5" maxlength="500"
                      autocomplete="off"></textarea>
            <small class="form-text text-muted">Max 500 characters</small>
          </div>
        </div>

        <!-- submit / back Buttons -->
        <div class="row py-4">
          <div class="col-6 text-start">
            <router-link :to="{name: 'home'}" class="btn btn-secondary" title="Back">Back</router-link>
          </div>
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
