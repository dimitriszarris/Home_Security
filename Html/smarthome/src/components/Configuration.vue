<template>
  <v-container fluid>
    <v-row>
      <v-radio-group
       class="radio-grp"
       label="Theme"
       v-model="themeGroup"
       @change="setTheme"
      >
        <v-radio label="Dark">
        </v-radio>
        <v-radio label="Light">
        </v-radio>
      </v-radio-group>
    </v-row>
    <v-row v-if="alarmId != null">
      <v-col cols="4" xl="3" lg="4" sm="12" v-for="(alarm, index) of alarmSystems" :key="index">
        <v-card @click="onChangeActiveAlarm(alarm.id)"
          elevation="4"
          :color="(alarm.id == alarmId) ? 'blue' : 'grey'"
        >
          <v-card-title class="justify-center">
            {{alarm.description}}
          </v-card-title>
        </v-card>
      </v-col>
    </v-row>
    <v-row v-show="!expandNewSensor">
      <v-col>
        <v-card
         class=""
         elevation="4"
         max-width="200"
        >
          <v-card-text>
            Find new sensor
          </v-card-text>
          <v-card-actions>
            <v-btn @click="findNewSensor()"
              color="darkgrey"
            >
              <v-icon color="blue">mdi-access-point-network</v-icon>
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
    <v-expand-transition>
      <v-col>
        <v-card id="sensor" class="sensor-values" v-show="expandNewSensor">
          <v-card-text>
            New Sensor
          </v-card-text>
          <v-row justify="end">
            <v-col>
              <v-tooltip top>
                <template v-slot:activator="{ on }">
                  <v-icon
                    :color="(newSensor.enabled === true) ? 'blue' : 'grey'"
                    v-on="on"
                  >mdi-access-point-network</v-icon>
                </template>
                <span v-if="newSensor.enabled">Sensor is Online</span>
                <span v-else>Sensor is Offline</span>
              </v-tooltip>
            </v-col>
          </v-row>
          <v-row>
          <v-col>
            <v-text-field
              label="Wifi SSID"
              class="text-field"
              maxlength="30"
              dense
              v-model="wifiSsid"
            ></v-text-field>
            <v-text-field
              label="Wifi Password"
              class="text-field"
              maxlength="30"
              dense
              v-model="wifiPassword"
            ></v-text-field>
            <v-text-field
              label="Sensor Id"
              class="text-field"
              readonly
              dense
              disabled
              :value="'Not created yet'"
            ></v-text-field>
            <v-text-field
              label="Created (UTC)"
              class="text-field"
              readonly
              dense
              disabled
              :value="'Not created yet'"
            ></v-text-field>
            <v-text-field
              label="Description"
              class="text-field"
              maxlength="30"
              dense
              :value="newSensor.description"
              v-model="newSensor.description"
            ></v-text-field>
            <v-text-field
              label="Device Identifier"
              class="text-field"
              maxlength="30"
              dense
              :value="newSensor.device_identifier"
              v-model="newSensor.device_identifier"
            ></v-text-field>
            <v-switch
              label="Enabled"
              v-model="newSensor.enabled"
              dense
              reverse
              class="vswitch"
              :checked="newSensor.enabled">
            </v-switch>
            <v-switch
              label="Battery Powered"
              v-model="newSensor.battery_powered"
              dense
              class="vswitch"
              :checked="newSensor.battery_powered">
            </v-switch>
            <v-switch v-if="activeTab == 'door_sensor'"
              label="Trigger verification process"
              v-model="newSensor.trigger_verification_process"
              dense
              reverse
              class="vswitch"
              :checked="newSensor.trigger_verification_process">
            </v-switch>
            <v-tooltip top>
              <template v-slot:activator="{ on }">
                <v-switch
                  v-on="on"
                  label="Arm In"
                  v-model="newSensor.arm_in"
                  dense
                  reverse
                  class="vswitch"
                  :checked="newSensor.arm_in">
                </v-switch>
              </template>
              <span>Enable/Disable when Alarm is in 'Alarm In' state</span>
            </v-tooltip>
            <v-tooltip top>
              <template v-slot:activator="{ on }">
                <v-switch
                  v-on="on"
                  label="Arm Away"
                  v-model="newSensor.arm_away"
                  dense
                  reverse
                  class="vswitch"
                  :checked="newSensor.arm_away">
                </v-switch>
              </template>
              <span>Enable/Disable when Alarm is in 'Alarm In' state</span>
            </v-tooltip>
          </v-col>
          </v-row>
          <v-row justify="end">
            <v-tooltip top>
              <template v-slot:activator="{ on, attrs }">
                <v-card
                  v-bind="attrs"
                  v-on="on"
                  class="card-button"
                  elevation="4"
                  @click="expandNewSensor = false">
                  <div
                  class="d-flex flex-column justify-space-between align-center">
                    <v-img src="../assets/cancel.png"/>
                  </div>
                </v-card>
              </template>
              <span>Cancel</span>
            </v-tooltip>
            <v-tooltip top>
              <template v-slot:activator="{ on, attrs }">
                <v-card
                  v-bind="attrs"
                  v-on="on"
                  class="card-button"
                  elevation="4"
                  @click="registerSensor(newSensor)">
                  <div
                  class="d-flex flex-column justify-space-between align-center">
                    <v-img src="../assets/check.png"/>
                  </div>
                </v-card>
              </template>
              <span>Register Sensor</span>
            </v-tooltip>
          </v-row>
        </v-card>
      </v-col>
    </v-expand-transition>
    <v-row>
      <v-col cols="12">
        <v-card class="title"
          height="100%"
          >
            <v-card-title class="title-text justify-center">
              Sensors
            </v-card-title>
        </v-card>
      </v-col>
    </v-row>
    <v-tabs align-with-title>
      <v-tabs-slider color="grey"></v-tabs-slider>
      <v-tab @click="onTabChange(environmentSensors, 'environment_sensor')">Environment</v-tab>
      <v-tab @click="onTabChange(doorSensors, 'door_sensor')">Door</v-tab>
      <v-tab @click="onTabChange(motionSensors, 'motion_sensor')">Motion</v-tab>
    </v-tabs>
    <div v-if="alarmId != null">
      <v-row v-for="(sensor, index) of sensors" :key="index">
        <v-col>
          <v-card id="sensor" class="sensor-values">
            <v-row justify="end">
              <v-col>
                <v-tooltip top>
                  <template v-slot:activator="{ on }">
                    <v-icon
                      :color="(sensor.enabled === true) ? 'blue' : 'grey'"
                      v-on="on"
                    >mdi-access-point-network</v-icon>
                  </template>
                  <span v-if="sensor.enabled">Sensor is Online</span>
                  <span v-else>Sensor is Offline</span>
                </v-tooltip>
              </v-col>
            </v-row>
            <v-row>
            <v-col>
              <v-text-field
                label="Sensor Id"
                class="text-field"
                readonly
                dense
                disabled
                :value="sensor.id"
              ></v-text-field>
              <v-text-field
                label="Created (UTC)"
                class="text-field"
                readonly
                dense
                disabled
                :value="Date(sensor.created_utc).toString()"
              ></v-text-field>
              <v-text-field
                label="Description"
                class="text-field"
                maxlength="30"
                dense
                :value="sensor.description"
                v-model="sensor.description"
              ></v-text-field>
              <v-text-field
                label="Device Identifier"
                class="text-field"
                maxlength="30"
                dense
                :value="sensor.device_identifier"
                v-model="sensor.device_identifier"
              ></v-text-field>
              <v-switch
                label="Enabled"
                v-model="sensor.enabled"
                dense
                reverse
                class="vswitch"
                :checked="sensor.enabled">
              </v-switch>
              <v-switch
                label="Battery Powered"
                v-model="sensor.battery_powered"
                dense
                class="vswitch"
                :checked="sensor.battery_powered">
              </v-switch>
              <v-switch v-if="activeTab == 'door_sensor'"
                label="Trigger verification process"
                v-model="sensor.trigger_verification_process"
                dense
                reverse
                class="vswitch"
                :checked="sensor.trigger_verification_process">
              </v-switch>
              <v-tooltip top>
                <template v-slot:activator="{ on }">
                  <v-switch
                    v-on="on"
                    label="Arm In"
                    v-model="sensor.arm_in"
                    dense
                    reverse
                    class="vswitch"
                    :checked="sensor.arm_in">
                  </v-switch>
                </template>
                <span>Enable/Disable when Alarm is in 'Alarm In' state</span>
              </v-tooltip>
              <v-tooltip top>
                <template v-slot:activator="{ on }">
                  <v-switch
                    v-on="on"
                    label="Arm Away"
                    v-model="sensor.arm_away"
                    dense
                    reverse
                    class="vswitch"
                    :checked="sensor.arm_away">
                  </v-switch>
                </template>
                <span>Enable/Disable when Alarm is in 'Alarm In' state</span>
              </v-tooltip>
            </v-col>
            </v-row>
            <v-row justify="end">
              <v-tooltip top>
                <template v-slot:activator="{ on, attrs }">
                  <v-card
                    v-bind="attrs"
                    v-on="on"
                    class="card-button"
                    elevation="4"
                    @click="deleteSensor(sensor.id)">
                    <div
                    class="d-flex flex-column justify-space-between align-center">
                      <v-img src="../assets/delete.png"/>
                    </div>
                  </v-card>
                </template>
                <span>Delete Sensor</span>
              </v-tooltip>
              <v-tooltip top>
                <template v-slot:activator="{ on, attrs }">
                  <v-card
                    v-bind="attrs"
                    v-on="on"
                    class="card-button"
                    elevation="4"
                    @click="undoChanges()">
                    <div
                    class="d-flex flex-column justify-space-between align-center">
                      <v-img src="../assets/undo.png"/>
                    </div>
                  </v-card>
                </template>
                <span>Undo Changes</span>
              </v-tooltip>
              <v-tooltip top>
                <template v-slot:activator="{ on, attrs }">
                  <v-card
                    v-bind="attrs"
                    v-on="on"
                    class="card-button"
                    elevation="4"
                    @click="saveChanges(sensor)">
                    <div
                    class="d-flex flex-column justify-space-between align-center">
                      <v-img src="../assets/check.png"/>
                    </div>
                  </v-card>
                </template>
                <span>Save Changes</span>
              </v-tooltip>
            </v-row>
          </v-card>
        </v-col>
      </v-row>
    </div>
  </v-container>
</template>

<script>
// eslint-disable-next-line import/no-duplicates
// import { v4 as uuidv4 } from 'uuid';
// eslint-disable-next-line import/no-duplicates
// import { parse as uuidParse } from 'uuid';

export default {
  name: 'Configuration',
  data() {
    return {
      activeAlarmId: this.alarmId,
      themeGroup: null,
      expandNewSensor: false,
      activeTab: 'environment_sensor',
      sensors: null,
      newSensor: {
        id: null,
        description: null,
        device_identifier: null,
        battery_powered: null,
        enabled: null,
        online: false,
        created_utc: null,
        trigger_verification_process: null,
        arm_in: null,
        arm_away: null,
      },
      gattServer: null,
      commandService: null,
      writeCharacteristic: null,
      alarmCharacteristicUuid: 'ea3e2887-c7a9-44d6-ba91-f6ddaec2a526',
      deviceCharacteristicUuid: 'cc88b0f2-e905-472c-86a7-8381d137d3d8',
      wifiSsidCharacteristicUuid: '81015940-d7a3-44a9-b5b1-4cd1cea1ddf2',
      wifiPasswordCharacteristicUuid: '47578d1f-be08-400a-b2ad-1abd7e1cd9b7',
      wifiSsid: null,
      wifiPassword: null,
      deviceId: null,
    };
  },

  created() {
    this.showSensors();

    const theme = localStorage.getItem('theme');
    if ((theme === null) || (theme != null && theme === 'dark')) {
      this.themeGroup = 0;
      this.$vuetify.theme.dark = true;
    } else if (theme != null && theme === 'light') {
      this.themeGroup = 1;
      this.$vuetify.theme.dark = false;
    }
  },

  computed: {
    loggedIn() {
      return this.$store.state.loggedIn;
    },
    alarmSystems() {
      return this.$store.state.alarmSystems;
    },
    alarmId() {
      return this.$store.state.alarmId;
    },
    environmentSensors() {
      return this.$store.state.environmentSensors;
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
      console.log(`watch: environmentSensors oldValue = ${oldValue}, newValue = ${JSON.stringify(newValue)}`);
      if (newValue !== null) {
        this.showSensors();
      }
    },
    doorSensors(newValue, oldValue) {
      console.log(`watch: doorSensors oldValue = ${oldValue}, newValue = ${JSON.stringify(newValue)}`);
      if (newValue !== null) {
        this.showSensors();
      }
    },
    motionSensors(newValue, oldValue) {
      console.log(`watch: motionSensors oldValue = ${oldValue}, newValue = ${JSON.stringify(newValue)}`);
      if (newValue !== null) {
        this.showSensors();
      }
    },
    activeTab(newValue, oldValue) {
      console.log(`watch: activeTab oldValue = ${oldValue}, newValue = ${JSON.stringify(newValue)}`);
      if (newValue !== null) {
        this.showSensors();
      }
    },
  },

  methods: {

    setTheme() {
      const isDarkTheme = this.themeGroup === 0;
      this.$vuetify.theme.dark = isDarkTheme;
      localStorage.removeItem('theme');
      localStorage.setItem('theme', isDarkTheme ? 'dark' : 'light');
    },

    setDeviceCharacteristicValue() {
      let deviceIdArrBuff;
      this.commandService.getCharacteristic(this.deviceCharacteristicUuid)
        .then((characteristic) => {
          console.log('Found write characteristic: ', characteristic);
          this.writeCharacteristic = characteristic;
          const strDeviceId = this.deviceId.toString();
          const deviceIdLen = new ArrayBuffer(strDeviceId.length);
          deviceIdArrBuff = new Uint8Array(deviceIdLen);
          for (let i = 0; i < strDeviceId.length; i += 1) {
            deviceIdArrBuff[i] = strDeviceId.charCodeAt(i);
          }
          this.writeCharacteristic.writeValue(deviceIdArrBuff);
        // })
        // .catch((error) => {
        //   console.log('DOMException: GATT operation already in progress.', error);
        //   return Promise.resolve()
        //     .then(() => {
        //       setTimeout(() => {
        //       }, 500);
        //     })
        //     .then(() => {
        //       this.writeCharacteristic.writeValue(deviceIdArrBuff);
        //     });
        });
    },

    setAlarmCharacteristicValue() {
      let alarmIdArrBuff;
      this.commandService.getCharacteristic(this.alarmCharacteristicUuid)
        .then((characteristic) => {
          console.log('Found write characteristic: ', characteristic);
          this.writeCharacteristic = characteristic;
          console.log(this.alarmId);
          const strAlarmId = this.alarmId.toString();
          const alarmIdLen = new ArrayBuffer(strAlarmId.length);
          alarmIdArrBuff = new Uint8Array(alarmIdLen);
          for (let i = 0; i < strAlarmId.length; i += 1) {
            alarmIdArrBuff[i] = strAlarmId.charCodeAt(i);
          }
          this.writeCharacteristic.writeValue(alarmIdArrBuff);
        // })
        // .catch((error) => {
        //   console.log('DOMException: GATT operation already in progress.', error);
        //   return Promise.resolve()
        //     .then(() => {
        //       setTimeout(() => {
        //       }, 500);
        //     })
        //     .then(() => {
        //       this.writeCharacteristic.writeValue(alarmIdArrBuff);
        //     });
        });
    },

    setWifiCharacteristicValues() {
      let ssidArrBuff;
      this.commandService.getCharacteristic(this.wifiSsidCharacteristicUuid)
        .then((characteristic) => {
          console.log('Found write characteristic: ', characteristic);
          this.writeCharacteristic = characteristic;
          console.log(this.wifiSsid);
          // eslint-disable-next-line max-len
          // const wifiSsidBufferLen = new ArrayBuffer(this.wifiSsid.length * 2); // 2 bytes for each char (UTF16). For UTF32 should be *4
          // ssidArrBuff = new Uint16Array(wifiSsidBufferLen);
          const wifiSsidBufferLen = new ArrayBuffer(this.wifiSsid.length);
          ssidArrBuff = new Uint8Array(wifiSsidBufferLen);
          for (let i = 0; i < this.wifiSsid.length; i += 1) {
            ssidArrBuff[i] = this.wifiSsid.charCodeAt(i);
          }
          this.writeCharacteristic.writeValue(ssidArrBuff)
            .then(() => {
              this.setWifiPasswordCharacteristicValue();
            });
        // })
        // .catch((error) => {
        //   console.log('DOMException: GATT operation already in progress.', error);
        //   return Promise.resolve()
        //     .then(() => {
        //       setTimeout(() => {
        //       }, 500);
        //     })
        //     .then(() => {
        //       this.writeCharacteristic.writeValue(ssidArrBuff)
        //         .then(this.setWifiPasswordCharacteristicValue());
        //     });
        });
    },

    setWifiPasswordCharacteristicValue() {
      let wifiPasswordArrBuff;
      this.commandService.getCharacteristic(this.wifiPasswordCharacteristicUuid)
        .then((characteristic) => {
          console.log('Found write characteristic: ', characteristic);
          this.writeCharacteristic = characteristic;
          console.log(this.wifiPassword);
          const wifiPasswordBufferLen = new ArrayBuffer(this.wifiPassword.length);
          wifiPasswordArrBuff = new Uint8Array(wifiPasswordBufferLen);
          for (let i = 0; i < this.wifiPassword.length; i += 1) {
            wifiPasswordArrBuff[i] = this.wifiPassword.charCodeAt(i);
          }
          console.log(wifiPasswordArrBuff);
          this.writeCharacteristic.writeValue(wifiPasswordArrBuff);
        });
      // .catch((error) => {
      //   console.log('DOMException: GATT operation already in progress.', error);
      //   return Promise.resolve()
      //     .then(() => {
      //       setTimeout(() => {
      //       }, 500);
      //     })
      //     .then(() => {
      //       this.writeCharacteristic.writeValue(wifiPasswordArrBuff);
      //     });
      // });
    },

    async findNewSensor() {
      if (this.gattServer != null && this.gattServer.connected) {
        if (this.gattServer.disconnect) {
          console.log('Disconnecting...');
          this.gattServer.disconnect();
          this.expandNewSensor = false;
        }
      } else {
        console.log('Connecting...');
        if (this.writeCharacteristic == null) {
          console.log('Requesting device with service 2714f485-9888-46b8-a119-98586cb867a3');
          navigator.bluetooth.requestDevice({
            filters: [
              // { services: ['heart_rate'] },
              // { services: [0x1802, 0x1803] },
              // { services: ['2714f485-9888-46b8-a119-98586cb867a3'] },
              // { name: 'SmartHome New Sensor' },
              { namePrefix: 'SmartHome' },
            ],
            optionalServices: ['2714f485-9888-46b8-a119-98586cb867a3'],
          })
            .then((device) => {
              this.expandNewSensor = true;
              console.log('Device connected: ', device);
              console.log('Connecting to GATT Server...');
              return device.gatt.connect();
            })
            .then((server) => {
              console.log('> Found GATT server: ', server);
              this.gattServer = server;
              // Get command service
              return this.gattServer.getPrimaryService('2714f485-9888-46b8-a119-98586cb867a3');
            })
            .then((service) => {
              console.log('> Found command service: ', service);
              this.commandService = service;
              this.setAlarmCharacteristicValue();
            })
            .catch((error) => {
              console.error('Error: ', error);
            });
        }
      }
    },

    getAlarmSystems() {
      this.setActiveAlarmId();
      this.storeActiveAlarmId();
      this.getSensors();
    },

    onChangeActiveAlarm(alarmId) {
      this.$store.commit('setAlarmId', alarmId);
      console.log(`onChangeActiveAlarm: ${alarmId}`);
      this.getSensors();
      this.showSensors();
    },

    onTabChange(tab) {
      this.activeTab = tab;
    },

    showSensors() {
      switch (this.activeTab) {
        case 'environment_sensor':
          this.sensors = this.environmentSensors;
          break;
        case 'door_sensor':
          this.sensors = this.doorSensors;
          break;
        case 'motion_sensor':
          this.sensors = this.motionSensors;
          break;
        default:
          break;
      }
    },

    setActiveAlarmId() {
      let i = 0; // The alarm Ids start from 1
      for (; i < this.alarmSystems.length; i += 1) {
        if (this.alarmId === this.alarmSystems[i].id) {
          // The radio button is zero-based, the alarm_id starts from 1
          this.activeAlarmId = this.alarmId;
          break;
        }
      }
    },

    storeActiveAlarmId() {
      localStorage.removeItem('alarmId');
      localStorage.setItem('alarmId', this.activeAlarmId);
      this.$store.commit('setAlarmId', this.activeAlarmId);
      this.getSensors();
    },

    undoAlarmChanges() {
      this.getSensors();
    },

    saveAlarmChanges(editedAlarm) {
      // eslint-disable-next-line no-restricted-globals
      if (!confirm('Are you sure you want to update this sensor?')) { return; }

      const requestBody = {
        description: editedAlarm.description,
      };

      fetch(`http://interactivehome.ddns.net:8080/${editedAlarm.id}`,
        {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(requestBody),
        })
        .then(async (response) => {
          const data = await response.json();

          // check for error response
          if (!response.ok) {
          // get error message from body or default to response statusText
            const error = (data && data.message) || response.statusText;
            alert(error);
          }
        })
        .then(this.getSensors());
    },

    getSensors() {
      console.log(`getSensors: ${this.alarmId}`);
      fetch(`http://interactivehome.ddns.net:8080/environment_sensor?alarmId=${this.alarmId}`)
        .then(async (response) => {
          const data = await response.json();

          // check for error response
          if (!response.ok) {
            // get error message from body or default to response statusText
            const error = (data && data.message) || response.statusText;
            alert(error);
          }

          this.$store.commit('setEnvironmentSensors', data);
        })
        .catch((error) => {
          this.errorMessage = error;
          console.error('There was an error!', error);
        });

      fetch(`http://interactivehome.ddns.net:8080/door_sensor?alarmId=${this.alarmId}`)
        .then(async (response) => {
          const data = await response.json();

          // check for error response
          if (!response.ok) {
            // get error message from body or default to response statusText
            const error = (data && data.message) || response.statusText;
            alert(error);
          }

          this.$store.commit('setDoorSensors', data);
        })
        .catch((error) => {
          this.errorMessage = error;
          console.error('There was an error!', error);
        });

      fetch(`http://interactivehome.ddns.net:8080/motion_sensor?alarmId=${this.alarmId}`)
        .then(async (response) => {
          const data = await response.json();

          // check for error response
          if (!response.ok) {
            // get error message from body or default to response statusText
            const error = (data && data.message) || response.statusText;
            alert(error);
          }

          this.$store.commit('setMotionSensors', data);
        })
        .catch((error) => {
          this.errorMessage = error;
          console.error('There was an error!', error);
        });

      /*
      fetch(`http://interactivehome.ddns.net:8080/window_sensor?alarmId=${this.alarmId}`)
        .then(async (response) => {
          const data = await response.json();

          // check for error response
          if (!response.ok) {
            // get error message from body or default to response statusText
            const error = (data && data.message) || response.statusText;
            alert(error);
          }

          this.$store.commit('setWindowSensors', data);
        })
        .catch((error) => {
          this.errorMessage = error;
          console.error('There was an error!', error);
        });

      fetch(`http://interactivehome.ddns.net:8080/sound_sensor?alarmId=${this.alarmId}`)
        .then(async (response) => {
          const data = await response.json();

          // check for error response
          if (!response.ok) {
            // get error message from body or default to response statusText
            const error = (data && data.message) || response.statusText;
            alert(error);
          }

          this.$store.commit('setSoundSensors', data);
        })
        .catch((error) => {
          this.errorMessage = error;
          console.error('There was an error!', error);
        });
        */
    },

    undoChanges() {
      this.getSensors();
    },

    saveChanges(editedSensor) {
      // eslint-disable-next-line no-restricted-globals
      if (!confirm('Are you sure you want to update this sensor?')) { return; }

      const requestBody = {
        description: editedSensor.description,
        device_identifier: editedSensor.device_identifier,
        enabled: editedSensor.enabled,
        battery_powered: editedSensor.battery_powered,
        trigger_verification_process: editedSensor.trigger_verification_process,
        arm_in: editedSensor.arm_in,
        arm_away: editedSensor.arm_away,
      };

      fetch(`http://interactivehome.ddns.net:8080/${this.activeTab}/${editedSensor.id}?alarmId=${this.alarmId}`,
        {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(requestBody),
        })
        .then(async (response) => {
          const data = await response.json();

          // check for error response
          if (!response.ok) {
          // get error message from body or default to response statusText
            const error = (data && data.message) || response.statusText;
            alert(error);
          }
        })
        .then(this.getSensors(true));
    },

    deleteSensor(id) {
      // eslint-disable-next-line no-restricted-globals
      if (!confirm(`Are you sure you want to delete sensor with id ${id}?`)) { return; }

      fetch(
        `http://interactivehome.ddns.net:8080/${this.activeTab}/${id}?alarmId=${this.alarmId}`,
        { method: 'delete' },
      )
        .then(async (response) => {
          const data = await response;
          // check for error response
          if (!data.ok) {
          // get error message from body or default to response statusText
            const error = (data && data.message) || data.statusText;
            alert(error);
          } else {
            const sensors = this.environmentSensors;
            for (let i = 0; i < sensors.length; i += 1) {
              if (sensors[i].id === id) {
                sensors.splice(i, 1);
                this.$store.commit('setEnvironmentSensors', sensors);
              }
            }
          }
        });
    },

    registerSensor(sensor) {
      console.log(sensor);
      if (this.wifiSsid === null || this.wifiPassword === null
           || !this.wifiSsid.length || !this.wifiPassword.length) {
        alert('Please fill in all mandatory fields for the new Sensor');
        return;
      }
      this.setWifiCharacteristicValues();
      const requestBody = {
        description: sensor.description,
        device_identifier: sensor.device_identifier,
        enabled: sensor.enabled,
        battery_powered: sensor.battery_powered,
        trigger_verification_process: sensor.trigger_verification_process,
        arm_in: sensor.arm_in,
        arm_away: sensor.arm_away,
      };

      fetch(`http://interactivehome.ddns.net:8080/${this.activeTab}?alarmId=${this.alarmId}`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(requestBody),
        })
        .then(async (response) => {
          const data = await response.json();

          // check for error response
          if (!response.ok) {
          // get error message from body or default to response statusText
            const error = (data && data.message) || response.statusText;
            alert(error);
          }
          this.deviceId = data;
          console.log('device id data: ', data);
          console.log('DeviceId: ', this.deviceId);
          this.setDeviceCharacteristicValue();
        })
        .then(() => {
          this.expandNewSensor = false;
          this.newSensor.id = this.deviceId;
          this.newSensor.description = sensor.description;
          this.newSensor.device_identifier = sensor.device_identifier;
          this.newSensor.battery_powered = sensor.battery_powered;
          this.newSensor.enabled = sensor.enabled;
          this.newSensor.online = false;
          this.newSensor.created_utc = sensor.created_utc;
          this.newSensor.trigger_verification_process = sensor.trigger_verification_process;
          this.newSensor.arm_in = sensor.arm_in;
          this.newSensor.arm_away = sensor.arm_away;
          const sensors = this.environmentSensors;
          sensors.push(this.newSensor);
          this.$store.commit('setEnvironmentSensors', sensors);
        });
    },
  },
};
</script>

<style scoped>
.v-progress-circular {
  margin: 1rem;
  font-size: 250%;
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
.radio-grp {
  margin-left: 3em;
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
