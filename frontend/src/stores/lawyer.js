import { defineStore } from 'pinia'
import http from '@/libs/HttpRequester.js'

export const useLawyerStore = defineStore('lawyer', {
    state: () => ({
        lawyerInfo: null
    }),
    actions: {
        async fetchLawyerInfo(no) {
            console.log('📌 현재 lawyerInfo, api 실행 생략:', this.lawyerInfo)
            if (this.lawyerInfo) return
            const res = await http.get(`/api/lawyer/info/${no}`)
            console.log('✅ 받아온 응답:', res)
            this.lawyerInfo = res.data
        }
    }
})