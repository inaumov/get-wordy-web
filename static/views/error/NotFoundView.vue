<script>

export default {
  mounted() {
  }
}
</script>

<template>
  <div id="error404" class="pt-5 d-flex justify-content-center">
    <div class="text-center">
      <h1 class="display-1 fw-bold">404</h1>
      <p class="fs-4"><span>Oops!</span> Page not found.</p>
      <p class="lead">
        The page you’re looking for does not exist
      </p>
      <router-link to="/" class="mt-4 btn btn-link">
        Back to main page
      </router-link>
    </div>
  </div>
</template>

<style>
div#error404 i, div#error404 span {
  color: rgb(185, 87, 84)
}
</style>
