<script>
export default {
  name: 'CreateModal',
  props: {
    modalName: {
      type: String,
      required: true
    },
    onSubmit: {
      type: Function,
      required: true
    }
  },
  data() {
    return {
      visible: false,
      name: '',
      error: '',
      loading: false
    };
  },
  methods: {
    open() {
      this.visible = true;
      this.reset();
      this.$nextTick(() => {
        this.$refs.nameInput?.focus();
      });
    },
    close() {
      this.visible = false;
    },
    reset() {
      this.name = '';
      this.error = '';
      this.loading = false;
    },
    clearError() {
      this.error = '';
    },
    async submit() {
      const name = this.name.trim();
      if (!name) {
        this.error = 'Name is required.';
        return;
      }
      this.loading = true;
      this.error = '';
      try {
        await this.onSubmit(name);
        this.close();
      } catch (err) {
        const status = err.status ?? err.response?.status;
        if (status === 409) {
          this.error = err.message || 'A record with this name already exists.';
        } else {
          this.error = err.message || 'Unexpected error. Please try again.';
          console.error(err);
        }
      } finally {
        this.loading = false;
      }
    },

    handleKeydown(event) {
      if (event.key === 'Escape') {
        this.close();
      }
      if (event.key === 'Enter') {
        this.submit();
      }
    }
  },

  computed: {
    title() {
      return `Create new ${this.modalName}`;
    }
  },

  mounted() {
    window.addEventListener('keydown', this.handleKeydown);
  },

  beforeUnmount() {
    window.removeEventListener('keydown', this.handleKeydown);
  }
};
</script>

<template>
  <div
      v-if="visible"
      class="modal-backdrop"
      @click.self="close"
  >
    <div
        class="modal-box"
        role="dialog"
        aria-modal="true"
    >

      <h5 class="modal-title mb-3">
        {{ title }}
      </h5>

      <input
          ref="nameInput"
          v-model="name"
          type="text"
          class="form-control"
          :class="{ 'is-invalid': error }"
          placeholder="Enter name"
          autocomplete="off"
          @input="clearError"
      />

      <div
          v-if="error"
          class="invalid-feedback d-block"
      >
        {{ error }}
      </div>

      <div class="modal-actions">
        <button
            class="btn btn-sm btn-outline-secondary"
            :disabled="loading"
            @click="close"
        >
          Cancel
        </button>

        <button
            class="btn btn-sm btn-primary"
            :disabled="loading"
            @click="submit"
        >
          Create
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgb(0 0 0 / 35%);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1050;
}

.modal-box {
  background: white;
  border-radius: 8px;
  padding: 24px;
  width: min(400px, calc(100% - 32px));
  box-shadow:
      0 20px 40px rgb(0 0 0 / 20%);
}

.modal-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

</style>
