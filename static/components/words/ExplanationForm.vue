<script>
export default {
  name: "ExplanationForm",
  props: {
    model: {
      type: Object,
      default: {
        value: "",
        transcription: "",
        explanation: {
          partOfSpeech: "",
          meaning: "",
          collocations: [],
          inContext: []
        }
      }
    },
    onSubmit: {
      type: Function,
      required: true
    }
  },
  data() {
    return {
      parts: ["noun", "pronoun", "verb", "adjective", "adverb", "phrasal verb", "phrase"],
      config: {
        editForm: {
          formHeader: "Edit Explanation",
          btnName: "Update"
        },
        addForm: {
          formHeader: "Add Explanation",
          btnName: "Submit"
        }
      }
    };
  },
  computed: {
    labels() {
      return !!this.model ? this.config.editForm : this.config.addForm; // true if editing an existing entry
    },
  }
}
</script>

<template>
  <div class="p-4 d-flex flex-column align-items-start">
    <router-link :to="{ name: 'vocabulary' }" class="btn btn-secondary" title="Back">Back</router-link>
  </div>
  <div class="container p-4 d-flex justify-content-center">
    <div class="w-100" style="max-width: 800px;">
      <h2 class="mb-4">{{ labels.formHeader }}</h2>
      <form id="word-explanation-form" @submit.prevent="onSubmit">
        <!-- First row: Word and Part of Speech -->
        <div class="row mb-3">
          <div class="col-6">
            <label for="word" class="form-label">Word or a phrase<i>*</i></label>
            <input
                type="text"
                class="form-control"
                name="word"
                id="word"
                :value="model?.value || ''"
                autocomplete="off"
                required
            />
          </div>
          <div class="col-6">
            <label class="form-label">Part of Speech<i>*</i></label>
            <div id="radioGrp" class="d-flex flex-wrap">
              <div
                  v-for="(part, index) in parts"
                  :key="index"
                  class="form-check form-check-inline"
              >
                <input
                    type="radio"
                    class="form-check-input"
                    :value="part"
                    :id="part"
                    name="parts"
                    :checked="model?.explanation?.partOfSpeech === part"
                    required
                />
                <label
                    class="form-check-label"
                    :for="part"
                    style="text-transform: capitalize;"
                >
                  {{ part }}
                </label>
              </div>
            </div>
          </div>
        </div>

        <!-- Second row: Transcription -->
        <div class="row mb-3">
          <div class="col-12">
            <label for="transcription" class="form-label">Transcription</label>
            <input
                type="text"
                class="form-control"
                name="transcription"
                id="transcription"
                :value="model?.transcription || ''"
                autocomplete="off"
            />
            <small class="form-text text-muted">Provide the phonetic transcription if applicable.</small>
          </div>
        </div>

        <!-- Third row: Meaning -->
        <div class="row mb-3">
          <div class="col-12">
            <label for="meaning" class="form-label">Meaning<i>*</i></label>
            <input
                type="text"
                class="form-control"
                name="meaning"
                id="meaning"
                :value="model?.explanation?.meaning || ''"
                autocomplete="off"
                required
            />
            <small class="form-text text-muted">Provide the most relevant meaning of the word.</small>
          </div>
        </div>

        <!-- Fourth row: Collocations -->
        <div class="row mb-3">
          <div class="col-12">
            <label for="collocations" class="form-label">Collocations</label>
            <textarea
                class="form-control"
                rows="3"
                id="collocations"
                name="collocations"
                :value="(model?.explanation?.collocations || []).join('\n')"
                autocomplete="off">
            </textarea>
            <small class="form-text text-muted">Enter one collocation per line (e.g., "make a decision").</small>
          </div>
        </div>

        <!-- Fifth row: Usage in Context -->
        <div class="row mb-3">
          <div class="col-12">
            <label for="sentences" class="form-label">
              Usage in Context
            </label>
            <textarea
                class="form-control"
                rows="5"
                id="sentences"
                name="sentences"
                :value="(model?.explanation?.inContext || []).join('\n')"
                autocomplete="off">
            </textarea>
            <small class="form-text text-muted">Provide example sentences, one per line, for clarity.</small>
          </div>
        </div>

        <!-- Submit Button -->
        <div class="row py-4">
          <div class="col-12 text-end">
            <button type="submit" class="btn btn-primary">
              {{ labels.formHeader }}
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>

</style>