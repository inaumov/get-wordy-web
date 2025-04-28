<script>

export default {
  props: ['items'],
  data() {
    return {
      // items: []
    }
  },
  methods: {
  }
}

</script>

<template>
  <div class="p-4">

    <table class="table">
      <thead>
      <tr>
        <th>Word</th>
        <th>Transcription</th>
        <th>Meaning</th>
        <th>In Context</th>
        <th style="text-align: right">Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="item in items">
        <td>{{ item.value }} ({{ item.explanation?.partOfSpeech }})</td>
        <td>{{ item.transcription }}</td>
        <td>{{ item.explanation?.meaning }}</td>
        <td>
          <ul class="list-unstyled" v-if="item.explanation?.inContext">
            <li v-for="sentence in item.explanation?.inContext">
              {{ sentence }}
            </li>
          </ul>
        </td>
        <td style="text-align: right">
          <div id="actions">
            <slot name="actions" :row="item"></slot>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<style>

table #actions {
  position: relative;
  display: inline-flex;
}

table #actions .btn {
  padding: 0 5px !important;
}

</style>
