import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: MainLayout,
      redirect: '/home',
      children: [
        {
          path: 'runtime',
          component: () => import('@/views/Runtime.vue')
        },
        {
          path: 'home',
          component: () => import('@/views/Home.vue')
        },
        {
          path: 'ds/projects',
          component: () => import('@/views/ds/Projects.vue')
        },
        {
          path: 'ds/workflows',
          component: () => import('@/views/ds/Workflows.vue')
        },
        {
          path: 'seatunnel/tasks',
          component: () => import('@/views/seatunnel/Tasks.vue')
        },
        {
          path: 'seatunnel/instances',
          component: () => import('@/views/seatunnel/Instances.vue')
        },
        {
          path: 'seatunnel/virtual-tables',
          component: () => import('@/views/seatunnel/VirtualTables.vue')
        },
        {
          path: 'seatunnel/datasource',
          component: () => import('@/views/seatunnel/Datasource.vue')
        }
      ]
    }
  ]
})

export default router
