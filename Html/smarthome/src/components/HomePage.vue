<template>
  <v-container fluid fill-height>
    <v-row>
      <v-col>
        <v-card
          height="100%"
          >
            <v-card-title class="title-text justify-center">
              <div class="d-flex flex-column justify-space-between align-center">
                Security Alarm
              </div>
            </v-card-title>
        </v-card>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="8" xs="12">
        <v-card elevation="4">
          <v-card-title v-if="selectedAlarmObject != -1" class="active-alarm justify-center">
             {{alarmStateObjects[selectedAlarmObject].title}}
            <v-card-text v-if="alarmState != null">
              <div class="d-flex flex-column justify-space-between align-center">
                <v-img :src="alarmStateObjects[selectedAlarmObject].src"/>
              </div>
            </v-card-text>
            <v-card-text v-if="alarmState == null">
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
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-col cols="4" xs="6"
       v-for="(unselectedAlarm, index) in unselectedAlarmObjects" :key="index">
        <v-card v-if="unselectedAlarmObjects.length > 0"
           fill-height @click="arm(unselectedAlarm.alarmState)"
          elevation="4"
        >
          <v-card-title class="inactive-alarm justify-center">
            {{unselectedAlarm.title}}
            <v-card-text v-if="alarmState != null">
              <div class="d-flex flex-column justify-space-between align-center">
                <v-img :src="unselectedAlarm.src"/>
              </div>
            </v-card-text>
            <v-card-text v-if="alarmState == null">
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
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-card
          height="100%"
          >
            <v-card-title class="justify-center">
              <h2>
                Kitchen Sensors
              </h2>
            </v-card-title>
        </v-card>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="6" lg="4" md="4" sm="12" xs="12">
        <v-card
          elevation="4">
          <v-card-title class="justify-center">
            Temp (&#176;C)
            <v-card-text v-if="temperatureSeries[0] != null">
              <div id="chart" class="d-flex flex-column justify-space-between align-center">
                <apexchart :options="chartOptions" :series="temperatureSeries">
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
      </v-col>
      <v-col cols="6" lg="4" md="4" sm="12" xs="12">
        <v-card
          elevation="4">
          <v-card-title class="justify-center">
            Humidity
            <v-card-text v-if="humidity != null">
              <div class="d-flex flex-column justify-space-between align-center">
                <apexchart :options="chartOptions" :series="humiditySeries">
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
      </v-col>
      <v-col cols="6" lg="4" md="4" sm="12" x="12">
        <v-card
          elevation="4">
          <v-card-title class="justify-center">
            Air
            <v-card-text v-if="air != null">
              <div class="d-flex flex-column justify-space-between align-center">
                <v-progress-circular class="air-progress"
                  :rotate="180"
                  :size="170"
                  :width="50"
                  :value="airPercent"
                  :min="150"
                  :max="800"
                >
                  {{ air }}
                </v-progress-circular>
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
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import alarmStateObjects from '../data/alarmState';

export default {
  name: 'Main',

  data() {
    return {
      temperature: null,
      humidity: null,
      air: null,
      airPercent: null,
      temperatureSeries: [],
      humiditySeries: [],
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
      sensorId: 0,
      // alarmOn: null,
      // alarmState: null,
      // temHumAirSensors,
      // doorSensors,
      // movementSensors,
      selectedAlarmObject: -1,
      unselectedAlarmObjects: [],
      alarmStateObjects,
    };
  },

  created() {
    this.setSelectedAlarmState();
    this.getSensorsValues();
    setInterval(this.getSensorsValues, 1000);
    setInterval(this.setSelectedAlarmState, 100);
  },

  computed: {
    loggedIn() {
      return this.$store.state.loggedIn;
    },
    alarmId() {
      return this.$store.state.alarmId;
    },
    alarmState() {
      return this.$store.state.alarmState;
    },
    alarmIdLoaded() {
      return this.$store.state.alarmId > 0;
    },
  },

  methods: {

    setSelectedAlarmState() {
      if (this.alarmId === null || this.alarmId === 0) {
        return;
      }

      this.unselectedAlarmObjects = [];
      switch (this.alarmState) {
        case 0:
          this.selectedAlarmObject = 0;
          this.unselectedAlarmObjects.push({ ...alarmStateObjects[4] });
          this.unselectedAlarmObjects.push({ ...alarmStateObjects[5] });
          break;
        case 1:
          this.selectedAlarmObject = 1;
          this.unselectedAlarmObjects.push({ ...alarmStateObjects[3] });
          this.unselectedAlarmObjects.push({ ...alarmStateObjects[5] });
          break;
        case 2:
          this.selectedAlarmObject = 2;
          this.unselectedAlarmObjects.push({ ...alarmStateObjects[3] });
          this.unselectedAlarmObjects.push({ ...alarmStateObjects[4] });
          break;
        default:
      }
    },

    getSensorsValues() {
      if (this.alarmId === null || this.sensorId === null) return;

      const aId = parseInt(this.alarmId, 10);
      fetch(`http://interactivehome.ddns.net:8080/environment_sensor_state/${this.sensorId}?alarmId=${aId}`)
        .then(async (response) => {
          const data = await response.json();

          // check for error response
          if (!response.ok) {
          // get error message from body or default to response statusText
            const error = (data && data.message) || response.statusText;
            alert(error);
          }

          this.temperature = data[0].temperature;
          this.humidity = parseInt(data[0].humidity, 10);
          this.air = data[0].gas_value;
          this.airPercent = (100 * this.air) / 1024;
          this.temperatureSeries[0] = this.temperature;
          this.humiditySeries[0] = this.humidity;
        })
        .catch((error) => {
          this.errorMessage = error;
          console.error('There was an error!', error);
        });
    },

    arm(state) {
      if (state === this.alarmState) {
        return;
      }
      const data = { verification_activated: false, alarm_on: 0, alarm_state: state };

      fetch(`http://interactivehome.ddns.net:8080/alarm_system_state/${this.alarmId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      })
        .then(async (response) => {
          // check for error response
          if (!response.ok) {
          // get error message from body or default to response statusText
            const error = (response.json && response.json.message) || response.statusText;
            alert(error);
          }
        })
        .catch((error) => {
          console.error('Error:', error);
        });
    },
  },

  getImgUrl(alarmStateImg) {
    const images = require.context('../assets/', false, /\.png$/);
    return images(`./${alarmStateImg}.png`);
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.v-progress-circular {
  margin: 1rem;
  font-size: 250%;
}
.v-progress-circular.air-progress {
  color:yellow;
  font-size: 170%;
}
.title-text {
  font-size: 5vmin;
}
.active-alarm {
  font-size: 4.5vmin;
}
.inactive-alarm {
  font-size: 3vmin;
}
</style>
