<template id="sleepingtime-profile">
  <app-layout>
    <div v-if="noSleepingytimeFound">
      <p> We're sorry, we were not able to retrieve this sleepingTime.</p>
      <p> View <a :href="'/sleepingtimes'">all sleepingtimes</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-if="!noSleepingtimeFound">
      <div class="card-header">
        <div class="row">
          <div class="col-6"> SleepingTime Profile </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Update"
                    class="btn btn-info btn-simple btn-link"
                    @click="updateSleepingtime()">
              <i class="far fa-save" aria-hidden="true"></i>
            </button>
            <button rel="tooltip" title="Delete"
                    class="btn btn-info btn-simple btn-link"
                    @click="deleteSleepingtime()">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
      <div class="card-body">
        <form>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-sleepingtime-id">SleepingTime ID</span>
            </div>
            <input type="number" class="form-control" v-model="sleepingtime.id" name="id" readonly placeholder="Id"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-sleepingtime-startedAt">Started At</span>
            </div>
            <input type="text" class="form-control" v-model="sleepingtime.startedAt" name="startedAt" placeholder="StartedAt"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-sleepingtime-deepSleepingtime">Deep Sleeping Time</span>
            </div>
            <input type="text" class="form-control" v-model="sleepingtime.deepSleepingTime" name="deepSleepingTime" placeholder="DeepSleepingTime"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-sleepingtime-userId">User ID</span>
            </div>
            <input type="text" class="form-control" v-model="sleepingtime.userId" name="userId" placeholder="UserId"/>
          </div>
        </form>
      </div>
    </div>
  </app-layout>
</template>

<script>
Vue.component("sleepingtime-profile", {
  template: "#sleepingtime-profile",
  data: () => ({
    sleepingtime: null,
    noSleepingtimeFound: false,
  }),
  created: function () {
    const sleepingtimeId = this.$javalin.pathParams["sleepingtime-id"];
    const url = `/api/sleepingtimes/${sleepingtimeId}`
    axios.get(url)
        .then(res => this.sleepingtime = res.data)
        .catch(error => {
          console.log("No sleepingTime found for id passed in the path parameter: " + error)
          this.noSleepingtimeFound = true
        });
  },
  methods: {
    updateSleepingtime: function () {
      const sleepingtimeId = this.$javalin.pathParams["sleepingtime-id"];
      const url = `/api/sleepingtimes/${sleepingtimeId}`
      axios.patch(url,
          {
            startedAt: this.sleepingtime.startedAt,
            deepSleepingTime: this.sleepingtime.deepSleepingTime,
            userId: this.sleepingtime.userId
          })
          .then(response =>
              this.sleepingtime.push(response.data))
          .catch(error => {
            console.log(error)
          })
      alert("SleepingTime updated!")
    },
    deleteSleepingtime: function () {
      if (confirm("Do you really want to delete?")) {
        const sleepingtimeId = this.$javalin.pathParams["sleepingtime-id"];
        const url = `/api/sleepingtimes/${sleepingtimeId}`
        axios.delete(url)
            .then(response => {
              alert("SleepingTime deleted")
              //display the /sleepingtimes endpoint
              window.location.href = '/sleepingtimes';
            })
            .catch(function (error) {
              console.log(error)
            });
      }
    }
  }
});
</script>