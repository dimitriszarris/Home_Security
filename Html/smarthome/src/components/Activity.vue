<template>
  <!-- <v-container>
    <v-row>
      <v-col
        cols="4"
        sm="12"
        md="4"
      >
        <v-menu
          v-model="menuFrom"
          :close-on-content-click="false"
          :nudge-right="40"
          color="blue"
          transition="scale-transition"
          offset-y
          min-width="290px"
        >
          <template v-slot:activator="{ on, attrs }">
            <v-text-field
              v-model="dateFrom"
              label="From date"
              prepend-icon="mdi-calendar"
              readonly
              v-bind="attrs"
              v-on="on"
            ></v-text-field>
          </template>
          <v-date-picker
            v-model="dateFrom"
            color="blue"
            @input="menuFrom = false"
          ></v-date-picker>
        </v-menu>
      </v-col>
      <v-col
        cols="4"
        sm="12"
        md="4"
      >
        <v-menu
          v-model="menuTo"
          :close-on-content-click="false"
          :nudge-right="40"
          transition="scale-transition"
          offset-y
          min-width="290px"
        >
          <template v-slot:activator="{ on, attrs }">
            <v-text-field
              v-model="dateTo"
              label="To date"
              prepend-icon="mdi-calendar"
              readonly
              v-bind="attrs"
              v-on="on"
            ></v-text-field>
          </template>
          <v-date-picker
            v-model="dateTo"
            color="blue"
            @input="menuTo = false"
          ></v-date-picker>
        </v-menu>
      </v-col>
      <v-row>
        <v-col cols="4" sm="12">
          <v-select
          v-model="sensorDescription"
          :items="doorSensors"
          chips
          label="Sensor Type"
          multiple
          outlined
          ></v-select>
        </v-col>
      </v-row>
    </v-row> -->
    <div>
      <apexchart type="line" height="350" :options="chartOptions" :series="series"></apexchart>
    </div>
  <!-- </v-container> -->
</template>

<script>
// import chartData from '../data/data.json';
import VueApexCharts from 'vue-apexcharts';

export default {
  components: {
    apexchart: VueApexCharts,
  },
  data: () => ({
    // dialog: true,
    // dateFrom: new Date().toISOString().substr(0, 10),
    // dateTo: new Date().toISOString().substr(0, 10),
    // fromDateFormatted: this.formatDate(new Date().toISOString().substr(0, 10)),
    // toDateFormatted: this.formatDate(new Date().toISOString().substr(0, 10)),
    // menuFromDate: false,
    // menuToDate: false,
    // menuFrom: false,
    // menuTo: false,
    // fromTime: null,
    // toTime: null,
    // menuFromTime: false,
    // menuToTime: false,
    // doorSensors: ['Environment', 'Door', 'Motion', 'Camera'],
    // sensorDescription: ['Main Entrance', 'Entrance', 'Window', 'Kitchen'],
    temperatureData: [],
    humidityData: [],
    airData: [],
    dateData: [],
    series: [{
      name: 'Temperature',
      type: 'column',
      data: [],
    },
    {
      name: 'Humidity',
      type: 'line',
      data: [],
    }, {
      name: 'Air Quality',
      type: 'area',
      data: [],
    },
    ],
    chartOptions: {
      chart: {
        height: 350,
        type: 'line',
      },
      stroke: {
        width: [0],
      },
      title: {
        text: 'Environment Sensors',
      },
      dataLabels: {
        enabled: true,
        enabledOnSeries: [0, 1, 2],
      },
      labels: [],
      xaxis: {
        type: 'time',
      },
      yaxis: [{
        title: {
          text: 'Temperature',
        },

      },
      {
        opposite: true,
        title: {
          text: 'Humidity',
        },
      },
      {
        opposite: true,
        title: {
          text: 'Air Quality',
        },
      },
      ],
    },
  }),

  created() {
    this.fetchSensorsData();
  },

  computed: {
    computedDateFormatted() {
      return this.formatDate(this.date);
    },
    loggedIn() {
      return this.$store.state.loggedIn;
    },
    alarmId() {
      return this.$store.state.alarmId;
    },
  },

  watch: {
    date(val) {
      this.dateFormatted = this.formatDate(val);
    },
    alarmId(newValue, oldValue) {
      // eslint-disable-next-line max-len
      // console.log(`watch: environmentSensors oldValue = ${oldValue}, newValue = ${JSON.stringify(newValue)}`);
      if (newValue !== oldValue) {
        this.fetchSensorsData();
      }
    },
  },

  methods: {
    formatDate(date) {
      if (!date) return null;

      const [year, month, day] = date.split('-');
      return `${year}-${month}-${day}`;
    },
    parseDate(date) {
      if (!date) return null;

      const [month, day, year] = date.split('-');
      return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;
    },
    updateChart() {
      const newSeries = this.series;
      newSeries[0].data = {
        data: this.temperatureData,
      };
      newSeries[1] = {
        data: this.humidityData,
      };

      this.series = [{
        data: this.temperatureData,
      }];
      console.log(`dates: ${this.dateData}`);

      this.chartOptions = {
        labels: this.dateData,
      };
    },
    fetchSensorsData() {
      if (this.alarmId === null) {
        return;
      }
      this.$store.dispatch('getSensorsValues')
        .then(async (response) => {
          const data = await response;
          this.temperatureData = [];
          this.humidityData = [];
          this.airData = [];
          this.series[0].data = [];
          for (let i = data.length - 12; i < data.length; i += 1) {
            console.log(`data[i]: ${JSON.stringify(data[i])}`);
            this.temperatureData.push(data[i].temperature.toFixed(1));
            this.humidityData.push(data[i].humidity.toFixed(0));
            this.airData.push(data[i].gas_value.toFixed(0));
            const date = new Date(data[i].updated_utc).toLocaleTimeString();

            this.dateData.push(date);
          }
          console.log(`data: ${this.temperatureData}`);
          this.updateChart();
        });
    },
  },
};
</script>
