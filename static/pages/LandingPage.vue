<script>
import Header from "@/components/AppHeader.vue";
import Footer from "@/components/AppFooter.vue";

export default {
  components: {Header, Footer},
};
</script>

<template>
  <div class="min-vh-100 d-flex flex-column justify-content-between">
    <Header/>
    <main>
      <div class="d-flex justify-content-center">
        <router-link to="/Dashboard" class="mt-4 btn btn-link">
          Get started
        </router-link>
      </div>
    </main>
    <Footer/>
  </div>
</template>

<style>

</style>
