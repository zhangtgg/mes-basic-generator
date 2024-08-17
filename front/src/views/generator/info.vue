<template>
	<el-card>
		<div class="btn">
			<el-button type="primary" @click="handleSave">保存</el-button>
			<el-button type="danger" @click="handleBack">返回</el-button>
			<el-button type="warning" :disabled="true">下载JAVA代码</el-button>
			<el-button type="success" @click="importHandle" :disabled="isImportButtonDisabled">导入</el-button>
		</div>
		<div class="form">
			<el-form :model="formInfo" :inline="true">
				<el-form-item label="模块">
					<el-input v-model="formInfo.moduleName"></el-input>
				</el-form-item>
				<el-form-item label="项目名称">
					<el-select v-model="formInfo.projectName" placeholder="请选择项目名称">
						<el-option label="mes" value="mes"></el-option>
						<el-option label="mes.basic" value="mes.basic"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="描述">
					<el-input v-model="formInfo.functionDescription"></el-input>
				</el-form-item>
				<el-form-item label="创建时间">
					<el-date-picker v-model="formInfo.createdAt" type="datetime" placeholder="Select date and time" disabled />
				</el-form-item>
			</el-form>
		</div>
		<div class="table">
			<el-table :data="genListDtlEntityList" stripe border>
				<el-table-column type="index" label="序号" width="80px"></el-table-column>
				<el-table-column v-for="header in headerList" :label="header.label">
					<template #default="{ row, $index }">
						<el-checkbox v-if="header.type === 'checkbox'" v-model="row[header.code]" :true-label="0" :false-label="1"  @change="handleCheckboxChange(row, header.code)"></el-checkbox>
						<span v-else>{{ row[header.code] }}</span>
					</template>
				</el-table-column>
			</el-table>
		</div>
	</el-card>
	<import ref="importRef" :form-info="formInfo" @refreshDataList="handleRefreshDataList"></import>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Import from './import.vue'
import service from '@/utils/request'
import { ElMessage } from 'element-plus'
// 路由
const router = useRouter()
const route = useRoute()
// 表单数据
const formInfo = ref({
	id: '',
	projectName: '',
	moduleName: '',
	functionDescription: '',
	createdAt: ''
})

// 表格数据
const genListDtlEntityList = ref([])
// 表头
const headerList = ref([
	{
		label: '主表',
		code: 'mainTableFlag',
		type: 'checkbox'
	},
	{
		label: '表名',
		code: 'tableName'
	}
])
// 导入Ref
const importRef = ref()

const selectedRow = ref(null)
// 导入初始化
const importHandle = id => {
	importRef.value.init(id)
}

// 详情
const detail = id => {
	service.get('/gen/list/queryDetail', { params: { id } }).then(res => {
		console.log(res, 'res')
		if (res.msg === 'success') {
			Object.keys(formInfo.value).forEach(key => {
				formInfo.value[key] = res.data[key]
			})
			genListDtlEntityList.value = res.data.genListDtlEntityList || []
		}
	})
}

// 返回
const handleBack = () => {
	router.push('/gen/generator/index')
}

// 保存
const handleSave = () => {
	const params = {
		...formInfo.value,
		genListDtlEntityList: genListDtlEntityList.value
	}

	service.post('/gen/list/save', params).then(res => {
		if (res.msg === 'success') {
			ElMessage.success('操作成功')
			handleBack()
		}
	})
}

onMounted(() => {
	if (route.query.id) {
		detail(route.query.id)
	}
})

const handleRefreshDataList = tableList => {
	genListDtlEntityList.value = genListDtlEntityList.value.concat(
		tableList.map(table => ({
		tableName: table.tableName,
		tableId: table.id
		}))
	)
		}
const handleCheckboxChange = (row, code) => {
	// 遍历所有行数据
	genListDtlEntityList.value.forEach(item => {
		if (item !== row) {
			// 将其他行的复选框设置为未选中 (1)
			item[code] = 1;
		}
	});

	// 更新当前选中的行的复选框设置为选中 (0)
	row[code] = 0;

	// 更新选中的行
	selectedRow.value = row;
};

// 计算属性：检查列表长度是否超过 2
const isImportButtonDisabled = computed(() => {
	return genListDtlEntityList.value.length > 1;
});
</script>

<style scoped lang="scss">
.form {
	margin-top: 20px;
}
</style>
