#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
#  receive_log_topic.py
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
import sys

def callback(ch, method, properties, body):
    print(" [x] %r:%r" % (method.routing_key, body))

def main():
	connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
	channel = connection.channel()
	channel.exchange_declare(exchange='topic_logs',type='topic')
	result = channel.queue_declare(exclusive=True)
	queue_name = result.method.queue
	
	binding_keys = sys.argv[1:]
	if not binding_keys:
		sys.stderr.write("Usage: %s [binding_key]...\n" % sys.argv[0])
		sys.exit(1)
	for binding_key in binding_keys:
		channel.queue_bind(exchange='topic_logs',queue=queue_name,routing_key=binding_key)
	
	print(' [*] Waiting for logs. To exit press CTRL+C')
	channel.basic_consume(callback,queue=queue_name,no_ack=True)
	channel.start_consuming()
	return 0

if __name__ == '__main__':
	main()

