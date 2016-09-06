#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
#  emit_log_direct.py
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
	connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
	channel = connection.channel()
	channel.exchange_declare(exchange='direct_logs',type='direct')
	while True:
		message = raw_input("What's your message? ")
		if (message=="exit"):
			break
		else:
			severity = raw_input("Message severity: ")
			channel.basic_publish(exchange='direct_logs',routing_key=severity,body=message)
			print(" [x] Sent "+severity+" message: "+message)
	connection.close()
	return 0

if __name__ == '__main__':
	main()
