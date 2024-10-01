import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

//import bootstrap
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.js'

//import vue-router 
import router from './router/router'

createApp(App).use(router).mount('#app')
