import { defineStore } from 'pinia'
import http from '@/libs/HttpRequester.js'

export const useLawyerStore = defineStore('lawyer', {
    state: () => ({
        lawyerInfo: 0
    }),
    getters: {
        getLawyerNo(state) {
            return state.lawyerInfo?.data?.lawyerNo ?? null
        },
        getName(state) {
            return state.lawyerInfo?.data?.name ?? ''
        },
        getProfileImage(state) {
            return state.lawyerInfo?.data?.profileImagePath ?? ''
        }
    },

    actions: {
        // 동일한 값에 대해 반복적으로 조회되는 것을 피하기 위함
        async fetchLawyerInfo(no) {
            console.log('📌 현재 lawyerInfo :', this.lawyerInfo)
            if (this.lawyerInfo) return
            const res = await http.get(`/api/lawyer/info/${no}`)
            console.log('✅ 받아온 응답:', res)
            this.lawyerInfo = res.data
        },


    }
})