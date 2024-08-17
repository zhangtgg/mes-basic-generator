<template>
	<el-card>
		<el-form :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item>
				<el-input v-model="state.queryForm.tableName" placeholder="表名"></el-input>
			</el-form-item>
			<el-form-item>
				<el-button @click="getDataList()">查询</el-button>
			</el-form-item>
			<el-form-item>
				<el-button type="primary" @click="handleAdd">新增</el-button>
			</el-form-item>
		</el-form>
		<el-table
			v-loading="state.dataListLoading"
			show-overflow-tooltip
			:data="state.dataList"
			border
			style="width: 100%"
			@selection-change="selectionChangeHandle"
		>
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="moduleName" label="模块名" header-align="center" align="center"></el-table-column>
			<el-table-column prop="projectName" label="项目名" header-align="center" align="center"></el-table-column>
			<el-table-column prop="functionDescription" label="描述" header-align="center" align="center"></el-table-column>

			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="300">
				<template #default="scope">
					<el-button type="primary" link @click="previewHandle(scope.row.id)">预览</el-button>
<!--					<el-button type="primary" link @click="generatorHandle(scope.row.id)">生成代码</el-button>-->
					<el-button type="primary" link @click="handleEdit(scope.row.id)">编辑</el-button>
					<el-button type="primary" link @click="deleteHandle(scope.row.id)">删除</el-button>
				</template>
			</el-table-column>
		</el-table>
		<el-pagination
			:current-page="state.page"
			:page-sizes="state.pageSizes"
			:page-size="state.limit"
			:total="state.total"
			layout="total, sizes, prev, pager, next, jumper"
			@size-change="sizeChangeHandle"
			@current-change="currentChangeHandle"
		>
		</el-pagination>

		<preview ref="previewRef" @refresh-data-list="getDataList"></preview>
		<edit ref="editRef" @refresh-data-list="getDataList"></edit>
		<generator ref="generatorRef" @refresh-data-list="getDataList"></generator>
	</el-card>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import { useCrud } from '@/hooks'
import Preview from './preview.vue'
import Generator from './generator.vue'
import { useTableSyncApi } from '@/api/table'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useDownloadApi } from '@/api/generator'
import { useRouter } from 'vue-router'
const router = useRouter()
const state: IHooksOptions = reactive({
	dataListUrl: '/gen/list/findList',
	deleteUrl: '/gen/list/delete',
	queryForm: {
		tableName: ''
	}
})

const editRef = ref()
const previewRef = ref()
const generatorRef = ref()

const editHandle = (id?: number) => {
	editRef.value.init(id)
}
const previewHandle = (tableId?: number) => {
	previewRef.value.init(tableId)
}
const generatorHandle = (id?: number) => {
	generatorRef.value.init(id)
}
const handleAdd = () => {
	router.push({ name: 'GeneratorInfo' })
}

const handleEdit = (id?: number) => {
	router.push({ name: 'GeneratorInfo', query: { id } })
}

const downloadBatchHandle = () => {
	const tableIds = state.dataListSelections ? state.dataListSelections : []

	if (tableIds.length === 0) {
		ElMessage.warning('请选择生成代码的表')
		return
	}

	useDownloadApi(tableIds)
}

const syncHandle = () => {
	ElMessageBox.confirm(`确定同步数据表吗?`, '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning'
	})
		.then(() => {
			useTableSyncApi(state.dataListSelections as number[]).then(() => {
				ElMessage.success('同步成功')
			})
		})
		.catch(() => {})
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteHandle } = useCrud(state)
</script>
