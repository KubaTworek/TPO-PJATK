package com.example.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Properties;

public class ChatProducer extends Application {
    private static final String BOOTSTRAP_SERVERS = "localhost:29092";
    private static final String GROUP_ID = "chat_group";

    private Producer<String, String> producer;
    private KafkaConsumer<String, String> consumer;
    private TextArea chatArea;
    private TextField messageField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        if (getParameters().getRaw().isEmpty()) {
            System.out.println("Usage: ChatProducerOne <name>");
            Platform.exit();
            return;
        }

        String name = getParameters().getRaw().get(0);
        String topic = "chat_topic";
        String group = "chat_group_" + name;

        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(producerProps);

        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        consumerProps.put("group.id", group);
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(List.of(topic));

        chatArea = new TextArea();
        chatArea.setEditable(false);

        messageField = new TextField();
        messageField.setOnAction(e -> sendMessage());

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        VBox inputBox = new VBox(10, messageField, sendButton);
        inputBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(new ScrollPane(chatArea));
        root.setBottom(inputBox);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Producer");
        primaryStage.setOnCloseRequest(e -> {
            producer.close();
            consumer.close();
        });
        primaryStage.show();

        receiveMessages();
    }

    private void sendMessage() {
        String name = getParameters().getRaw().get(0);
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            producer.send(new ProducerRecord<>("chat_topic", name, message));
            messageField.clear();
        }
    }

    private void receiveMessages() {
        Thread receiverThread = new Thread(() -> {
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(100);
                    for (ConsumerRecord<String, String> record : records) {
                        String timestamp = LocalTime.now().toString();
                        String chatMessage = timestamp + " ---- " + record.key() + ": " + record.value();

                        Platform.runLater(() -> chatArea.appendText(chatMessage + "\n"));
                    }

                    consumer.commitSync();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        receiverThread.setDaemon(true);
        receiverThread.start();
    }
}
