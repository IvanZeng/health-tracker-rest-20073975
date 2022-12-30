<template id="bmi-profile">
  <app-layout>
    <div v-if="noBmiFound">
      <p> We're sorry, we were not able to retrieve this bmi.</p>
      <p> View <a :href="'/bmis'">all bmis</a>.</p>
    </div>
    <div class="card bg-light mb-3" v-if="!noBmiFound">
      <div class="card-header">
        <div class="row">
          <div class="col-6"> Bmi Profile </div>
          <div class="col" align="right">
            <button rel="tooltip" title="Update"
                    class="btn btn-info btn-simple btn-link"
                    @click="updateBmi()">
              <i class="far fa-save" aria-hidden="true"></i>
            </button>
            <button rel="tooltip" title="Delete"
                    class="btn btn-info btn-simple btn-link"
                    @click="deleteBmi()">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
      <div class="card-body">
        <form>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-bmi-id">Bmi ID</span>
            </div>
            <input type="number" class="form-control" v-model="bmi.id" name="id" readonly placeholder="Id"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-bmi-gender">Gender</span>
            </div>
            <input type="text" class="form-control" v-model="bmi.gender" name="gender" placeholder="Gender"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-bmi-height">Height</span>
            </div>
            <input type="text" class="form-control" v-model="bmi.height" name="height" placeholder="Height"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-bmi-weight">Weight</span>
            </div>
            <input type="text" class="form-control" v-model="bmi.weight" name="weight" placeholder="Weight"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-bmi-bmidata">BMI Data</span>
            </div>
            <input type="text" class="form-control" v-model="bmi.bmidata" name="bmidata" placeholder="Bmidata"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-bmi-userId">User ID</span>
            </div>
            <input type="text" class="form-control" v-model="bmi.userId" name="userId" placeholder="UserId"/>
          </div>
        </form>
      </div>
    </div>
  </app-layout>
</template>

<script>
Vue.component("bmi-profile", {
  template: "#bmi-profile",
  data: () => ({
    bmi: null,
    noBmiFound: false,
  }),
  created: function () {
    const bmiId = this.$javalin.pathParams["bmi-id"];
    const url = `/api/bmis/${bmiId}`
    axios.get(url)
        .then(res => this.bmi = res.data)
        .catch(error => {
          console.log("No bmi found for id passed in the path parameter: " + error)
          this.noBmiFound = true
        });
  },
  methods: {
    updateBmi: function () {
      const bmiId = this.$javalin.pathParams["bmi-id"];
      const url = `/api/bmis/${bmiId}`
      axios.patch(url,
          {
            gender: this.bmi.gender,
            height: this.bmi.height,
            weight: this.bmi.weight,
            bmidata: this.bmi.bmidata,
            userId: this.bmi.userId
          })
          .then(response =>
              this.bmi.push(response.data))
          .catch(error => {
            console.log(error)
          })
      alert("Bmi updated!")
    },
    deleteBmi: function () {
      if (confirm("Do you really want to delete?")) {
        const bmiId = this.$javalin.pathParams["bmi-id"];
        const url = `/api/bmis/${bmiId}`
        axios.delete(url)
            .then(response => {
              alert("Bmi deleted")
              //display the /bmis endpoint
              window.location.href = '/bmis';
            })
            .catch(function (error) {
              console.log(error)
            });
      }
    }
  }
});
</script>


