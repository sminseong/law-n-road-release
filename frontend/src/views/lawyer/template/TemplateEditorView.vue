<template>
  <LawyerFrame>
    <div class="container py-4">
      <h3 class="mb-4 fw-bold">템플릿 작성</h3>

      <div class="card p-3 mb-4">
        <label class="form-label fw-bold">템플릿 제목</label>
        <input v-model="title" class="form-control" placeholder="예: 교통사고 내용증명" />
      </div>

      <!-- 변수 영역 상단 이동 -->
      <div class="card p-3 mb-4">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <span class="fw-bold">사용된 변수</span>
          <button class="btn btn-sm btn-outline-primary" @click="showModal = true">+ 변수 추가</button>
        </div>
        <ul class="mb-0">
          <li v-for="v in usedVariables" :key="v">#{ {{ v }} }</li>
          <li v-if="usedVariables.length === 0" class="text-muted">사용된 변수가 없습니다</li>
        </ul>
      </div>

      <!-- 에디터 + 미리보기 반반 -->
      <div class="row align-items-stretch">
        <div class="col-lg-6 d-flex flex-column">
          <div class="card p-3 mb-4 h-100 d-flex flex-column">
            <label class="form-label fw-bold">템플릿 본문</label>

            <div class="editor-area-wrapper editor-bordered bg-white position-relative"  @click="editor?.commands.focus()">
              <div class="placeholder-text" v-if="isEditorEmpty">내용을 입력하세요</div>
              <EditorContent v-if="editor" :editor="editor" class="editor-area" />
              <div
                  v-if="showAiPopover"
                  class="ai-helper-popover position-absolute bg-white border rounded shadow-sm p-2"
                  :style="{ top: `${popoverY}px`, left: `${popoverX}px` }"
              >
                <!-- Bold -->
                <!-- Bold -->
                <button
                    class="btn btn-sm d-flex align-items-center gap-1"
                    :class="{ 'text-primary': editor?.isActive('bold') }"
                    @click="editor.chain().focus().toggleBold().run()"
                >
                  <i class="bi bi-type-bold"></i> 굵게
                </button>

                <!-- Italic -->
                <button
                    class="btn btn-sm d-flex align-items-center gap-1"
                    :class="{ 'text-primary': editor?.isActive('italic') }"
                    @click="editor.chain().focus().toggleItalic().run()"
                >
                  <i class="bi bi-type-italic"></i> 기울임
                </button>

                <!-- Underline -->
                <button
                    class="btn btn-sm d-flex align-items-center gap-1"
                    :class="{ 'text-primary': editor?.isActive('underline') }"
                    @click="editor.chain().focus().toggleUnderline().run()"
                >
                  <i class="bi bi-type-underline"></i> 밑줄
                </button>

                <!-- Heading Dropdown -->
                <div class="btn-group">
                  <button type="button" class="btn btn-sm dropdown-toggle d-flex align-items-center gap-1"
                          data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="bi bi-type-h1"></i> 제목
                  </button>
                  <ul class="dropdown-menu p-1">
                    <li v-for="n in 6" :key="n">
                      <button class="dropdown-item" style="font-size: 0.75rem"
                              :class="{ 'text-primary fw-bold': editor?.isActive('heading', { level: n }) }"
                              @click="editor.chain().focus().toggleHeading({ level: n }).run()">
                        제목 {{ n }}
                      </button>
                    </li>
                  </ul>
                </div>

                <!-- List Dropdown -->
                <div class="btn-group">
                  <button type="button" class="btn btn-sm dropdown-toggle d-flex align-items-center gap-1"
                          data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="bi bi-list-task"></i> 목록
                  </button>
                  <ul class="dropdown-menu p-1">
                    <li>
                      <button class="dropdown-item" style="font-size: 0.75rem"
                              :class="{ 'text-primary fw-bold': editor?.isActive('orderedList') }"
                              @click="editor.chain().focus().toggleOrderedList().run()">
                        숫자 목록
                      </button>
                    </li>
                    <li>
                      <button class="dropdown-item" style="font-size: 0.75rem"
                              :class="{ 'text-primary fw-bold': editor?.isActive('bulletList') }"
                              @click="editor.chain().focus().toggleBulletList().run()">
                        글머리 기호
                      </button>
                    </li>
                  </ul>
                </div>

                <!-- AI 교정 드롭다운 -->
                <div class="btn-group align-self-center">
                  <button type="button" class="btn btn-sm dropdown-toggle d-flex align-items-center gap-1"
                          data-bs-toggle="dropdown">
                    <i class="bi bi-stars"></i> AI교정
                  </button>
                  <ul class="dropdown-menu p-1">
                    <li><button class="dropdown-item" style="font-size: 0.75rem" @click="fixTone('맞춤법')">맞춤법 교정</button></li>
                    <li><button class="dropdown-item" style="font-size: 0.75rem" @click="fixTone('전문')">전문적인 말투</button></li>
                    <li><button class="dropdown-item" style="font-size: 0.75rem" @click="fixTone('정중')">정중한 말투</button></li>
                    <li><button class="dropdown-item" style="font-size: 0.75rem" @click="fixTone('다정')">다정한 말투</button></li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="col-lg-6 d-flex flex-column">
          <div class="card p-3 mb-4 h-100 d-flex flex-column">
            <div class="d-flex justify-content-between align-items-center fw-bold mb-2">
              <span>실시간 미리보기</span>
              <small class="text-muted">※ 변수는 예시값 또는 description으로 치환됩니다.</small>
            </div>
            <div class="preview-box" v-html="previewText"></div>
          </div>
        </div>
      </div>

      <div class="text-end mt-3">
        <button class="btn btn-primary" @click="saveTemplate">저장</button>
      </div>
    </div>

    <!-- 모달 -->
    <div v-if="showModal" class="modal-backdrop">
      <div class="modal-content p-3">
        <h5 class="fw-bold">변수 추가</h5>

        <div class="mb-2">
          <label class="form-label">변수명 (예: name)</label>
          <input v-model="newVariable" class="form-control" />
        </div>

        <div class="mb-3">
          <label class="form-label">예시값 또는 설명</label>
          <textarea v-model="newDescription" class="form-control" rows="3" />
        </div>

        <div class="text-end">
          <button class="btn btn-secondary me-2" @click="showModal = false">취소</button>
          <button class="btn btn-primary" @click="addVariable">추가</button>
        </div>
      </div>
    </div>
  </LawyerFrame>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Editor, EditorContent } from '@tiptap/vue-3'
import http from '@/libs/HttpRequester'
import LawyerFrame from "@/components/layout/lawyer/LawyerFrame.vue";

import StarterKit from '@tiptap/starter-kit'
import Underline from '@tiptap/extension-underline'
import TextStyle from '@tiptap/extension-text-style'

const title = ref('')
const content = ref('')
const showModal = ref(false)
const variableMap = ref({})

const newVariable = ref('')
const newDescription = ref('')

const editor = ref(null)
const showAiPopover = ref(false)
const popoverX = ref(0)
const popoverY = ref(0)

let isClickOnly = false

onMounted(() => {
  editor.value = new Editor({
    content: '',
    extensions: [
      StarterKit, // 모든 기본 확장 포함 (disable 제거)
      Underline,
      TextStyle
    ],
    onUpdate: ({ editor }) => {
      content.value = editor.getHTML()
    }
  })

  // ✅ 1. 드래그 시작 시점
  document.addEventListener('mousedown', () => {
    isClickOnly = true
  })

  // ✅ 2. 드래그 감지 시 클릭이 아님으로
  document.addEventListener('mousemove', () => {
    isClickOnly = false
  })

  // ✅ 3. 마우스 놓을 때
  document.addEventListener('mouseup', (e) => {
    const sel = window.getSelection()
    const selectedText = sel ? sel.toString().trim() : ''
    const editorEl = editor.value?.view.dom
    const isInsideEditor = sel?.anchorNode && editorEl.contains(sel.anchorNode)
    const isClickInsidePopover = e.target.closest('.ai-helper-popover')

    if (isClickInsidePopover) return
    if (!isInsideEditor || isClickOnly || !sel || selectedText === '') {
      showAiPopover.value = false
      return
    }

    const range = sel.getRangeAt(0)
    const rect = range.getBoundingClientRect()
    const editorRect = editor.value.view.dom.getBoundingClientRect()

    popoverX.value = rect.left - editorRect.left + rect.width / 2
    popoverY.value = rect.top - editorRect.top - 40
    showAiPopover.value = true
  })
})

const fixTone = async (tone) => {
  const text = window.getSelection().toString()
  if (!text) return
  try {
    const res = await fetch('/api/gemini/fix-tone', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ text, tone })
    })
    const { fixed } = await res.json()
    editor.value.chain().focus().deleteSelection().insertContent(fixed).run()
  } catch (e) {
    alert('❌ 말투 교정 실패')
  } finally {
    showAiPopover.value = false
  }
}

const usedVariables = computed(() => {
  const matches = content.value.match(/#\{(.*?)\}/g) || []
  return [...new Set(matches.map(v => v.slice(2, -1)))]
})

// const previewText = computed(() => {
//   const parser = new DOMParser()
//   const doc = parser.parseFromString(content.value || '', 'text/html')
//   let html = doc.body.innerHTML || ''
//   html = html.replace(/#\{(.*?)\}/g, (_, v) => variableMap.value[v] || `예시_${v}`)
//   return html
// })

const previewText = computed(() => {
  let html = content.value.replace(/#\{(.*?)\}/g, (_, v) => variableMap.value[v] || `예시_${v}`)

  // 빈 <p></p>나 <p><br></p> 줄에 &nbsp; 추가
  html = html.replace(/<p>(\s|<br\s*\/?>)*<\/p>/g, '<p>&nbsp;</p>')

  return html
})

const isEditorEmpty = computed(() => {
  // TipTap이 비었을 때 기본적으로 '<p></p>' 또는 '<p><br></p>' 반환함
  const stripped = content.value
      .replace(/<p><br><\/p>/g, '')
      .replace(/<p><\/p>/g, '')
      .replace(/\s|&nbsp;/g, '') // 공백 문자 제거
      .trim()
  return stripped === ''
})

// const previewText = computed(() => {
//   const raw = content.value.replace(/#\{(.*?)\}/g, (_, v) => variableMap.value[v] || `예시_${v}`)
//   return raw.replace(/\n/g, '<br />')
// })

const addVariable = () => {
  const key = newVariable.value.trim()
  if (!key) return
  const val = newDescription.value.trim()
  editor.value?.chain().focus().insertContent(`#{${key}}`).run()
  variableMap.value[key] = val || `예시_${key}`
  showModal.value = false
  newVariable.value = ''
  newDescription.value = ''
}

const saveTemplate = async () => {
  try {
    await http.post('/api/templates', {
      title: title.value,
      content: content.value
    })
    alert('✅ 템플릿이 저장되었습니다.')
  } catch (e) {
    alert('❌ 저장 실패: ' + e.message)
  }
}
</script>

<style scoped>
.editor-area-wrapper {
  min-height: 300px;
  overflow: visible; /* 스크롤 제거 */
  position: relative;
  border: 1px solid #ccc;
  border-radius: 0.375rem;
  padding: 1rem;
  cursor: text; /* ✅ 어디든 클릭 시 커서 뜸 */
}

.editor-area {
  height: auto;
  min-height: 300px;
  outline: none;
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.4; /* ✅ 줄간격 줄임 */
  font-size: 1rem;
  font-family: inherit;
}

.placeholder-text {
  position: absolute; /* ✅ 고정 위치 */
  top: 1rem;
  left: 50%; /* ✅ 가운데 기준으로 */
  color: #bbb;
  pointer-events: none;
  z-index: 0;
}

.preview-box {
  height: auto;
  min-height: 330px;
  overflow: visible; /* ✅ 스크롤 제거 */
  border: 1px solid #ddd;
  border-radius: 0.375rem;
  padding: 1rem;
  background: #f9f9f9;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: inherit;
  font-size: 1rem;
  line-height: 1.4; /* ✅ 줄간격 줄임 */
}

/* 빈 줄 강제 표현 */
.preview-box p:empty::before {
  content: '\00a0';
  display: block;
  height: 1em;
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 400px;
}

.ai-helper-popover {
  z-index: 2000;
  display: flex;
  gap: 4px;
  flex-wrap: nowrap;       /* ✅ 줄바꿈 방지 */
  align-items: center;     /* ✅ 세로 정렬 맞춤 */
  flex-direction: row;
  position: absolute;
  background: white;
  border: 1px solid #ccc;
  border-radius: 6px;
  padding: 4px 6px; /* 전체 padding 줄이기 */
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  white-space: nowrap;
}
.ai-helper-popover .btn {
  padding: 2px 6px;
  font-size: 0.75rem;
  line-height: 1.2;
  border: none;            /* ✅ 테두리 제거 */
  background: transparent; /* ✅ 배경 제거 */
  color: #aaa;
  box-shadow: none;
}
.ai-helper-popover .btn:hover {
  border-radius: 4px;
  color: #003366;
}

</style>
