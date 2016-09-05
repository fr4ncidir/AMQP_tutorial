#! /bin/bash

if [ "$EUID" -ne 0 ] 
  then 
  echo "Please run as root
      sudo bash $0"
  exit
fi
if [ "$#" -gt 1 ] || [ "$#" -eq 0 ]
  then
  echo "USAGE:
  rabbitmq_script.sh destroy		stops the AMQP RabbitMQ server
  rabbitmq_script.sh run		starts the server AMQP process RabbitMQ on localhost:5672
  rabbitmq_script.sh status		gets the RabbitMQ server status
  rabbitmq_script.sh queues		lists the queues in the server, and how many messages are in them
  rabbitmq_script.sh bindings		lists the bindings in the server
  rabbitmq_script.sh exchanges		lists the exchanges in the server
  rabbitmq_script.sh purge		clears the RabbitMQ server
  rabbitmq_script.sh pending		lists the unacknowledged messages in the server
  
For further functionalities, see https://www.rabbitmq.com/man/rabbitmqctl.1.man.html"
  exit
fi

if [ "$1" == "destroy" ]; then
  kill `sudo rabbitmqctl status | grep -o 'pid,[0-9][0-9]*' | grep -o '[0-9][0-9]*'`
  echo "RabbitMQ server stopped"
  exit
fi
if [ "$1" == "run" ]; then
  sudo rabbitmq-server -detached
  sudo rabbitmqctl status | grep -o 'pid,[0-9][0-9]*'
  echo "RabbitMQ server created"
  exit
fi
if [ "$1" == "status" ]; then
  sudo rabbitmqctl status
  exit
fi
if [ "$1" == "queues" ]; then
  sudo rabbitmqctl list_queues
  exit
fi
if [ "$1" == "bindings" ]; then
  sudo rabbitmqctl list_bindings
  exit
fi
if [ "$1" == "exchanges" ]; then
  sudo rabbitmqctl list_exchanges
  exit
fi
if [ "$1" == "purge" ]; then
  sudo rabbitmqctl stop_app
  sudo rabbitmqctl reset
  sudo rabbitmqctl start_app
  echo "RabbitMQ server cleared"
  exit
fi
if [ "$1" == "pending" ]; then
  echo "Queue_name msg_ready pending_ack"
  sudo rabbitmqctl list_queues name messages_ready messages_unacknowledged
  exit
fi