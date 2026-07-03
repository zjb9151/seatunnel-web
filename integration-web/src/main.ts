import { createApp } from 'vue'
import { createI18n } from 'vue-i18n'
import naive from 'naive-ui'
import App from './App.vue'
import router from './router'

const i18n = createI18n({
  legacy: false,
  locale: 'zh',
  messages: {
    zh: {
      menu: {
        home: '首页',
        ds_projects: '项目管理',
        ds_workflows: '工作流',
        st_tasks: '任务定义',
        st_instances: '任务实例',
        st_virtual_tables: '虚拟表',
        st_datasource: '数据源'
      }
    },
    en: {
      menu: {
        home: 'Home',
        ds_projects: 'Projects',
        ds_workflows: 'Workflows',
        st_tasks: 'Task Definitions',
        st_instances: 'Task Instances',
        st_virtual_tables: 'Virtual Tables',
        st_datasource: 'Datasources'
      }
    }
  }
})

createApp(App).use(router).use(i18n).use(naive).mount('#app')
