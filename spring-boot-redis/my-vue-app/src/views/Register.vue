<template>
  <div class="box">
    <h2>用户注册</h2>

    <!-- 1. 用真变量 mounted 控制渲染 -->
    <el-form
      v-if="mounted"
      :model="form"
      :rules="rules"
      ref="formRef"
      label-width="80px"
    >
      <el-form-item label="账号" prop="account">
        <el-input v-model="form.account" />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input type="password" v-model="form.password" />
      </el-form-item>

      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" />
      </el-form-item>

      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submit">注册</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { RegisterRequest } from '@/types/api'
import { authApi } from '@/api/auth'          // 2. 正确的注册 api

/* ---------- 响应式数据 ---------- */
const router = useRouter()
const mounted = ref(false)                    // 3. 控制 DOM 渲染
const formRef = ref<FormInstance>()           // 4. 表单实例

const form = reactive<RegisterRequest>({
  account: '',
  password: '',
  nickname: '',
  phone: ''
})

const rules: FormRules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ]
}

/* ---------- 生命周期 ---------- */
onMounted(() => {
  mounted.value = true      // 5. DOM 已挂载，再渲染表单
})

/* ---------- 提交方法 ---------- */
const submit = () => {
  const formEl = formRef.value
  if (!formEl) return        // 6. 空值保护

  formEl.validate(async (valid) => {
    if (!valid) return
    try {
      const res = await authApi.register(form)
      if (res.code === 0 || res.msg?.includes('成功')) {
        ElMessage.success('注册成功')
        formEl.resetFields()
        router.push('/login')
      } else {
        ElMessage.error(res.msg || '注册失败')
      }
    } catch (e: any) {
      ElMessage.error(e.response?.data?.msg || '网络错误')
    }
  })
}
</script>

<style scoped>
.box {
  width: 400px;
  margin: 60px auto;
  padding: 20px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
}
</style>