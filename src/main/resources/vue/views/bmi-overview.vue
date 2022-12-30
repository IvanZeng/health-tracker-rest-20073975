<template id="bmi-overview">
  <app-layout>
    <div class="card bg-light mb-3">
      <div class="card-header">
        <div class="row">
          <div class="col-6">
            Bmis
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
        <form id="addBmi">
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-bmi-gender">Gender</span>
            </div>
            <input type="text" class="form-control" v-model="formData.gender" name="gender" placeholder="Gender"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-bmi-height">Height</span>
            </div>
            <input type="text" class="form-control" v-model="formData.height" name="height" placeholder="Height"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-bmi-weight">Weight</span>
            </div>
            <input type="text" class="form-control" v-model="formData.weight" name="weight" placeholder="Weight"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-bmi-bmidata">Bmidata</span>
            </div>
            <input type="text" class="form-control" v-model="formData.bmidata" name="bmidata" placeholder="Bmidata"/>
          </div>
          <div class="input-group mb-3">
            <div class="input-group-prepend">
              <span class="input-group-text" id="input-bmi-userId">User ID</span>
            </div>
            <input type="text" class="form-control" v-model="formData.userId" name="userId" placeholder="UserId"/>
          </div>
        </form>
        <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link" @click="addBmi()">Add Bmi</button>
      </div>
    </div>
    <div class="list-group list-group-flush">
      <div class="list-group-item d-flex align-items-start"
           v-for="(bmi,index) in bmis" v-bind:key="index">
        <div class="mr-auto p-2">
          <span><a :href="`/bmis/${bmi.id}`">User's ID {{bmi.userId}}</a></span>
        </div>
        <div class="p2">
          <a :href="`/bmis/${bmi.id}`">
            <button rel="tooltip" title="Update" class="btn btn-info btn-simple btn-link">
              <i class="fa fa-pencil" aria-hidden="true"></i>
            </button>
            <button rel="tooltip" title="Delete" class="btn btn-info btn-simple btn-link"
                    @click="deleteBmi(bmi, index)">
              <i class="fas fa-trash" aria-hidden="true"></i>
            </button>
          </a>
        </div>
      </div>
    </div>
  </app-layout>
</template>


<script>
Vue.component("bmi-overview", {
  template: "#bmi-overview",
  data: () => ({
    bmis: [],
    formData: [],
    hideForm: true,
  }),
  created() {
    this.fetchBmis();
  },
  methods: {
    fetchBmis: function () {
      axios.get("/api/bmis")
          .then(res => this.bmis = res.data)
          .catch(() => alert("Error while fetching bmis"));
    },
    deleteBmi: function (bmi, index) {
      if (confirm('Are you sure you want to delete this bmi? This action cannot be undone.', 'Warning')) {
        //bmi confirmed delete
        const bmiId = bmi.id;
        const url = `/api/bmis/${bmiId}`;
        axios.delete(url)
            .then(response =>
                //delete from the local state so Vue will reload list automatically
                this.bmis.splice(index, 1).push(response.data))
            .catch(function (error) {
              console.log(error)
            });
      }
    },
    addBmi: function (){
      const url = `/api/bmis`;
      axios.post(url,
          {
            gender: this.formData.gender,
            height: this.formData.height,
            weight: this.formData.weight,
            bmidata: this.formData.bmidata,
            userId: this.formData.userId
          })
          .then(response => {
            this.bmis.push(response.data)
            this.hideForm= true;
          })
          .catch(error => {
            console.log(error)
          })
    }
  }
});
</script>