<script>
import Header from "@/components/AppHeader.vue";
import Footer from "@/components/AppFooter.vue";
import NotFound from "@/views/error/NotFoundView.vue";

export default {
  components: {Header, Footer, NotFound},
};
</script>

<template>
  <div>
    <Header/>
    <main>
      <NotFound/>
    </main>
  </div>
</template>

<style>

</style>
