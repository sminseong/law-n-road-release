<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  rows: { type: Array, required: true },              // 행 데이터
  columns: { type: Array, required: true },           // 열 정의: [{ label: '이름', key: 'name' }, ...]
  showSearchInput: { type: Boolean, default: true },
  filters: { type: Array, default: () => [] },        // ex: [{ label: 'Status', key: 'status', options: ['Active', 'Inactive'] }]
  rowsPerPage: { type: Number, default: 10 },
})

const emit = defineEmits(['edit', 'delete'])

const searchKeyword = ref('')
const currentPage = ref(1)
const selectedFilters = ref({})

// 필터링
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

const paginatedRows = computed(() => {
  const start = (currentPage.value - 1) * props.rowsPerPage
  return filteredRows.value.slice(start, start + props.rowsPerPage)
})

const totalPages = computed(() => Math.ceil(filteredRows.value.length / props.rowsPerPage))

</script>

<template>
  <div class="card card-lg h-100">
    <div class="px-4 py-4">
      <div class="row justify-content-between">
        <div class="col-md-6" v-if="showSearchInput">
          <input v-model="searchKeyword" type="text" class="form-control" placeholder="Search..." />
        </div>
        <div class="col-md-6 d-flex gap-2 justify-content-end">
          <div v-for="filter in filters" :key="filter.key" class="w-auto">
            <select v-model="selectedFilters[filter.key]" class="form-select">
              <option value="All">All</option>
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
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(row, index) in paginatedRows" :key="index">
          <td v-for="col in columns" :key="col.key">{{ row[col.key] }}</td>
          <td>
            <div class="dropdown">
              <a href="#" class="text-reset" data-bs-toggle="dropdown">
                <i class="bi bi-three-dots-vertical"></i>
              </a>
              <ul class="dropdown-menu">
                <li><a class="dropdown-item" @click="$emit('edit', row)">Edit</a></li>
                <li><a class="dropdown-item" @click="$emit('delete', row)">Delete</a></li>
              </ul>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div class="d-flex justify-content-between align-items-center px-4 py-3 border-top">
      <span>Showing {{ (currentPage - 1) * rowsPerPage + 1 }} to
        {{ Math.min(currentPage * rowsPerPage, filteredRows.length) }} of {{ filteredRows.length }} entries</span>
      <ul class="pagination mb-0">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <a class="page-link" @click="currentPage--">Previous</a>
        </li>
        <li
            v-for="n in totalPages"
            :key="n"
            class="page-item"
            :class="{ active: n === currentPage }"
        >
          <a class="page-link" @click="currentPage = n">{{ n }}</a>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <a class="page-link" @click="currentPage++">Next</a>
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.dropdown-item {
  cursor: pointer;
}
</style>
