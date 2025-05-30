<script setup>
const props = defineProps({
  List: {
    type: Array,
    required: true
  },
  maxLines: {
    type: Number,
    default: 2
  }
})

const lineHeight = 1.4 // em 기준 줄 간격


</script>

<template>
  <section class="mb-10">
    <div class="container">
      <div class="row">
        <div class="col-lg-12">
          <div class="recommend-box p-4 bg-light-subtle">
            <ul class="list-unstyled mb-0">
              <li class="qa-item" v-for="qna in List" :key="qna.id">
                <RouterLink :to="`/qna/${qna.id}`">
                  <div class="question">Q. {{ qna.question }}</div>
                  <div
                    class="answer-preview"
                    :style="{
                      WebkitLineClamp: maxLines,
                      maxHeight: `${lineHeight * maxLines + 0.6}em`
                    }"
                  >
                    A. {{ qna.answerPreview }}
                  </div>
                </RouterLink>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.qa-item {
  padding: 12px 16px;
  border-radius: 12px;
  background-color: #fff;
  margin-bottom: 12px;
}
.qa-item a {
  display: block;
  text-decoration: none;
  color: inherit;
}
.qa-item:last-child {
  border-bottom: none;
}
.question {
  margin-top: 10px;
  margin-left: 10px;
  padding: 8px;
  font-size: 20px;
  font-weight: bold;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal;
  line-height: 1.4;
}
.answer-preview {
    margin-left: 10px;
    margin-right: 10px;
  margin-bottom: 10px;
  font-size: 18px;
  padding: 8px;
  color: #555;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal;
  line-height: 1.4;
}
</style>
