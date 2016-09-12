#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
#  rcp_server.py
#  
#  Copyright 2016 Francesco Antoniazzi <francesco@debianFrancesco>
#  
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#  
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#  
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
#  MA 02110-1301, USA.
#  
#  

import pika

def fib(n):
	if n==0:
		return 0
	elif n==1:
		return 1
	else:
		return fib(n-1)+fib(n-2)
		
def on_request(ch,method,props,body):
	n = int(body)
	print(" [.] fib(%s)" % n)
	response = fib(n)
	print("Returned response %s" % response)
	ch.basic_publish(exchange='',routing_key=props.reply_to,properties=pika.BasicProperties(correlation_id = props.correlation_id),body=str(response))
	ch.basic_ack(delivery_tag = method.delivery_tag)

def main():
	connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
	channel = connection.channel()
	channel.queue_declare(queue='rpc_queue')
	channel.basic_qos(prefetch_count=1)
	channel.basic_consume(on_request, queue='rpc_queue')

	print(" [x] Awaiting RPC requests")
	channel.start_consuming()
	return 0

if __name__ == '__main__':
	main()

