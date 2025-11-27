/*
 * @Author: Fhx0902 YJX040124@outlook.com
 * @Date: 2025-07-06 14:00:03
 * @LastEditors: Fhx0902 YJX040124@outlook.com
 * @LastEditTime: 2025-07-08 17:20:27
 * @FilePath: \front_flask\src\stores\yoloModel.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import { defineStore } from 'pinia';

export const useYoloModelStore = defineStore('yoloModel', {
  state: () => ({
    currentModel: '',
    modelLoaded: false,
    modelInfo: null as any
  }),
  actions: {
    setModelStatus(status: any) {
      this.currentModel = status.current_model || status.currentModel || '';
      this.modelLoaded = (status.model_loaded !== undefined ? status.model_loaded : status.modelLoaded) || false;
      this.modelInfo = status.model_info || status.modelInfo || null;
    },
    clearModelStatus() {
      this.currentModel = '';
      this.modelLoaded = false;
      this.modelInfo = null;
    }
  },
  persist: true // 启用持久化
}); 