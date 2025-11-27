/*
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-07-05 19:08:50
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-07-11 00:00:08
 * @FilePath: \front_flask\src\stores\user.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { UserInfo } from '@/types/user';

export const useUserStore = defineStore('user', () => {
  const currentUser = ref<UserInfo | null>(null);

  // 设置用户信息
  const setUser = (user: UserInfo) => {
    currentUser.value = user;
  };

  // 清除用户信息
  const clearUser = () => {
    currentUser.value = null;
  };

  // 检查是否已登录
  const isLoggedIn = () => {
    return !!currentUser.value?.token;
  };

  // 获取用户token
  const getToken = () => {
    return currentUser.value?.token;
  };

  // 检查是否是管理员
  const isAdmin = () => {
    return currentUser.value?.roleType === 1;
  };

  return {
    currentUser,
    setUser,
    clearUser,
    isLoggedIn,
    getToken,
    isAdmin
  };
}, {
  persist: true // 启用状态持久化
}); 