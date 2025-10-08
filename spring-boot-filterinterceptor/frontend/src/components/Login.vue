<template>
  <div class="container">
    <h1>用户接口测试</h1>
    <!-- 登录表单 -->
    <div class="card">
      <h2>1. 登录接口</h2>
      <div class="form-group">
        <label>用户名：</label>
        <input v-model="loginForm.username" placeholder="请输入用户名" type="text"/>
      </div>
      <div class="form-group">
        <label>密码：</label>
        <input v-model="loginForm.password" placeholder="请输入密码" type="password"/>
      </div>
      <button class="btn" @click="handleLogin">登录</button>
      <div v-if="token" class="result success">
        <p>登录成功！Token: {{ token }}</p>
      </div>
      <div v-if="errorMsg1" class="result error">
        <p>错误：{{ errorMsg1 }}</p>
      </div>
    </div>

    <!-- 查看个人信息 -->
    <div class="card">
      <h2>2. 查看个人信息（需要 ADMIN 或 USER 权限）</h2>
      <button class="btn" @click="handleGetProfile">获取个人信息</button>
      <div v-if="profileInfo" class="result">
        <pre>{{ JSON.stringify(profileInfo) }}</pre>
      </div>
      <div v-if="errorMsg2" class="result error">
        <p>错误：{{ errorMsg2 }}</p>
      </div>
    </div>

    <!-- 查看部门信息 -->
    <div class="card">
      <h2>3. 查看部门信息（仅 ADMIN 权限可访问）</h2>
      <button class="btn" @click="handleGetDepartment">获取部门信息</button>
      <div v-if="departmentInfo" class="result">
        <pre>{{ JSON.stringify(departmentInfo) }}</pre>
      </div>
      <div v-if="errorMsg3" class="result error">
        <p>错误：{{ errorMsg3 }}</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue'
import {getDepartmentInfo, getUserProfile, login} from '@/api/user'
import type {LoginRequest, User} from '@/api/types'

const loginForm = ref<LoginRequest>({
  username: 'user',
  password: '123456'
})

const token = ref<string>('')
const profileInfo = ref<User | null>(null)
const departmentInfo = ref<string>('')
const errorMsg1 = ref<string>('')
const errorMsg2 = ref<string>('')
const errorMsg3 = ref<string>('')

// 登录
const handleLogin = async (): Promise<void> => {
  try {
    if (!loginForm.value.username || !loginForm.value.password) {
      errorMsg1.value = '请填写用户名和密码'
      return
    }
    const response = await login(loginForm.value)
    const {data} = response.data
    console.log('登录成功：', data)
    // 保存 token 到 localStorage
    localStorage.setItem('token', data.token)
    token.value = data.token
  } catch (error: unknown) {
    errorMsg1.value = error instanceof Error ? error.message : '登录失败'
  }
}

// 获取个人信息
const handleGetProfile = async (): Promise<void> => {
  try {
    profileInfo.value = null
    if (!token.value) {
      errorMsg2.value = '请先登录'
      return
    }
    const response = await getUserProfile()
    profileInfo.value = response.data.data
  } catch (error: unknown) {
    errorMsg2.value = error instanceof Error ? error.message : '获取个人信息失败'
  }
}

// 获取部门信息
const handleGetDepartment = async (): Promise<void> => {
  try {
    departmentInfo.value = ''
    if (!token.value) {
      errorMsg3.value = '请先登录'
      return
    }
    const response = await getDepartmentInfo()
    departmentInfo.value = response.data.data
  } catch (error: unknown) {
    errorMsg3.value = error instanceof Error ? error.message : '获取部门信息失败'
  }
}
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

h1 {
  text-align: center;
  color: #333;
}

.card {
  background: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.card h2 {
  margin-top: 0;
  color: #555;
  font-size: 18px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: inline-block;
  width: 80px;
  font-weight: bold;
}

.form-group input {
  width: calc(100% - 100px);
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.btn {
  background: #4CAF50;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn:hover {
  background: #45a049;
}

.result {
  margin-top: 15px;
  padding: 15px;
  background: #e8f5e9;
  border: 1px solid #4CAF50;
  border-radius: 4px;
}

.result.error {
  background: #ffebee;
  border-color: #f44336;
  color: #f44336;
}

.result.success {
  background: #e3f2fd;
  border-color: #2196F3;
  color: #1976D2;
}

.result pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.result p {
  margin: 0;
}
</style>