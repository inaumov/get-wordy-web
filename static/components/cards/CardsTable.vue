<script>

export default {
  data() {
    return {
      cards: []
    }
  },
}

</script>

<template>
  <div class="container p-4" id="cards-table-panel">
    <table class="table">
      <thead>
      <tr>
        <th>Word</th>
        <th>Transcription</th>
        <th>Meaning</th>
        <th>Status</th>
        <th>Score</th>
        <th>Context</th>
        <th>Collocations</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="card in cards">
        <td>{{ card.word.value }} ({{ card.word.partOfSpeech }})</td>
        <td>{{ card.word.transcription }}</td>
        <td>{{ card.word.meaning }}</td>
        <td>{{ card.status }}</td>
        <td>{{ card.score }}</td>
        <td>
          <ul class="list-unstyled" v-if="card.contexts.length > 0">
            <li v-for="context in card.contexts">
              {{ context }}
            </li>
          </ul>
        </td>
        <td>
          <ul class="list-unstyled" v-if="card.collocations.length > 0">
            <li v-for="collocation in card.collocations">
              {{ collocation }}
            </li>
          </ul>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>
