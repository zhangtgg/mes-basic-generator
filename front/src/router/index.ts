import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'

export const menuRoutes: RouteRecordRaw[] = [
	{
		path: '/p/gen',
		meta: {
			title: '代码生成器',
			icon: 'icon-appstore'
		},
		children: [
			{
				path: '/gen/generator',
				name: 'Generator',
				redirect:'/gen/generator/index',
				component: () => import('../views/generator/main.vue'),

				meta: {
					title: '代码生成',
					icon: 'icon-fire',
				},
				children:[
					{
						path:'/gen/generator/index',
						name:'GeneratorIndex',
						component:() => import ('../views/generator/index.vue'),
						meta: {
							title: '代码生成',
							icon: 'icon-fire',
							hidden:true,
								parent:'/gen/generator'
						},
					},
					{
						path:'/gen/generator/info',
						name:'GeneratorInfo',
						component:() => import ('../views/generator/info.vue'),
						meta: {
							title: '代码生成',
							icon: 'icon-fire',
							hidden:true,
								parent:'/gen/generator'
						},

					}
				]
			},
			{
				path: '/gen/datasource',
				name: 'DataSource',
				component: () => import('../views/datasource/index.vue'),
				meta: {
					title: '数据源管理',
					icon: 'icon-database-fill'
				}
			},

		]
	}
]

export const constantRoutes: RouteRecordRaw[] = [
	{
		path: '/redirect',
		component: () => import('../layout/index.vue'),
		children: [
			{
				path: '/redirect/:path(.*)',
				component: () => import('../layout/components/Router/Redirect.vue')
			}
		]
	},
	{
		path: '/',
		component: () => import('../layout/index.vue'),
		redirect: '/gen/generator',
		children: [...menuRoutes]
	},
	{
		path: '/404',
		component: () => import('../views/404.vue')
	},
	{
		path: '/:pathMatch(.*)',
		redirect: '/404'
	}
]

export const router = createRouter({
	history: createWebHashHistory(),
	routes: constantRoutes
})
