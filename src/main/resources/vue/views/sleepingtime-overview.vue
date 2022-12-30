<template id="sleepingtime-overview">
  <app-layout>
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6">
            SleepingTimes
          </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Add"
                    class="btn btn-info btn-simple btn-link"
                    @click="hideForm =!hideForm">
              <i class="fa fa-plus" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
      <div class="card-body" :class="{ 'd-none': hideForm}">
        <form id="addSleepingtime">
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-sleepingTime-startedAt">startedAt</span>
            </div>
            <input type="text" class="form-control" v-model="formData.startedAt" name="startedAt" placeholder="StartedAt"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-sleepingTime-deepSleepingTime">DeepSleepingTime</span>
            </div>
            <input type="text" class="form-control" v-model="formData.deepSleepingTime" name="deepSleepingTime" placeholder="DeepSleepingTime"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-sleepingTime-userId">User ID</span>
            </div>
            <input type="text" class="form-control" v-model="formData.userId" name="userId" placeholder="UserId"/>
          </div>
        </form>
        <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link" @click="addSleepingtime()">Add Sleeping Time</button>
      </div>
    </div>
    <div class="list-group list-group-flush">
      <div class="list-group-item d-flex align-items-start"
           v-for="(sleepingtime,index) in sleepingtimes" v-bind:key="index">
        <div class="mr-auto p-2">
          <span><a :href="`/sleepingtimes/${sleepingtime.id}`">User's ID {{sleepingtime.userId}}</a></span>
        </div>
        <div class="p2">
          <a :href="`/sleepingtimes/${sleepingtime.id}`">
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link">
              <i class="fa fa-pencil" aria-hidden="true"></i>
            </button>
            <button rel="tooltip" title="Delete" class="btn btn-info btn-simple btn-link"
                    @click="deleteSleepingtime(sleepingtime, index)">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </a>
        </div>
      </div>
    </div>
  </app-layout>
</template>


<script>
Vue.component("sleepingtime-overview", {
  template: "#sleepingtime-overview",
  data: () => ({
    sleepingtimes: [],
    formData: [],
    hideForm: true,
  }),
  created() {
    this.fetchSleepingtimes();
  },
  methods: {
    fetchSleepingtimes: function () {
      axios.get("/api/sleepingtimes")
          .then(res => this.sleepingtimes = res.data)
          .catch(() => alert("Error while fetching sleepingtimes"));
    },
    deleteSleepingtime: function (sleepingtime, index) {
      if (confirm('Are you sure you want to delete this sleepingtime? This action cannot be undone.', 'Warning')) {
        //sleepingtime confirmed delete
        const sleepingtimeId = sleepingtime.id;
        const url = `/api/sleepingtimes/${sleepingtimeId}`;
        axios.delete(url)
            .then(response =>
                //delete from the local state so Vue will reload list automatically
                this.sleepingtimes.splice(index, 1).push(response.data))
            .catch(function (error) {
              console.log(error)
            });
      }
    },
    addSleepingtime: function (){
      const url = `/api/sleepingtimes`;
      axios.post(url,
          {
            startedAt: this.formData.startedAt,
            deepSleepingTime: this.formData.deepSleepingTime,
            userId: this.formData.userId
          })
          .then(response => {
            this.sleepingtimes.push(response.data)
            this.hideForm= true;
          })
          .catch(error => {
            console.log(error)
          })
    }
  }
});
</script>