import axios from '@/libs/HttpRequester'

export const sendWelcomeMessage = async () => {
    const res = await axios.post('/api/public/chatbot/open')
    return res.data // bubbles가 여기 있어야 함
}

export const sendChatToBot = (question) => {
    return axios.post('/api/public/chatbot/send', { question })
}