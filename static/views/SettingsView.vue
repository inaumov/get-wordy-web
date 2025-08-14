<script>
import {fetchUserVocabularies, updateName, updatePicture, deleteVocabulary} from '@/js/dictionaries.js';

export default {
  data() {
    return {
      vocabularies: [],
      editingNameId: null,
      newName: '',
    }
  },
  methods: {
    async getData() {
      const response = await fetchUserVocabularies();
      let data = await response.json();
      this.vocabularies = data.filter(v => v.accessType === 'OWN');
    },
    startNameEdit(vocab) {
      this.editingNameId = vocab.vocabId;
      this.newName = vocab.name;
    },
    async saveName(vocab) {
      if (this.newName.trim() && this.newName !== vocab.name) {
        const res = await updateName(vocab.vocabId, this.newName.trim());
        if (res.ok) {
          vocab.name = this.newName.trim();
        }
      }
      this.editingNameId = null;
    },
    cancelEdit() {
      this.editingNameId = null;
    },
    async changePicture(vocab) {
      const newUrl = prompt("Enter new picture URL:", vocab.pictureUrl || '');
      if (newUrl !== null && newUrl !== vocab.pictureUrl) {
        const isClearing = newUrl.trim() === '';
        const res = await updatePicture(vocab.vocabId, newUrl.trim(), isClearing);
        if (res.ok) {
          vocab.pictureUrl = newUrl.trim();
        }
      }
    },
    async onDelete(vocab) {
      if (!confirm(`Delete vocabulary "${vocab.name}"? This action cannot be undone.`)) return;
      const res = await deleteVocabulary(vocab.vocabId);
      if (res.ok) {
        this.vocabularies = this.vocabularies.filter(v => v.vocabId !== vocab.vocabId);
      }
    },
    getColor(name) {
      let hash = 0;
      for (let i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash);
      }
      return `hsl(${Math.abs(hash) % 360}, 70%, 80%)`;
    },
    getFontSize(name) {
      const baseSize = 18; // default font size
      const length = name.length;
      if (length <= 8) return `${baseSize}px`;
      if (length <= 16) return `${baseSize - 2}px`;
      if (length <= 24) return `${baseSize - 4}px`;
      return `${baseSize - 6}px`; // very long names
    }
  },
  mounted() {
    this.getData();
  }
}
</script>

<template>
  <div class="p-4">
    <router-link :to="{ name: 'user-vocabularies' }" class="btn btn-secondary mb-4">← Back</router-link>
    <h4 class="mb-3">Manage My Vocabularies</h4>

    <div v-if="vocabularies.length" class="row g-3">
      <div v-for="vocab in vocabularies" :key="vocab.vocabId" class="col-md-4 col-sm-6">
        <div class="card h-100">
          <div class="m-3">
            <!-- picture or placeholder -->
            <img v-if="vocab.pictureUrl"
                 :src="vocab.pictureUrl"
                 class="card-img-top vocab-thumbnail me-3"
                 alt="Vocabulary Image"
                 title="Click to change picture"
                 @click="changePicture(vocab)"/>
            <div v-else
                 class="vocab-placeholder me-3"
                 :style="{backgroundColor: getColor(vocab.name), fontSize: getFontSize(vocab.name)}"
                 title="Click to change picture"
                 @click="changePicture(vocab)">
              <span class="ellipsis-multiline">{{ vocab.name }}</span>
            </div>
          </div>
          <div class="card-body">
            <div v-if="editingNameId === vocab.vocabId" class="d-flex justify-content-between gap-2 w-100">
              <input type="text"
                     v-model="newName"
                     class="inline-edit-input vocab-name-input me-1"
                     @keyup.enter="saveName(vocab)"
                     @keyup.esc="cancelEdit"/>
              <div class="d-flex gap-1">
                <button class="btn btn-success btn-sm" @click="saveName(vocab)">
                  <i class="bi bi-check-lg"></i>
                </button>
                <button class="btn btn-outline-secondary btn-sm" @click="cancelEdit">
                  <i class="bi bi-x-lg"></i>
                </button>
              </div>
            </div>
            <div v-else class="d-flex align-items-center justify-content-between w-100">
              <span class="card-title mb-0 h5 card-title-ellipsis">{{ vocab.name }}</span>
              <div class="d-flex gap-1">
                <button class="btn btn-outline-primary btn-sm" @click="startNameEdit(vocab)">
                  <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger" @click="onDelete(vocab)">
                  <i class="bi bi-trash"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="text-center mt-5">
      <p class="h4">You haven’t created any vocabularies yet.</p>
    </div>
  </div>
</template>

<style scoped>
.vocab-thumbnail,
.vocab-placeholder {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  cursor: pointer; /* show it's clickable */
  transition: transform 0.2s ease;
}

.vocab-thumbnail {
  object-fit: cover;
}

.vocab-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 8px;
  font-weight: bold;
  word-wrap: break-word;
  font-size: clamp(12px, 2.5vw, 18px);
  user-select: none;
}

.vocab-thumbnail:hover,
.vocab-placeholder:hover {
  transform: scale(1.05);
}

/* multi-line ellipsis */
.ellipsis-multiline {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.2;
  max-height: calc(1.2em * 2); /* keep height aligned with clamp */
}

.card-title-ellipsis {
  flex: 1;
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.inline-edit-input {
  border: none;
  border-bottom: 1px solid #ccc;
  background: transparent;
  font-size: 1.25rem;
  font-weight: 500;
  line-height: 1.5;
  padding: 0;
  margin: 0;
  height: auto;
  flex: 1;
}

.inline-edit-input:focus {
  outline: none;
  border-bottom: 1px solid #007bff; /* blue underline on focus */
}
.vocab-name-input {
  flex: 1;          /* take available space */
  min-width: 0;     /* allow shrinking */
  max-width: 60%;   /* keep buttons visible */
}
</style>
