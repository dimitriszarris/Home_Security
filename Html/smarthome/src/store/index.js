import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    alarmId: null,
    alarmOn: false,
    alarmState: null,
    loggedIn: false,
    username: null,
    alarmSystems: [],
    environmentSensors: [],
    environmentSensorsValues: [],
    doorSensors: [],
    motionSensors: [],
    windowSensors: [],
    soundSensors: [],
  },
  mutations: {
    setAlarmSystems(state, alarmSystems) {
      state.alarmSystems = alarmSystems;
    },
    setAlarmId(state, activeAlarmId) {
      state.alarmId = activeAlarmId;
    },
    setAlarmState(state, alarmState) {
      state.alarmState = alarmState;
    },
    setAlarmOn(state, alarmOn) {
      state.alarmOn = alarmOn;
    },
    setLoggedIn(state, isLoggedIn) {
      state.loggedIn = isLoggedIn;
    },
    setUsername(state, user) {
      state.username = user;
    },
    setEnvironmentSensors(state, sensors) {
      state.environmentSensors = sensors;
    },
    setEnvironmentSensorsValues(state, sensorsValues) {
      state.environmentSensorsValues = sensorsValues;
    },
    setDoorSensors(state, sensors) {
      state.doorSensors = sensors;
    },
    setMotionSensors(state, sensors) {
      state.motionSensors = sensors;
    },
    setWindowSensors(state, sensors) {
      state.windowSensors = sensors;
    },
    setSoundSensors(state, sensors) {
      state.soundSensors = sensors;
    },
  },
  getters: {
    getAlarmId: (state) => state.alarmId,
    getEnvironmentSensorsValues: (state) => state.environmentSensorsValues,
  },
  actions: {
    fetchEnvironmentSensors({ commit }, alarmId) {
      fetch(`http://interactivehome.ddns.net:8080/environment_sensor?alarmId=${alarmId}`)
        .then(async (response) => {
          const data = await response.json();
          commit('setEnvironmentSensors', data);
        });
    },
    getSensorsValues({ getters }) {
      console.log(`getSensorsValues: ${getters.getAlarmId}`);
      return new Promise((resolve) => {
        fetch(`http://interactivehome.ddns.net:8080/environment_sensor_state/4?alarmId=${getters.getAlarmId}`)
          .then(async (response) => {
            const data = await response.json();
            resolve(data);
          });
      });
    },
    // fetchEnvironmentSensorsValues({ commit, getters }, sensorsIds) {
    //   const sensorsValues = getters.getEnvironmentSensorsValues;
    //   for (let i = 0; i < sensorsIds.length; i += 1) {
    //     console.log(`fetchEnvironmentSensorsValues for id ${sensorsIds[i]} index ${i}`);
    //     fetch(`http://interactivehome.ddns.net:8080/environment_sensor_state/${sensorsIds[i]}?alarmId=${getters.getAlarmId}`)
    //       .then(async (response) => {
    //         const data = await response.json();
    //         let found = false;
    //         console.log(`data: ${JSON.stringify(data[0])}`);
    //         console.log(`sensorsValues.length: ${sensorsValues.length}`);
    //         for (let j = 0; j < sensorsValues.length; j += 1) {
    // eslint-disable-next-line max-len
    //           console.log(`data.id === sensorsValues[j].id - ${data[i].id} === ${sensorsValues[j].id}`);
    //           if (data[i].id === sensorsValues[j].id) {
    //             sensorsValues.splice(j, 1, data[i]);
    //             found = true;
    //             break;
    //           }
    //         }
    //         if (!found) {
    //           console.log(`sensorsValues push: ${JSON.stringify(sensorsValues)}`);
    // eslint-disable-next-line max-len
    //           console.log(`getters.getEnvironmentSensorsValues: ${JSON.stringify(getters.getEnvironmentSensorsValues)}`);
    //           console.log(`data2: ${JSON.stringify(data[i])}`);
    //           sensorsValues.push({ ...data[0] });
    //         }
    //       })
    //       .catch((e) => {
    //         console.error(`error: ${e}`);
    //       });
    //   }
    //   console.log(`sensorsValues Before commit: ${JSON.stringify(sensorsValues)}`);
    //   commit('setEnvironmentSensorsValues', sensorsValues);
    // },
  },

  modules: {
  },
});
