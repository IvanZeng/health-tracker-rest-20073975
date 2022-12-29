<template id="activity-profile">
  <div>
    <form v-if="activity">
      <label class="col-form-label">Activity ID: </label>
      <input class="form-control" v-model="activity.id" name="id" type="number" readonly/><br>
      <label class="col-form-label">Description: </label>
      <input class="form-control" v-model="activity.description" name="description" type="text"/><br>
      <label class="col-form-label">Duration (mins): </label>
      <input class="form-control" v-model="activity.duration" name="duration" type="text"/><br>
      <label class="col-form-label">Calories: </label>
      <input class="form-control" v-model="activity.calories" name="calories" type="text"/><br>
      <label class="col-form-label">Started Time: </label>
      <input class="form-control" v-model="activity.started" name="started" type="text"/><br>
    </form>
    <dt v-if="activity">
    </dt>
  </div>
</template>

<script>
Vue.component("activity-profile", {
  template: "#activity-profile",
  data: () => ({
    activity: null
  }),
  created: function () {
    const activityId = this.$javalin.pathParams["activity-id"];
    const url = `/api/activities/${activityId}`
    axios.get(url)
        .then(res => this.activity = res.data)
        .catch(() => alert("Error while fetching activity" + activityId));
  }
});
</script>