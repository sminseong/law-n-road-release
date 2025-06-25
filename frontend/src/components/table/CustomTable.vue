<script setup>
import { ref, computed, watch, toRefs } from 'vue'

const props = defineProps({
  rows: { type: Array, default: () => [] },
  columns: { type: Array, default: () => [] },
  showSearchInput: { type: Boolean, default: true },
  filters: { type: Array, default: () => [] },
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
  },
  currentPage: { type: Number, required: true },
  totalPages: { type: Number, required: true }
})

const emit = defineEmits([
  'update:filters',
  'page-change',
  'row-click',
  'edit-action',
  'delete-action'
])

// Unwrap reactive props
const { rows, columns, showSearchInput, filters, imageConfig, actionButtons, currentPage, totalPages } = toRefs(props)

const searchKeyword = ref('')
const selectedFilters = ref({})
// Initialize filters
filters.value.forEach(filter => {
  selectedFilters.value[filter.key] = filter.options.includes('전체') ? '전체' : filter.options[0]
})

function onFilterChange(key, value) {
  selectedFilters.value[key] = value
  emit('update:filters', {
    ...selectedFilters.value,
    keyword: searchKeyword.value
  })
}

watch(searchKeyword, () => {
  emit('update:filters', {
    ...selectedFilters.value,
    keyword: searchKeyword.value
  })
})

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
    emit('page-change', page)
  }
}
</script>

<template>
  <div class="card card-lg h-100">
    <!-- Header: Search and Filters -->
    <div class="px-4 py-4">
      <div class="row justify-content-between">
        <div class="col-md-6" v-if="showSearchInput">
          <label class="form-label mb-1">검색</label>
          <input v-model="searchKeyword" type="text" class="form-control" placeholder="Search..." />
        </div>
        <div class="col-md-6 d-flex gap-2 justify-content-end">
          <div v-for="filter in filters" :key="filter.key" class="w-auto">
            <label class="form-label mb-1">{{ filter.label }}</label>
            <select
                v-model="selectedFilters[filter.key]"
                class="form-select"
                @change="onFilterChange(filter.key, selectedFilters[filter.key])"
            >
              <option v-for="opt in filter.options" :key="opt" :value="opt">{{ opt }}</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="table-responsive">
      <table class="table table-hover mb-0 text-nowrap">
        <thead class="bg-light">
        <tr>
          <th v-for="col in columns" :key="col.key">{{ col.label }}</th>
        </tr>
        </thead>
        <tbody>
        <tr
            v-for="(row, index) in rows"
            :key="index"
            class="cursor-pointer"
            @click="emit('row-click', row)"
        >
          <td v-for="col in columns" :key="col.key" class="align-middle">
            <slot :name="`cell-${col.key}`" :row="row">
              <!-- Default rendering logic -->
              <template v-if="col.key === 'actions'">
                <!-- 생략 -->
              </template>
              <template v-else-if="imageConfig.enabled && col.key === imageConfig.targetKey">
                <!-- 생략 -->
              </template>
              <template v-else-if="col.key === 'status'">
      <span
          class="badge"
          :class="{
          'bg-success': ['완료', 'PAID', '결제완료'].includes(row[col.key]),
          'bg-warning text-dark': ['진행 중', 'ORDERED', '결제대기'].includes(row[col.key]),
          'bg-danger': ['환불', 'REFUNDED', '환불'].includes(row[col.key])
        }"
      >{{ row[col.key] }}</span>
              </template>
              <template v-else>
      <span>
        {{ col.formatter
          ? col.formatter(row[col.key], row)
          : row[col.key]
        }}
      </span>
              </template>
            </slot>
          </td>

        </tr>
        <tr v-if="rows.length === 0">
          <td :colspan="columns.length" class="text-center">데이터가 없습니다.</td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="d-flex justify-content-between align-items-center px-4 py-3 border-top">
      <span>Page {{ currentPage }} of {{ totalPages }}</span>
      <ul class="pagination mb-0">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <button class="page-link" @click="changePage(currentPage - 1)">Previous</button>
        </li>
        <li class="page-item" :class="{ disabled: !pageGroup.hasPrevGroup }">
          <button class="page-link" @click="changePage(pageGroup.prevPage)">«</button>
        </li>
        <li
            v-for="p in pageGroup.pages"
            :key="p"
            class="page-item"
            :class="{ active: p === currentPage }"
        >
          <button class="page-link" @click="changePage(p)">{{ p }}</button>
        </li>
        <li class="page-item" :class="{ disabled: !pageGroup.hasNextGroup }">
          <button class="page-link" @click="changePage(pageGroup.nextPage)">»</button>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <button class="page-link" @click="changePage(currentPage + 1)">Next</button>
        </li>
      </ul>
    </div>
  </div>
</template>

