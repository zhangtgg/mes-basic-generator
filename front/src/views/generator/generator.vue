<template>
	<el-dialog v-model="visible" title="生成代码" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="dataForm"  label-width="120px">
<!--			<el-row>-->
<!--				<el-col :span="12">-->
<!--					<el-form-item label="表名" prop="tableName">-->
<!--						<el-input v-model="dataForm.tableName" disabled placeholder="表名"></el-input>-->
<!--					</el-form-item>-->
<!--				</el-col>-->
<!--				<el-col :span="12">-->
<!--					<el-form-item label="说明" prop="tableComment">-->
<!--						<el-input v-model="dataForm.tableComment" disabled placeholder="说明"></el-input>-->
<!--					</el-form-item>-->
<!--				</el-col>-->
<!--			</el-row>-->
<!--			<el-row>-->
<!--				<el-col :span="12">-->
<!--					<el-form-item label="模块名" prop="moduleName">-->
<!--						<el-input v-model="dataForm.moduleName" disabled placeholder="模块名"></el-input>-->
<!--					</el-form-item>-->
<!--				</el-col>-->
<!--				<el-col :span="12">-->
<!--					<el-form-item label="功能名" prop="functionName">-->
<!--						<el-input v-model="dataForm.functionName" disabled placeholder="功能名"></el-input>-->
<!--					</el-form-item>-->
<!--				</el-col>-->
<!--			</el-row>-->
			<el-row>
				<el-col :span="12">
					<el-form-item label="项目名" prop="packageName">
						<el-input v-model="dataForm.packageName" :disabled = true placeholder="项目类型"></el-input>
					</el-form-item>
				</el-col>
			</el-row>
			<el-form-item label="后端生成路径" prop="backendPath">
				<el-input v-model="dataForm.backendPath" placeholder="后端生成路径"></el-input>
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" @click="submitHandle()">保存</el-button>
			<el-button type="danger" @click="generatorHandle()">生成代码</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es'
import { useGeneratorApi, useDownloadApi } from '@/api/generator'
import { useTableApi, useTableSubmitApi } from '@/api/table'

const emit = defineEmits(['refreshDataList'])

const visible = ref(false)
const dataFormRef = ref()
const baseClassList = ref<any[]>([])
const dataForm = reactive({
	id: '',
	genListId: '',
	baseclassId: '',
	generatorType: 1,
	formLayout: 1,
	backendPath: '',
	frontendPath: '',
	packageName: '',
	email: '',
	author: '',
	version: '',
	moduleName: '',
	functionName: '',
	className: '',
	tableComment: '',
	tableName: ''
})

const init = (id: any) => {
	visible.value = true
	dataForm.id = id

	// 重置表单数据
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	getTable(id)
}


const getTable = (id: number) => {
	useTableApi(id).then(res => {
		Object.assign(dataForm, res.data)
	})
}

// 保存
const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}

		useTableSubmitApi(dataForm).then(() => {
			ElMessage.success({
				message: '操作成功',
				duration: 500,
				onClose: () => {
					visible.value = false
					emit('refreshDataList')
				}
			})
		})
	})
}

// 生成代码
const generatorHandle = () => {
	dataFormRef.value.validate(async (valid: boolean) => {
		if (!valid) {
			return false
		}

		// 先保存
		await useTableSubmitApi(dataForm)

		// 生成代码，自定义路径
		useGeneratorApi([dataForm.genListId]).then(() => {
			ElMessage.success({
				message: '操作成功',
				duration: 500,
				onClose: () => {
					visible.value = false
					emit('refreshDataList')
				}
			})
		})
	})
}

defineExpose({
	init
})
</script>

<style lang="scss" scoped>
.generator-code .el-dialog__body {
	padding: 15px 30px 0 20px;
}
</style>
