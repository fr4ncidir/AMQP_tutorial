#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
#  receive_log_direct.py
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
    print(" [x] Received %r:%r" % (method.routing_key,body))

def main():
	connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
	channel = connection.channel()
	channel.exchange_declare(exchange='direct_logs',type='direct')
	result = channel.queue_declare(exclusive=True)
	queue_name = result.method.queue
	
	severities = sys.argv[1:]
	if not severities:
		sys.stderr.write("Usage: %s [info] [warning] [error]\n" % sys.argv[0])
		sys.exit(1)
	
	for severity in severities:
		channel.queue_bind(exchange='direct_logs',queue=queue_name,routing_key=severity)

	channel.basic_consume(callback,queue=queue_name,no_ack=True)
	print(' [*] Waiting for messages. To exit press CTRL+C')
	channel.start_consuming()
	return 0

if __name__ == '__main__':
	main()
