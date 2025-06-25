<script setup>
import {computed, onMounted, ref, watch} from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import ClientFrame from "@/components/layout/client/ClientFrame.vue";
import { makeApiRequest } from "@/libs/axios-auth.js"

const vodList = ref([]);
const router = useRouter();
const currentPage = ref(1);
const totalPages = ref(1);
// 정렬기준
const sort = ref("recent");
// 카테고리
const categories = ref([])         // 카테고리 목록
const selectedCategory = ref(null) // 선택된 카테고리 번호

const fetchCategories = async () => {
  try {
    const res = await makeApiRequest({
      method: 'get',
      url: '/api/public/category/list'
    })
    categories.value = res.data
  } catch (err) {
    console.error("❌ 카테고리 목록 불러오기 실패:", err)
  }
}

const formatDuration = (seconds) => {
  const h = String(Math.floor(seconds / 3600)).padStart(2, "0");
  const m = String(Math.floor((seconds % 3600) / 60)).padStart(2, "0");
  const s = String(seconds % 60).padStart(2, "0");
  return `${h}:${m}:${s}`;
};

const fetchVodList = async () => {
  try {
    const res = await makeApiRequest({
      method: 'get',
      url: '/api/public/vod/list',
      params: {
        page: currentPage.value,
        size: 12,
        sort: sort.value,
        categoryNo: selectedCategory.value
      }
    })

    vodList.value = res.data.content
    totalPages.value = res.data.totalPages
  } catch (err) {
    console.error("❌ VOD 목록 불러오기 실패:", err)
  }
}


const goToVod = async (vod) => {
  try {
    await makeApiRequest({
      method: 'put',
      url: `/api/public/vod/${vod.vodNo}` // 조회수 증가
    })
  } catch (err) {
    console.error("❌ 조회수 증가 실패:", err)
  }

  router.push(`/vod/${vod.broadcastNo}`) // broadcastNo 기준으로 이동
}


const setSort = (type) => {
  if (sort.value !== type) {
    sort.value = type;
    currentPage.value = 1;
    fetchVodList();
  }
};

// Pagination group logic
const pageGroup = computed(() => {
  const groupSize = 5
  const gIndex = Math.floor((currentPage.value - 1) / groupSize)
  const start = gIndex * groupSize + 1
  const end = Math.min(start + groupSize - 1, totalPages.value)
  const pages = Array.from({ length: end - start + 1 }, (_, i) => start + i)
  return {
    pages,
    hasPrevGroup: start > 1,
    hasNextGroup: end < totalPages.value,
    prevPage: start - 1,
    nextPage: end + 1
  }
})

function changePage(page) {
  if (page >= 1 && page <= totalPages.value && page !== currentPage.value) {
    currentPage.value = page
  }
}

const onCategoryChange = () => {
  currentPage.value = 1;
  fetchVodList();
};

watch(currentPage, () => {
  fetchVodList();
});

onMounted(() => {
  fetchCategories();
  fetchVodList();
});
</script>


<template>
  <ClientFrame>
    <div class="container py-4">
      <h2 class="fs-3 fw-bold text-primary mb-4">방송 다시보기</h2>
      <!-- 정렬 + 카테고리 선택 라인 -->
      <div class="mb-3 d-flex justify-content-between align-items-center flex-wrap gap-2">
        <!-- 정렬 버튼 -->
        <div class="d-flex gap-2">
          <button
              class="btn"
              :class="sort === 'recent' ? 'btn-primary' : 'btn-outline-primary'"
              @click="setSort('recent')"
          >
            최신순
          </button>
          <button
              class="btn"
              :class="sort === 'popular' ? 'btn-primary' : 'btn-outline-primary'"
              @click="setSort('popular')"
          >
            인기순
          </button>
        </div>

        <!-- 카테고리 선택 -->
        <div class="d-flex align-items-center gap-2">
          <label class="fw-bold text-dark mb-0" style="white-space: nowrap;">카테고리</label>
          <select
              class="form-select"
              style="min-width: 200px"
              v-model="selectedCategory"
              @change="onCategoryChange"
          >
            <option :value="null">전체 카테고리</option>
            <option
                v-for="c in categories"
                :key="c.no"
                :value="c.no"
            >
              {{ c.name }}
            </option>
          </select>
        </div>
      </div>

      <div class="row g-4">
        <template v-for="vod in vodList" :key="vod.vodNo">
          <div class="col-12 col-sm-6 col-md-4 col-lg-3">
            <div class="card h-100 shadow-sm border-0 cursor-pointer" @click="goToVod(vod)">
              <div class="position-relative">
                <img
                    :src="vod.thumbnailPath || '/images/default-thumbnail.jpg'"
                    class="card-img-top"
                    alt="방송 썸네일"
                    style="height: 180px; object-fit: cover"
                />
                <!-- 뱃지 묶음 -->
                <div class="position-absolute top-0 start-0 m-2 d-flex gap-2">
                  <span class="badge bg-primary" style="font-size: 0.7rem; padding: 0.35em 0.6em;">다시보기</span>
                  <span
                      v-if="vod.categoryName"
                      class="badge bg-secondary"
                      style="font-size: 0.7rem; padding: 0.35em 0.6em;"
                  >{{ vod.categoryName }}
                  </span>
                </div>
                <!-- 영상 길이 -->
                <span
                    class="position-absolute bottom-0 end-0 m-2 px-2 py-1 bg-dark bg-opacity-50 text-white rounded small"
                    style="font-size: 0.75rem;"
                >
                  {{ formatDuration(vod.duration) }}
                </span>
              </div>

              <div class="card-body d-flex flex-column justify-content-between">
                <h5 class="card-title fs-6 fw-bold text-truncate mb-2 text-dark">{{ vod.title }}</h5>

                <div class="d-flex align-items-center gap-2 mb-1">
                  <img
                      :src="vod.lawyerProfile || '/images/default-profile.png'"
                      alt="변호사 프로필"
                      class="rounded-circle border border-secondary shadow-sm"
                      width="28"
                      height="28"
                  />
                  <span class="fs-7 text-dark-gray">{{ vod.lawyerName }} 변호사</span>
                </div>

                <!-- 조회수 + 날짜 -->
                <p class="mb-2 fs-7 text-muted">
                  조회수 {{ vod.viewCount }}회 <span class="mx-1">·</span> {{ vod.createdAt.slice(0, 10) }}
                </p>

                <!-- 키워드 목록 -->
                <div
                    v-if="vod.keywords && vod.keywords.length"
                    class="d-flex flex-wrap gap-1"
                >
                  <span
                      v-for="(keyword, index) in vod.keywords"
                      :key="index"
                      class="px-1 py-0 border border-secondary rounded-1 text-truncate"
                      style="font-size: 0.75rem;"
                  >
                    {{ keyword }}
                  </span>
                </div>
              </div>

            </div>
          </div>
        </template>
      </div>
      <!-- 페이지네이션 -->
      <div class="d-flex justify-content-center mt-4">
        <ul class="pagination">
          <!-- 이전 그룹 버튼 -->
          <li class="page-item" :class="{ disabled: currentPage === 1 }">
            <button class="page-link" @click="changePage(currentPage - 1)">Previous</button>
          </li>
          <li class="page-item" :class="{ disabled: !pageGroup.hasPrevGroup }">
            <a class="page-link" @click="changePage(pageGroup.prevPage)">«</a>
          </li>
          <!-- 현재 그룹의 페이지 번호들 -->
          <li
              v-for="p in pageGroup.pages"
              :key="p"
              class="page-item"
              :class="{ active: currentPage === p }"
          >
            <a class="page-link" @click="changePage(p)">{{ p }}</a>
          </li>
          <!-- 다음 그룹 버튼 -->
          <li class="page-item" :class="{ disabled: !pageGroup.hasNextGroup }">
            <a class="page-link" @click="changePage(pageGroup.nextPage)">»</a>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
            <button class="page-link" @click="changePage(currentPage + 1)">Next</button>
          </li>
        </ul>
      </div>
    </div>
  </ClientFrame>
</template>

<style scoped>
.fs-7 {
  font-size: 0.875rem;
}
.cursor-pointer {
  cursor: pointer;
}

.card:hover {
  box-shadow: 0 0 0.5rem rgba(0, 0, 0, 0.15);
  transform: scale(1.01);
  transition: all 0.2s;
}
</style>
