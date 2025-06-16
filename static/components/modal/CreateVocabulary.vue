<script>
export default {
  name: 'CreateVocabularyModal',
  props: {
    createAction: {
      type: Function,
      required: true
    }
  },
  data() {
    return {
      visible: false,
      vocabName: '',
      error: ''
    };
  },
  methods: {
    open() {
      this.visible = true;
      this.vocabName = '';
      this.error = '';
    },
    close() {
      this.visible = false;
    },
    clearError() {
      this.error = '';
    },
    async submit() {
      if (!this.vocabName) {
        this.error = 'Vocabulary name is required.';
        return;
      }
      try {
        await this.createAction(this.vocabName);
        this.close();
      } catch (err) {
        if (err.status === 409) {
          this.error = err.message || 'Duplicate vocabulary name.';
        } else {
          this.error = 'Unexpected error. Please try again.';
          console.error(err);
        }
      }
    }
  }
};
</script>

<template>
  <div v-if="visible" class="modal-backdrop">
    <div class="modal-box">
      <h5 class="modal-title">Create New Vocabulary</h5>

      <div class="my-3">
        <input
            id="vocabName"
            v-model="vocabName"
            type="text"
            class="form-control"
            :class="{ 'is-invalid': error }"
            placeholder="Enter vocabulary name"
            @input="clearError"
            autocomplete="off"
            required
        />
        <div v-if="error" class="form-text text-danger">{{ error }}</div>
      </div>

      <div class="mt-4 text-end">
        <button class="btn btn-sm btn-outline-secondary me-2" @click="close">Cancel</button>
        <button class="btn btn-sm btn-outline-primary" @click="submit">Create</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050;
}

.modal-box {
  background: white;
  border-radius: 8px;
  padding: 24px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.is-invalid {
  border-color: #dc3545;
}

</style>
