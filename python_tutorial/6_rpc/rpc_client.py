#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
#  rpc_client.py
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
import uuid

class Fibonacci(object):
	
	def __init__(self):
		self.connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
		self.channel = self.connection.channel()
		result = self.channel.queue_declare(exclusive=True)
		self.callback_queue = result.method.queue
		self.channel.basic_consume(self.on_response, no_ack=True,queue=self.callback_queue)
	
	def on_response(self, ch, method, props, body):
		if self.corr_id == props.correlation_id:
			self.response = body
	
	def call(self, n):
		self.response = None
		self.corr_id = str(uuid.uuid4())
		self.channel.basic_publish(exchange='',routing_key='rpc_queue',properties=pika.BasicProperties(reply_to = self.callback_queue,correlation_id = self.corr_id,),body=str(n))
		while self.response is None:
			self.connection.process_data_events()
		return int(self.response)

def main():
	fibonacci_rpc = Fibonacci()
	while True:
		number = raw_input(" [x] Requesting fib(")
		if number == "exit":
			break
		print(")")
		response = fibonacci_rpc.call(int(number))
		print(" [.] Got %r" % response)

	return 0

if __name__ == '__main__':
	main()

