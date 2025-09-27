<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <el-icon><Iphone /></el-icon>
          <span class="title">短信登录</span>
        </div>
      </template>

      <el-form
        @submit.prevent="login"
        :model="formData"
        ref="loginFormRef"
        :rules="rules"
      >
        <el-form-item prop="phone">
          <el-input
            v-model="phone"
            placeholder="请输入手机号"
            size="large"
            :prefix-icon="Iphone"
            maxlength="11"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="sendSms"
            :disabled="isCountingDown"
            :loading="isSendingSms"
            size="large"
            style="width: 100%"
          >
            <el-icon><Message /></el-icon>
            {{ isCountingDown ? `${countdown}秒后重发` : "发送验证码" }}
          </el-button>
          <div style="text-align: center; width: 100%;">
    还没有账号？
    <el-link type="primary" @click="goRegister">去注册</el-link>
  </div>
        </el-form-item>

        <div v-show="isCodeSent">
          <el-form-item prop="code">
            <el-input
              v-model="code"
              placeholder="请输入验证码"
              size="large"
              :prefix-icon="Lock"
              maxlength="4"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="success"
              @click="login"
              :loading="isLoggingIn"
              size="large"
              style="width: 100%"
            >
              <el-icon><Check /></el-icon>
              登录
            </el-button>
          </el-form-item>
        </div>

        <el-alert
          v-if="errorMessage"
          :title="errorMessage"
          type="error"
          :closable="false"
          show-icon
        />

        <el-alert
          v-if="successMessage"
          :title="successMessage"
          type="success"
          :closable="false"
          show-icon
        />
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { Iphone, Message, Lock, Check } from "@element-plus/icons-vue";
import type { LoginRequest } from "../types/api";
import { authApi } from "../api/auth";

const router = useRouter();

const phone = ref("15365130252");
const code = ref("");
const errorMessage = ref("");
const successMessage = ref("");
const isCountingDown = ref(false);
const countdown = ref(60);
const isSendingSms = ref(false);
const isLoggingIn = ref(false);
let timer: ReturnType<typeof setInterval>;
const isCodeSent = ref(false);
const loginFormRef = ref<FormInstance>();

const formData = reactive({
  phone,
  code,
});


const rules: FormRules = {
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  code: [
    { required: true, message: "请输入验证码", trigger: "blur" },
    { len: 4, message: "验证码长度为4位", trigger: "blur" },
  ],
};
function goRegister() {
  router.push('/register')   // 跳注册页
}

const sendSms = async () => {
  if (!loginFormRef.value) return;

  const valid = await loginFormRef.value
    .validateField("phone")
    .catch(() => false);
  if (!valid) return;

  if (isCountingDown.value) return;

  errorMessage.value = "";
  successMessage.value = "";
  isSendingSms.value = true;

  try {
    const response = await authApi.sendSms(phone.value);

    if (response.code === 0) {
      successMessage.value = "验证码已发送！";
      ElMessage.success("验证码已发送，请查收！");
      isCodeSent.value = true;
      startCountdown();
    } else {
      errorMessage.value = response.msg || "发送失败，请重试。";
      ElMessage.error(response.msg || "发送失败，请重试。");
    }
  } catch (error) {
    const errorMsg = "请求失败，请稍后再试。";
    errorMessage.value = errorMsg;
    ElMessage.error(errorMsg);
  } finally {
    isSendingSms.value = false;
  }
};

const login = async () => {
  if (!loginFormRef.value) return;

  const valid = await loginFormRef.value.validate().catch(() => false);
  if (!valid) return;

  errorMessage.value = "";
  successMessage.value = "";
  isLoggingIn.value = true;

  try {
    const loginData: LoginRequest = {
      phone: phone.value,
      code: code.value,
    };

   /* ===== 紧接上文 <script setup lang="ts"> 区域 ===== */
const response = await authApi.login(loginData);
const loginResult = response.data;
console.log(loginResult);

if (loginResult.token) {
  localStorage.setItem("token", loginResult.token);
  localStorage.setItem("userPhone", loginResult.phone);
  localStorage.setItem("loginTime", new Date().toISOString());

  successMessage.value = loginResult.message || "登录成功!";
  ElMessage.success(loginResult.message || "登录成功!");

  // 跳转到首页
  router.push("/home");
} else {
  errorMessage.value = loginResult.message || "登录失败，请重试。";
  ElMessage.error(loginResult.message || "登录失败，请重试。");
}
} catch (error) {
  const errorMsg = "请求失败，请稍后再试。";
  errorMessage.value = errorMsg;
  ElMessage.error(errorMsg);
} finally {
  isLoggingIn.value = false;
}
}

const startCountdown = () => {
  isCountingDown.value = true;
  countdown.value = 60; // 重置倒计时为60秒

  timer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--;
    } else {
      clearInterval(timer);
      isCountingDown.value = false;
    }
  }, 1000);
};

// 清除定时器以避免内存泄漏
watch(isCountingDown, (newValue) => {
  if (!newValue) {
    clearInterval(timer);
  }
});
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  border-radius: 15px;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 20px;
  font-weight: 600;
  color: #409eff;
}

.card-header .el-icon {
  font-size: 24px;
}

.title {
  background: linear-gradient(45deg, #409eff, #67c23a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-1px);
}
</style>