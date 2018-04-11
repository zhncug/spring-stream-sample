# -*- coding: utf-8 -*-
import pika

local_host = '127.0.0.1'

connection = pika.BlockingConnection(pika.ConnectionParameters(host=local_host))
channel = connection.channel()

message = '{"type":1100005,"init_time":1521776564,"data":{"event":[{"user":"root","uid":"0","tty":"pts/3","time":"1521776545","proname":"login","process":"bash","ip":"192.168.199.109","hostname":"localhost.localdomain","euser":"root","euid":"0","cmd":"ping 192.168.199.86"}],"comid":"59080851823593e1a80b","agent_id":"b470f51011e3b7a9"}}'
channel.basic_publish(exchange='event_notify',
                      routing_key='event.intrusion_detect',
                      body=message)
print(" [x] Sent %r" % message)
connection.close()