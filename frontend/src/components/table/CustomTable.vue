<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  rows: Array,
  columns: Array,
  showSearchInput: { type: Boolean, default: true },
  filters: { type: Array, default: () => [] },
  rowsPerPage: { type: Number, default: 10 },
  imageConfig: {
    type: Object,
    default: () => ({
      enabled: false,
      key: 'imageUrl',
      targetKey: 'productName'
    })
  },
  actionButtons: {
    type: Object,
    default: () => ({
      edit: true,
      delete: true
    })
  }
})

const emit = defineEmits(['row-click', 'edit-action', 'delete-action'])

const searchKeyword = ref('')
const currentPage = ref(1)
const selectedFilters = ref({})

props.filters.forEach(filter => {
  selectedFilters.value[filter.key] = 'All'
})

const filteredRows = computed(() => {
  let result = props.rows
  for (const { key } of props.filters) {
    const selected = selectedFilters.value[key]
    if (selected && selected !== 'All') {
      result = result.filter(row => row[key] === selected)
    }
  }
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(row =>
        Object.values(row).some(val => String(val).toLowerCase().includes(keyword))
    )
  }
  return result
})

const totalPages = computed(() =>
    Math.ceil(filteredRows.value.length / props.rowsPerPage)
)

const paginatedRows = computed(() => {
  const start = (currentPage.value - 1) * props.rowsPerPage
  return filteredRows.value.slice(start, start + props.rowsPerPage)
})

const pageGroup = computed(() => {
  const groupSize = 5
  const groupIndex = Math.floor((currentPage.value - 1) / groupSize)
  const start = groupIndex * groupSize + 1
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
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}
</script>

<template>
  <div class="card card-lg h-100">
    <div class="px-4 py-4">
      <div class="row justify-content-between">
        <div class="col-md-6" v-if="showSearchInput">
          <label class="form-label mb-1">검색</label>
          <input v-model="searchKeyword" type="text" class="form-control" placeholder="Search..." />
        </div>
        <div class="col-md-6 d-flex gap-2 justify-content-end">
          <div v-for="filter in filters" :key="filter.key" class="w-auto">
            <label class="form-label mb-1">{{ filter.label }}</label>
            <select v-model="selectedFilters[filter.key]" class="form-select">
              <option v-if="!filter.options.includes('All')" value="All">All</option>
              <option v-for="opt in filter.options" :key="opt" :value="opt">{{ opt }}</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-hover mb-0 text-nowrap">
        <thead class="bg-light">
        <tr>
          <th v-for="col in columns" :key="col.key">{{ col.label }}</th>
        </tr>
        </thead>
        <tbody>
        <tr
            v-for="(row, index) in paginatedRows"
            :key="index"
            class="cursor-pointer"
            @click="emit('row-click', row)"
        >
          <td v-for="col in columns" :key="col.key" class="align-middle">
            <template v-if="col.key === 'actions'">
              <div class="d-flex gap-2">
                <button
                    v-if="props.actionButtons.edit"
                    class="btn btn-sm btn-outline-primary"
                    @click.stop.prevent="emit('edit-action', row)"
                >수정</button>
                <button
                    v-if="props.actionButtons.delete"
                    class="btn btn-sm btn-outline-secondary"
                    @click.stop.prevent="emit('delete-action', row)"
                >삭제</button>
              </div>
            </template>
            <template v-else-if="props.imageConfig.enabled && col.key === props.imageConfig.targetKey">
              <div class="d-flex align-items-center gap-3">
                <img :src="row[props.imageConfig.key]" alt="상품 이미지" width="80" height="80" class="rounded" />
                <div>{{ row[col.key] }}</div>
              </div>
            </template>
            <template v-else-if="col.key === 'status'">
                <span class="badge"
                      :class="{
                        'bg-primary': row[col.key] === '완료',
                        'bg-warning': row[col.key] === '진행 중',
                        'bg-danger': row[col.key] === '취소'
                      }">
                  {{ row[col.key] }}
                </span>
            </template>
            <template v-else>
              <span>{{ row[col.key] }}</span>
            </template>
          </td>
        </tr>
        <tr v-if="filteredRows.length === 0">
          <td :colspan="columns.length" class="text-center">
            데이터가 없습니다.
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div class="d-flex justify-content-between align-items-center px-4 py-3 border-top">
      <span>
        Showing
        {{ (currentPage - 1) * rowsPerPage + 1 }}
        to
        {{ Math.min(currentPage * rowsPerPage, filteredRows.length) }}
        of
        {{ filteredRows.length }} entries
      </span>
      <ul class="pagination mb-0">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <a class="page-link" @click="changePage(currentPage - 1)">Previous</a>
        </li>
        <li class="page-item" :class="{ disabled: !pageGroup.hasPrevGroup }">
          <button class="page-link" @click="changePage(pageGroup.prevPage)">«</button>
        </li>
        <li v-for="p in pageGroup.pages" :key="p" class="page-item" :class="{ active: p === currentPage }">
          <button class="page-link" @click="changePage(p)">{{ p }}</button>
        </li>
        <li class="page-item" :class="{ disabled: !pageGroup.hasNextGroup }">
          <button class="page-link" @click="changePage(pageGroup.nextPage)">»</button>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <a class="page-link" @click="changePage(currentPage + 1)">Next</a>
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
img {
  object-fit: cover;
}
</style>