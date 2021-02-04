/* eslint-disable max-len */
<template>
  <v-container fluid>
    <v-tabs align-with-title>
      <v-tabs-slider color="grey"></v-tabs-slider>
      <v-tab @click="onTabChange(environmentSensors, 'environment_sensor_state')">
        Environment
      </v-tab>
      <v-tab @click="onTabChange(doorSensors, 'door_sensor_state')">Door</v-tab>
      <v-tab @click="onTabChange(motionSensors, 'motion_sensor_state')">Motion</v-tab>
    </v-tabs>
    <div v-if="alarmId != null">
      <div v-for="sensorValues of sensorsValues" :key="sensorValues.id">
        <v-row>
          <v-col>
            <v-card
              height="100%"
              >
                <v-card-title class="justify-center">
                  <h2>
                    {{sensorValues.description}}
                  </h2>
                </v-card-title>
            </v-card>
          </v-col>
        </v-row>
        <v-row>
          <v-col xl3 lg4 md4 sm12 xs12>
            <v-card
              elevation="4">
              <v-card-title class="justify-center">
                Temp (&#176;C)
              <!-- <v-card-text v-if="sensorValues.temperatureSeries != null &&
               sensorValues.temperatureSeries[0] != null"> -->
              <v-card-text v-if="sensorValues.temperature != null">
                <div class="d-flex flex-column justify-space-between align-center">
                  <v-progress-circular class="air-progress"
                    :rotate="180"
                    :size="170"
                    :width="50"
                    :value="sensorValues.temperature"
                    :min="150"
                    :max="800"
                  >
                    {{ sensorValues.temperature }}
                  </v-progress-circular>
                  <!-- <apexchart :options="chartOptions" :series="sensorValues.temperatureSeries">
                  </apexchart> -->
                </div>
              </v-card-text>
              <v-card-text v-else>
                <div class="d-flex flex-column justify-space-between align-center">
                  <v-progress-circular
                    :size="170"
                    :width="10"
                    color="grey"
                    indeterminate
                  ></v-progress-circular>
                </div>
              </v-card-text>
              </v-card-title>
            </v-card>
          </v-col>
        <!-- <v-col cols="6" lg="4" md="4" sm="12" xs="12">
          <v-card
            elevation="4">
            <v-card-title class="justify-center">
              Temp (&#176;C)
              <v-card-text v-if="sensor.temperatureSeries[0] != null">
                <div class="d-flex flex-column justify-space-between align-center">
                  <apexchart :options="chartOptions" :series="sensor.temperatureSeries">
                  </apexchart>
                </div>
              </v-card-text>
              <v-card-text v-else>
                <div class="d-flex flex-column justify-space-between align-center">
                  <v-progress-circular
                    :size="70"
                    :width="7"
                    color="grey"
                    indeterminate
                  ></v-progress-circular>
                </div>
              </v-card-text>
            </v-card-title>
          </v-card>
        </v-col> -->
        <v-col xl3 lg4 md4 sm12 xs12>
          <v-card
            elevation="4">
            <v-card-title class="justify-center">
              Humidity
              <v-card-text v-if="sensorValues.humidity != null">
                <div class="d-flex flex-column justify-space-between align-center">
                  <v-progress-circular class="air-progress"
                    :rotate="180"
                    :size="170"
                    :width="50"
                    :value="sensorValues.humidity"
                    :min="150"
                    :max="800"
                  >
                    {{ sensorValues.humidity }}
                  </v-progress-circular>
                  <!-- <apexchart :options="chartOptions" :series="sensor.humiditySeries">
                  </apexchart> -->
                </div>
              </v-card-text>
              <v-card-text v-else>
                <div class="d-flex flex-column justify-space-between align-center">
                  <v-progress-circular
                    :size="170"
                    :width="10"
                    color="grey"
                    indeterminate
                  ></v-progress-circular>
                </div>
              </v-card-text>
            </v-card-title>
          </v-card>
        </v-col>
        <v-col xl3 lg4 md4 sm12 xs12>
          <v-card
            elevation="4">
            <v-card-title class="justify-center">
              Air
              <v-card-text v-if="sensorValues.air != null">
                <div class="d-flex flex-column justify-space-between align-center">
                  <v-progress-circular class="air-progress"
                    :rotate="180"
                    :size="170"
                    :width="50"
                    :value="sensorValues.airPercent"
                    :min="150"
                    :max="800"
                  >
                    {{ sensorValues.air }}
                  </v-progress-circular>
                </div>
              </v-card-text>
              <v-card-text v-else>
                <div class="d-flex flex-column justify-space-between align-center">
                  <v-progress-circular
                    :size="170"
                    :width="10"
                    color="grey"
                    indeterminate
                  ></v-progress-circular>
                </div>
              </v-card-text>
            </v-card-title>
          </v-card>
        </v-col>
        </v-row>
      </div>
    </div>
  </v-container>
</template>

<script>

export default {
  name: 'Sensors',

  data() {
    return {
      activeTab: 'environment_sensor_state',
      environmentSensorValues: {
        id: null,
        description: null,
        temperature: null,
        temperatureSeries: [],
        humiditySeries: [],
        pressure: null,
        altitude: null,
        air: null,
        airPercent: null,
      },
      chartOptions: {
        chart: {
          type: 'radialBar',
          offsetY: 0,
          sparkline: {
            enabled: true,
          },
        },
        plotOptions: {
          radialBar: {
            startAngle: -90,
            endAngle: 90,
            track: {
              // background: '#e7e7e7',
              // strokeWidth: '97%',
              margin: 0, // margin is in pixels
              dropShadow: {
                enabled: false,
                top: 0,
                left: 0,
                color: '#999',
                opacity: 1,
                blur: 1,
              },
            },
            dataLabels: {
              name: {
                show: false,
              },
              value: {
                offsetY: 0,
                fontSize: '22px',
                color: '#2196F3',
              },
              total: {
                show: true,
                label: 'Total',
                color: '#2196F3',
              },
            },
          },
        },
        grid: {
          padding: {
            top: -10,
          },
        },
        fill: {
          type: 'gradient',
          gradient: {
            shade: 'light',
            shadeIntensity: 0.4,
            inverseColors: false,
            opacityFrom: 1,
            opacityTo: 1,
            stops: [0, 50, 53, 91],
          },
        },
        labels: [/* 'Average Results' */],
      },
      sensorsData: [],
      sensorDataLst: [],
      sensors: [],
      sensorsValues: [],
      sensorsIds: [],
    };
  },

  created() {
    this.showSensors();
    this.getSensorsValues();
    setInterval(() => {
      this.getSensorsValues();
    }, 5000);
  },

  computed: {
    loggedIn() {
      return this.$store.state.loggedIn;
    },
    alarmId() {
      return this.$store.state.alarmId;
    },
    environmentSensors() {
      return this.$store.state.environmentSensors;
    },
    environmentSensorsValues() {
      return this.$store.state.environmentSensorsValues;
    },
    doorSensors() {
      return this.$store.state.doorSensors;
    },
    motionSensors() {
      return this.$store.state.motionSensors;
    },
  },

  watch: {
    // alarmId(newValue, oldValue) {
    //   console.log(`watch: alarm id oldValue = ${oldValue}, newValue = ${newValue}`);
    // },
    environmentSensors(newValue, oldValue) {
      // eslint-disable-next-line max-len
      // console.log(`watch: environmentSensors oldValue = ${oldValue}, newValue = ${JSON.stringify(newValue)}`);
      if (newValue !== oldValue) {
        this.showSensors();
      }
    },
    environmentSensorsValues(newValue, oldValue) {
      // eslint-disable-next-line max-len
      // console.log(`environmentSensorsValues \noldValue: ${JSON.stringify(oldValue)},\nnewValue: ${JSON.stringify(newValue)}`);
      if (newValue !== oldValue && newValue !== null) {
        // console.log(`environmentSensorsValues: ${JSON.stringify(newValue)}`);
        // this.updateSensorsValues(newValue);
      }
    },
    doorSensors(newValue, oldValue) {
      // eslint-disable-next-line max-len
      // console.log(`watch: doorSensors oldValue = ${oldValue}, newValue = ${JSON.stringify(newValue)}`);
      if (newValue !== oldValue) {
        this.showSensors();
      }
    },
    motionSensors(newValue, oldValue) {
      // eslint-disable-next-line max-len
      // console.log(`watch: motionSensors oldValue = ${oldValue}, newValue = ${JSON.stringify(newValue)}`);
      if (newValue !== oldValue) {
        this.showSensors();
      }
    },
    activeTab(newValue, oldValue) {
      // eslint-disable-next-line max-len
      // console.log(`watch: activeTab oldValue = ${oldValue}, newValue = ${JSON.stringify(newValue)}`);
      if (newValue !== oldValue) {
        this.showSensors();
      }
    },
  },

  methods: {

    onTabChange(sensorList, tab) {
      this.sensors = sensorList;
      this.activeTab = tab;
    },

    isSensorShown(id) {
      for (let i = 0; i < this.sensors.length
       && this.sensorsValues != null
       && i < this.sensorsValues.length; i += 1) {
        if (this.sensorsValues[i].id === id) {
          console.log(`sensor shown: ${id}`);
          return true;
        }
      }
      console.log(`sensor not shown: ${id}`);
      return false;
    },

    showSensors() {
      switch (this.activeTab) {
        case 'environment_sensor_state':
          this.sensors = this.environmentSensors;
          this.sensorsIds = [];
          for (let i = 0; i < this.sensors.length; i += 1) {
            this.environmentSensorValues.id = this.sensors[i].id;
            this.sensorsIds.push(this.sensors[i].id);
            this.environmentSensorValues.description = this.sensors[i].description;
            if (this.isSensorShown(this.sensors[i].id) !== true) {
              console.log(`Adding sensor: ${this.environmentSensorValues.id}`);
              this.sensorsValues.push({ ...this.environmentSensorValues });
              this.$store.commit('setEnvironmentSensorsValues', this.sensorsValues);
            }
          }
          break;
        case 'door_sensor_state':
          this.sensors = this.doorSensors;
          break;
        case 'motion_sensor_state':
          this.sensors = this.motionSensors;
          break;
        default:
          break;
      }
    },

    updateSensors() {
      this.sensors = this.environmentSensors;
      this.sensorsIds = [];
      for (let i = 0; i < this.sensors.length; i += 1) {
        this.environmentSensorValues.id = this.sensors[i].id;
        this.sensorsIds.push(this.sensors[i].id);
        this.environmentSensorValues.description = this.sensors[i].description;
        if (this.isSensorShown(this.sensors[i].id) !== true) {
          console.log(`Adding sensor: ${this.environmentSensorValues.id}`);
          this.sensorsValues.push({ ...this.environmentSensorValues });
          this.$store.commit('setEnvironmentSensorsValues', this.sensorsValues);
        }
      }
    },

    updateSensorsValues(values) {
      const val = values;
      if (val === null && val.length > 0) {
        return;
      }
      for (let i = 0; i < val.length; i += 1) {
        for (let j = 0; j < this.sensorsValues.length; j += 1) {
          // console.log(`sensorsValues : ${this.sensorsValues[i].id}`);
          // console.log(`values[j] : ${values[j].id}`);
          if (this.sensorsValues[j] !== null
              && val !== null
              && this.sensorsValues[j].id === val[i].id) {
            val[i].temperature = val[i].temperature.toFixed(1);
            val[i].humidity = val[i].humidity.toFixed(1);
            val[i].air = val[i].gas_value;
            val[i].airPercent = (val[i].gas_value * 100) / 4095;
            console.log(`measurements: ${JSON.stringify(val[i])}`);
            this.sensorsValues.splice(j, 1, val[i]);
            break;
          }
        }
      }
    },

    getSensorsValues() {
      if (this.alarmId === null || this.sensorsValues === null || !this.sensorsValues.length) {
        return;
      }
      this.fetchEnvironmentSensorsValues()
        .then((response) => {
          this.updateSensorsValues(response);
        });
    },
    async fetchEnvironmentSensorsValues() {
      const url = `http://interactivehome.ddns.net:8080/environment_sensor_state?alarmId=${this.alarmId}`;
      const response = await fetch(url);
      const data = await response.json();
      return data;
    },
  },
};

</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.v-progress-circular {
  margin: 1rem;
  font-size: 250%;
}
.v-progress-circular.temperature-progress {
  color:green;
  font-size: 170%;
}
.v-progress-circular.humidity-progress {
  color:aqua;
  font-size: 170%;
}
.v-progress-circular.air-progress {
  color:yellow;
  font-size: 170%;
}
.title {
  margin-left: 1em;
  margin-right: 1em;
}
.title-text {
  font-size: 5vmin;
}
.button-style {
  margin: .1em;
}
.card-button {
  margin: 1em;
  margin-left: .4em;
  margin-right: 2em;
  padding: .4em;
  padding-left: 2em;
  padding-right: 2em;
}

.input {
  border-style: solid;
  border-width: .1em;
  border-color: #ccc;
  display: inline-block;
  white-space: nowrap;
  width: 80%;
  overflow: hidden;
  text-overflow:ellipsis;
  cursor: text;
  color: #ccc;
}
.label {
  display: inline-block;
  white-space: nowrap;
  width: 80%;
  overflow: hidden;
  text-overflow:ellipsis;
  cursor: text;
  color: #ccc;
}
.sensor-values {
  font-size: .8em;
  border-radius: .5em;
  padding-left: 1em;
  margin-left: 3em;
  margin-right: 3em;
  width: 80%;
}
.switch {
  position: absolute;
  margin: .1em;
  width: 2em;
  height: 1em;
}
.switch input {
  opacity: 0;
  width: 0em;
  height: 0em;
}

.text-field {
  margin: .8em;
  margin-right: 10vmin;
  padding-left: 2vmin;
}

.vswitch {
  margin: -.8em;
  padding-left: 3.5em;
  padding-right: 25vmin;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0em;
  left: 0em;
  right: 0em;
  bottom: 0em;
  background-color: #ccc;
  -webkit-transition: .4s;
  transition: .4s;
}

.slider:before {
  position: absolute;
  content: "";
  height: 1em;
  width: 1em;
  left: 0em;
  bottom: 0em;
  background-color: white;
  -webkit-transition: .4s;
  transition: .4s;
}

input:checked + .slider {
  background-color: #2196F3;
}

input:focus + .slider {
  box-shadow: 0 0 1px #2196F3;
}

input:checked + .slider:before {
  -webkit-transform: translateX(1em);
  -ms-transform: translateX(1em);
  transform: translateX(1em);
}

/* Rounded sliders */
.slider.round {
  border-radius: 2vmin;
}

.slider.round:before {
  border-radius: 50%;
}

</style>
