<template id="bmi-overview">
  <div>
    <div>
      <ul class="bmi-overview-list">
        <li v-for="bmi in bmis">
          <a :href="`/bmis/${bmi.id}`">User ID:{{bmi.userId}}</a>
        </li>
      </ul>
    </div>
  </div>
</template>
<script>
Vue.component("bmi-overview", {
  template: "#bmi-overview",
  data: () => ({
    bmis: [],
  }),
  created() {
    this.fetchUsers();
  },
  methods: {
    fetchUsers: function () {
      axios.get("/api/bmis")
          .then(res => this.bmis = res.data)
          .catch(() => alert("Error while fetching bmis"));
    }
  }
});
</script>