package com.daenerys;

import com.daenerys.Encapsulation.Dispositivo;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import static com.daenerys.Sender.gson;

public class Receiver {
    private static final String EXCHANGE_NAME = "logs";
    static Gson gson = new Gson();

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            Dispositivo a = gson.fromJson(message, Dispositivo.class);
            System.out.println(" [x] Received '" + a.getFecha() + "'");

        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
