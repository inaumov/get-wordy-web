<script>
export default {
  name: 'RenameModal',

  props: {
    modalName: {
      type: String,
      required: true
    },
    currentName: {
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
      confirming: false,
      name: '',
      error: '',
      loading: false
    };
  },

  computed: {
    title() {
      return `Rename ${this.modalName}`;
    },
    normalizedName() {
      return this.name.trim();
    }
  },

  methods: {
    open() {
      this.visible = true;
      this.confirming = false;
      this.name = this.currentName;
      this.error = '';
      this.loading = false;

      this.$nextTick(() => {
        this.$refs.nameInput?.focus();
        this.$refs.nameInput?.select();
      });
    },

    close() {
      this.visible = false;
      this.confirming = false;
    },

    clearError() {
      this.error = '';
    },

    next() {
      const name = this.normalizedName;

      if (!name) {
        this.error = 'Name is required.';
        return;
      }

      if (name === this.currentName.trim()) {
        this.error = 'The new name must be different.';
        return;
      }

      this.error = '';
      this.confirming = true;
    },

    back() {
      if (this.loading) {
        return;
      }

      this.confirming = false;

      this.$nextTick(() => {
        this.$refs.nameInput?.focus();
        this.$refs.nameInput?.select();
      });
    },

    async submit() {
      this.loading = true;
      this.error = '';

      try {
        await this.onSubmit(this.normalizedName);
        this.close();
      } catch (err) {
        const status = err.status ?? err.response?.status;

        if (status === 409) {
          this.error = err.message || 'A record with this name already exists.';
          this.confirming = false;
        } else {
          this.error = err.message || 'Unexpected error. Please try again.';
          console.error(err);
          this.confirming = false;
        }
      } finally {
        this.loading = false;
      }
    },

    handleKeydown(event) {
      if (!this.visible) {
        return;
      }

      if (event.key === 'Escape') {
        this.close();
        return;
      }

      if (event.key === 'Enter') {
        if (this.confirming) {
          this.submit();
        } else {
          this.next();
        }
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

      <!-- step 1 -->
      <template v-if="!confirming">

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
            :disabled="loading"
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
              @click="next"
          >
            Continue
          </button>
        </div>

      </template>

      <!-- step 2 -->
      <template v-else>

        <h5 class="modal-title mb-3">
          Confirm rename
        </h5>

        <p class="mb-2">
          Are you sure you want to rename this {{ modalName }}?
        </p>

        <div class="rename-preview">
          <div class="old-name">
            {{ currentName }}
          </div>

          <div class="arrow">
            →
          </div>

          <div class="new-name">
            {{ normalizedName }}
          </div>
        </div>

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
              @click="back"
          >
            Back
          </button>

          <button
              class="btn btn-sm btn-primary"
              :disabled="loading"
              @click="submit"
          >
            {{ loading ? 'Renaming...' : 'Rename' }}
          </button>
        </div>

      </template>

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

.rename-preview {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  margin-top: 16px;
  background: #f8f9fa;
  border-radius: 6px;
  word-break: break-word;
}

.old-name {
  color: #6c757d;
}

.arrow {
  flex-shrink: 0;
}

.new-name {
  font-weight: 600;
}
</style>