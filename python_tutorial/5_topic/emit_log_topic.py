#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
#  emit_log_topic.py
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

def main():
	connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
	channel = connection.channel()
	channel.exchange_declare(exchange='topic_logs',type='topic')
	
	while True:
		routing_key = raw_input("What's the routing key? ")
		if (routing_key=="exit"):
			break
		else:
			message = raw_input("Message: ")
			channel.basic_publish(exchange='topic_logs',routing_key=routing_key,body=message)
			print(" [x] Sent "+routing_key+" message: "+message)
	connection.close()
	return 0

if __name__ == '__main__':
	main()

