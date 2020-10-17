import Vue from 'vue';
import VueRouter from 'vue-router';
import HomePage from '../components/HomePage.vue';
import UsersGroups from '../components/UsersGroups.vue';
import LoginPage from '../components/LoginPage.vue';
import Sensors from '../components/Sensors.vue';
import Cameras from '../components/Cameras.vue';

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'HomePage',
    component: HomePage,
  },
  {
    path: '/users',
    name: 'UsersGroups',
    component: UsersGroups,
  },
  {
    path: '/login',
    name: 'LoginPage',
    component: LoginPage,
  },
  {
    path: '/sensors',
    name: 'Sensors',
    component: Sensors,
  },
  {
    path: '/cameras',
    name: 'Cameras',
    component: Cameras,
  },
];

const router = new VueRouter({
  routes,
});

export default router;
