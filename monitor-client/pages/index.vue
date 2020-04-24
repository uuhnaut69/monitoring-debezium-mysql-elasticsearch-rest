<template>
  <div>
    <div class="center grid">
      <vs-row>
        <vs-col vs-type="flex" vs-justify="center" vs-align="center" w="12">
          <img src="~/static/dbz.png" width="15%" height="15%" />
        </vs-col>
      </vs-row>
      <vs-row>
        <vs-col vs-type="flex" vs-justify="center" vs-align="center" w="3">
          <vs-button
            success
            flat
            :active="action == 'start'"
            @click="triggerCdc('start')"
          >
            Start
          </vs-button>
        </vs-col>
        <vs-col vs-type="flex" vs-justify="center" vs-align="center" w="3">
          <vs-button
            danger
            flat
            :active="action == 'stop'"
            @click="triggerCdc('stop')"
          >
            Stop
          </vs-button>
        </vs-col>
        <vs-col vs-type="flex" vs-justify="center" vs-align="center" w="3">
          <vs-button
            warn
            flat
            :active="action == 'reset'"
            @click="triggerCdc('reset')"
          >
            Reset offset
          </vs-button>
        </vs-col>
      </vs-row>
    </div>
  </div>
</template>

<script>
export default {
  components: {},
  data: () => ({
    action: '',
    activeItem: 0
  }),
  methods: {
    async triggerCdc(event) {
      const loading = this.$vs.loading()
      this.action = event
      const result = await this.$axios.$post(`/sync/${this.action}`)
      loading.close()
      if (this.action === 'start') {
        this.openNotification('success', result)
      } else if (this.action === 'stop') {
        this.openNotification('danger', result)
      } else if (this.action === 'reset') {
        this.openNotification('warn', result)
      }
    },
    openNotification(color, message) {
      const noti = this.$vs.notification({
        color,
        position: 'bottom-right',
        title: 'Notification',
        text: message
      })
    }
  }
}
</script>

<style>
.container {
  background: black;
}
</style>
