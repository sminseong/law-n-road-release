<script setup>
import UserHeader from './UserHeader.vue'
import UserFooter from './UserFooter.vue'
import UserSidebar from './UserSidebar.vue'
import UserPageWrapper from './UserPageWrapper.vue'
</script>

<template>
    <UserHeader />
    <UserSidebar />
    <UserPageWrapper>
        <slot></slot>
    </UserPageWrapper>
    <UserFooter />
</template>
  
  