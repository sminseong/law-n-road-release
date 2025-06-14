<script setup>
import {ref, computed, onMounted, watch, nextTick } from 'vue'
import { Editor, EditorContent } from '@tiptap/vue-3'
import { VariableNode } from '@/components/template/variable.js'
import StarterKit from '@tiptap/starter-kit'
import Underline from '@tiptap/extension-underline'
import TextStyle from '@tiptap/extension-text-style'
import http from '@/libs/HttpRequester'

// props
const props = defineProps({
  content: String,
  variables: Array,
  isEdit: { type: Boolean, default: false },
  isDetail: { type: Boolean, default: false }
})
const emit = defineEmits(['update:content', 'update:variables'])

// state
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
    content: props.content || '',
    editable: !props.isDetail,
    extensions: [StarterKit, Underline, TextStyle, VariableNode],
    onUpdate: ({ editor }) => {
      // 1. 본문 반영
      const html = editor.getHTML()
      emit('update:content', html)

      // 2) JSON 문서 가져오기
      const doc = editor.getJSON()
      const found = new Set()

      // 3) 재귀 순회로 variable 노드만 골라내기
      function traverse(nodes) {
        if (!nodes) return
        for (const node of nodes) {
          if (node.type === 'variable' && node.attrs?.name) {
            found.add(node.attrs.name)
          }
          // 자식이 있으면 내려가서 또 찍어 보고
          if (node.content) {
            traverse(node.content)
          }
        }
      }
      traverse(doc.content)

      // 4) variableMap 동기화
      found.forEach(name => {
        if (!variableMap.value[name]) {
          // 기존에 없던 변수면 description 기본값은 name
          variableMap.value[name] = name
        }
      })

      // 5) emit variables
      emit(
          'update:variables',
          Object.entries(variableMap.value).map(([name, description]) => ({
            name,
            description,
          }))
      )
    },
  })

  document.addEventListener('mousedown', () => { isClickOnly = true })
  document.addEventListener('mousemove', () => { isClickOnly = false })

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
    const editorRect = editorEl.getBoundingClientRect()

    popoverX.value = rect.left - editorRect.left + rect.width / 2
    popoverY.value = rect.top - editorRect.top - 40
    showAiPopover.value = true
  })
})

let initialized = false

watch(
    () => props.variables,
    (newVal) => {
      if (!initialized && Array.isArray(newVal) && newVal.length > 0 && (props.isEdit || props.isDetail)) {
        const map = {}
        newVal.forEach(v => {
          map[v.name] = v.description
        })
        variableMap.value = map
        initialized = true
      }
    },
    { immediate: true }
)

let contentInitialized = false

watch(
    () => props.content,
    (newVal) => {
      if (!contentInitialized && editor.value && newVal) {
        editor.value.commands.setContent(newVal)
        contentInitialized = true
      }
    },
    { immediate: true }
)

const fixTone = async (tone) => {
  const text = window.getSelection().toString().trim()
  if (!text) {
    alert('⚠️ 선택된 텍스트가 없습니다.')
    return
  }

  try {
    const res = await http.post('/api/gemini/fix-tone', {
      text,
      tone
    })
    const { fixed } = res.data
    editor.value.chain().focus().deleteSelection().insertContent(fixed).run()
  } catch (e) {
    console.error('❌ 말투 교정 실패:', e)
    alert('❌ 말투 교정 실패')
  } finally {
    showAiPopover.value = false
  }
}

const usedVariables = computed(() => {
  const matches = props.content.match(/#\{(.*?)\}/g) || []
  return [...new Set(matches.map(v => v.slice(2, -1)))]
})

// const previewText = computed(() => {
//   let html = props.content.replace(/#{(.*?)}/g, (_, v) => variableMap.value[v] || `(${v})`)
//   // 빈 <p></p>나 <p><br></p> 줄에 &nbsp; 추가
//   return html.replace(/<p>(\s|<br\s*\/?\>)*<\/p>/g, '<p>&nbsp;</p>')
// })

const previewText = computed(() => {
  return props.content
      .replace(/#\{(.*?)\}/g, (_, v) =>
          variableMap.value[v] != null
              ? `<span class="text-danger">${variableMap.value[v]}</span>`
              : `#{${v}}`
      )
      .replace(/<p>(\s|<br\s*\/?>)*<\/p>/g, '<p>&nbsp;</p>')
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

const addVariable = () => {
  const key = newVariable.value.trim()
  if (!key) return
  const val = newDescription.value.trim()

  editor.value
      .chain()
      .focus()
      .setVariable({ name: key, description: val })
      .run()

  // 상태 업데이트
  variableMap.value[key] = val || ''
  emit('update:variables',
      Object.entries(variableMap.value).map(([name,description])=>({ name, description })))
  newVariable.value = ''
  newDescription.value = ''
  showModal.value = false
}

// 예시 입력 함수
function insertExample() {
  const rawText = `
본 문서는 #{이름} 님과 #{상대방 이름} 님 간의 교통사고에 대한 합의 내용을 담고 있습니다.

사고 일시: #{사고 일시}
사고 장소: #{사고 장소}

피해자와 가해자는 아래의 조건에 따라 합의하며,
추후 법적 책임을 상호간에 묻지 않기로 합니다.

합의금액: #{합의금액} 원
`

  const htmlText = rawText
      .trim()
      .split('\n')
      .map(line => `<p>${line}</p>`)
      .join('')

  // 🔥 에디터 본문 직접 변경
  editor.value?.commands.setContent(htmlText)

  // 변수 맵도 업데이트
  variableMap.value = {
    '이름': '홍길동',
    '상대방 이름': '김철수',
    '사고 일시': '2025.01.01 12:00:00',
    '사고 장소': '서울 서초구 삼성동 1117로 사거리',
    '합의금액': '400,000원'
  }

  // 부모에도 알려줘야 preview 쪽 computed도 갱신됨
  emit('update:content', htmlText)
  emit('update:variables', Object.entries(variableMap.value).map(([name, description]) => ({ name, description })))
}

const inputRef = ref(null)
const descRef = ref(null)

watch(showModal, (val) => {
  if (val) {
    nextTick(() => {
      inputRef.value?.focus()
    })
  }
})

watch(
    () => props.isDetail,
    (detail) => {
      if (editor.value) {
        editor.value.setOptions({ editable: props.isEdit && !detail })
      }
    }
)

</script>

<template>
  <div v-if="!isDetail" class="card p-3 mb-4 bg-light-subtle template-guide">
    <div class="d-flex justify-content-between align-items-center">
      <strong>AI 템플릿이란?</strong>
      <button class="btn btn-outline-secondary btn-sm" @click="insertExample">입력 예시 불러오기</button>
    </div>
    <p class="mt-2 mb-1 text-muted small">
      사용자의 정보를 자동으로 치환하여 문서를 완성하는 양식입니다. 예를 들어 <code>#{이름}</code>처럼 변수를 넣어두면, 구매자가 입력한 정보로 자동 대체됩니다.
      <br>
      또한, 구매자가 내용을 쉽게 작성할 수 있도록 <u><b>AI 인터뷰 형식</b></u>으로 질문에 답하게 도와줍니다.
    </p>
    <p class="mt-2 mb-1 text-muted small">
      이 방식은 구매자가 문서를 손쉽게 작성할 수 있어 이용률이 높아지고, 변호사는 일정한 품질의 문서를 반복적으로 판매할 수 있는 구조를 만들 수 있습니다.
    </p>
    <strong class="mt-5 mb-2">AI 템플릿 작성방법</strong>
    <ul class="mb-2 small text-muted">
      <li>템플릿 본문에 <code>#{변수명}</code> 형식으로 원하는 입력값을 지정하세요.</li>
      <li>우측 “실시간 미리보기” 영역에서 변수 치환 결과를 즉시 확인할 수 있습니다.</li>
      <li>작성이 어려우면 <u><b>예시 불러오기</b></u> 버튼을 눌러 예시 문장을 확인하세요.</li>
    </ul>
  </div>

  <!-- 변수 목록 및 추가 -->
  <div v-if="!isDetail" class="card p-3 mb-4 ">
    <div class="d-flex justify-content-between mb-2">
      <span class="form-label fw-bold">사용된 변수</span>
      <button class="btn btn-sm btn-outline-primary" @click="showModal = true">+ 변수 추가</button>
    </div>
    <ul class="mb-0">
      <li v-for="v in usedVariables" :key="v">#{ {{ v }} }</li>
      <li v-if="usedVariables.length === 0" class="text-muted">사용된 변수가 없습니다</li>
    </ul>
  </div>

  <!-- 에디터 + 미리보기 반반 -->
  <div class="row">
    <div class="col-lg-6">
      <div class="card p-3 mb-4">
        <label class="form-label fw-bold">템플릿 본문</label>
        <div class="editor-area-wrapper" @click="editor?.commands.focus()">
          <div class="placeholder-text" v-if="!props.content">내용을 입력하세요</div>
          <!-- 입력창 -->
          <EditorContent v-if="editor" :editor="editor" class="editor-area" />

          <!-- 입력보조 버튼 -->
          <div
              v-if="!isDetail && showAiPopover"
              class="ai-helper-popover position-absolute bg-white border rounded shadow-sm p-2"
              :style="{ top: `${popoverY}px`, left: `${popoverX}px` }"
          >
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
                <li><button class="dropdown-item" style="font-size: 0.75rem" @click="fixTone('SPELL')">맞춤법 교정</button></li>
                <li><button class="dropdown-item" style="font-size: 0.75rem" @click="fixTone('PROFESSIONAL')">전문적인 말투</button></li>
                <li><button class="dropdown-item" style="font-size: 0.75rem" @click="fixTone('TRUSTWORTHY')">신뢰감 있는 말투</button></li>
                <li><button class="dropdown-item" style="font-size: 0.75rem" @click="fixTone('WARM')">다정한 말투</button></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 실시간 미리보기창 -->
    <div class="col-lg-6">
      <div class="card p-3 mb-4">
        <div class="d-flex justify-content-between fw-bold mb-2">
          <span>실시간 미리보기</span>
          <small class="text-muted">※ 변수는 예시값 또는 설명으로 치환됩니다.</small>
        </div>
        <div class="preview-box" v-html="previewText"></div>
      </div>
    </div>
  </div>

  <!-- 모달 -->
  <div v-if="showModal" class="modal-backdrop">
    <div class="modal-content p-3">
      <h5 class="fw-bold">변수 추가</h5>
      <div class="mb-2">
        <label class="form-label">변수명 (예: name)</label>
        <input v-model="newVariable" class="form-control" ref="inputRef" />
      </div>
      <div class="mb-3">
        <label class="form-label">예시값 또는 설명 (예: 당사자)</label>
        <textarea v-model="newDescription" class="form-control" rows="3"
                  ref="descRef" @keydown.enter.prevent="addVariable"/>
      </div>
      <div class="text-end">
        <button class="btn btn-secondary me-2" @click="showModal = false">취소</button>
        <button class="btn btn-primary" @click="addVariable">추가</button>
      </div>
    </div>
  </div>
</template>

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
  flex-wrap: nowrap;
  align-items: center;
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
  border: none;
  background: transparent;
  color: #aaa;
  box-shadow: none;
}

.ai-helper-popover .btn:hover {
  border-radius: 4px;
  color: #003366;
}
</style>
