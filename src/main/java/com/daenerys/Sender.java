package com.daenerys;

import com.daenerys.Encapsulation.Dispositivo;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.omg.IOP.Encoding;


import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static spark.Spark.get;

public class Sender {

    private static final String EXCHANGE_NAME = "logs";
    static Random r1;
    static Gson gson = new Gson();
    static Format f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    static ConnectionFactory factory = new ConnectionFactory();
    static Timer t = new Timer();

    public static void main(String[] argv) throws Exception {
        factory.setHost("localhost");
        r1 = new Random();

        get("/", (request, response) -> {
            sendMessage();
            TimeUnit.SECONDS.sleep(2);
            response.redirect("/");
            return "";
        });
    }

    public static String generateObject(int dispositivo) {
        Dispositivo d = new Dispositivo(f.format(new Date()), dispositivo, generateRandom(), generateRandom());
        return gson.toJson(d);
    }

    public static double generateRandom() {
        r1 = new Random();
        return r1.nextInt((150 - (-150)) + 1) + (-150);
    }

    public static void sendMessage() throws  Exception{
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            String message = generateObject(r1.nextInt(1 + 1)  + 1);

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");
        }
    }


}
