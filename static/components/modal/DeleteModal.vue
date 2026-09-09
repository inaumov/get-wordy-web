<script>
export default {
  name: 'DeleteModal',

  props: {
    modalName: {
      type: String,
      required: true
    },
    itemName: {
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
      loading: false,
      error: ''
    };
  },

  computed: {
    title() {
      return `Delete ${this.modalName}`;
    }
  },

  methods: {
    open() {
      this.visible = true;
      this.loading = false;
      this.error = '';
    },

    close() {
      this.visible = false;
      this.error = '';
    },

    async submit() {
      this.loading = true;
      this.error = '';

      try {
        await this.onSubmit();
        this.close();
      } catch (err) {
        this.error = err.message || 'Unexpected error. Please try again.';
        console.error(err);
      } finally {
        this.loading = false;
      }
    },

    handleKeydown(event) {
      if (!this.visible) {
        return;
      }

      if (event.key === 'Escape' && !this.loading) {
        this.close();
      }
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

      <p class="mb-2">
        Are you sure you want to delete
        <strong>{{ itemName }}</strong>?
      </p>

      <p class="text-muted mb-0">
        This action cannot be undone.
      </p>

      <div
          v-if="error"
          class="alert alert-danger py-2 mt-3 mb-0"
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
            class="btn btn-sm btn-danger"
            :disabled="loading"
            @click="submit"
        >
          {{ loading ? 'Deleting...' : 'Delete' }}
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
  box-shadow: 0 20px 40px rgb(0 0 0 / 20%);
}

.modal-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>