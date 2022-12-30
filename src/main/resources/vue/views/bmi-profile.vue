<template id="bmi-profile">
  <div>
    <form v-if="bmi">
      <label class="col-form-label">BMI ID: </label>
      <input class="form-control" v-model="bmi.id" name="id" type="number" readonly/><br>
      <label class="col-form-label">Gender: </label>
      <input class="form-control" v-model="bmi.gender" name="gender" type="text"/><br>
      <label class="col-form-label">Height: </label>
      <input class="form-control" v-model="bmi.height" name="height" type="text"/><br>
      <label class="col-form-label">Weight: </label>
      <input class="form-control" v-model="bmi.weight" name="weight" type="text"/><br>
      <label class="col-form-label">BMI Data: </label>
      <input class="form-control" v-model="bmi.bmidata" name="bmidata" type="text"/><br>
    </form>
    <dt v-if="bmi">
    </dt>
  </div>
</template>

<script>
Vue.component("bmi-profile", {
  template: "#bmi-profile",
  data: () => ({
    bmi: null
  }),
  created: function () {
    const bmiId = this.$javalin.pathParams["bmi-id"];
    const url = `/api/bmis/${bmiId}`
    axios.get(url)
        .then(res => this.bmi = res.data)
        .catch(() => alert("Error while fetching bmi" + bmiId));
  }
});
</script>

