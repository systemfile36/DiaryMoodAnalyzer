import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

//import bootstrap
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.js'

//import vue-router 
import router from './router/router'

//import pinia for state management
import { createPinia } from 'pinia'
const pinia = createPinia();

createApp(App).use(router).use(pinia).mount('#app')
