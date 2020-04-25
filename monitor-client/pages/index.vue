<template>
  <div class="center grid">
    <vs-row>
      <h3 style="color:white">Simple DashBoard</h3>
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
</template>

<script>
import { Client } from '@stomp/stompjs'

export default {
  components: {},
  data: () => ({
    action: null,
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
    },
    initWsConnector() {
      const client = new Client({
        brokerURL: 'ws://' + window.location.hostname + ':15674/ws',
        connectHeaders: {
          login: 'guest',
          passcode: 'guest'
        },
        debug: function(str) {
          console.log(str)
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000
      })

      // Fallback code
      if (typeof WebSocket !== 'function') {
        // For SockJS you need to set a factory that creates a new SockJS instance
        // to be used for each (re)connect
        client.webSocketFactory = function() {
          // Note that the URL is different from the WebSocket URL
          return new SockJS('http://localhost:15674/stomp')
        }
      }

      client.onConnect = function(frame) {
        client.subscribe('/topic/log-info', function(message) {
          if (message.body) {
            console.log('hihi')
            console.log(JSON.parse(message.body))
          } else {
            console.error('Error !!!')
          }
        })
      }

      client.onStompError = function(frame) {
        // Will be invoked in case of error encountered at Broker
        // Bad login/passcode typically will cause an error
        // Complaint brokers will set `message` header with a brief message. Body may contain details.
        // Compliant brokers will terminate the connection after any error
        console.log('Broker reported error: ' + frame.headers['message'])
        console.log('Additional details: ' + frame.body)
      }
      client.activate()
    }
  },
  mounted() {
    this.initWsConnector()
  }
}
</script>

<style></style>
