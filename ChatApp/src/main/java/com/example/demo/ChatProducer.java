package com.example.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChatProducer extends Application {
    private static final String BOOTSTRAP_SERVERS = "localhost:29092";
    private Producer<String, String> producer;
    private final Map<String, KafkaConsumer<String, String>> consumers = new HashMap<>();
    private TextArea chatArea;
    private TextField messageField;
    private ComboBox<String> chatComboBox;
    private String username;
    private final Set<String> chatNames = new HashSet<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        chatArea = new TextArea();
        chatArea.setEditable(false);

        messageField = new TextField();
        messageField.setOnAction(e -> sendMessage());

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        chatComboBox = new ComboBox<>();
        chatComboBox.setEditable(true);
        chatComboBox.setOnAction(e -> switchChat());

        VBox inputBox = new VBox(10, chatComboBox, messageField, sendButton);
        inputBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(new ScrollPane(chatArea));
        root.setBottom(inputBox);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Producer");
        primaryStage.setOnCloseRequest(e -> {
            producer.close();
            consumers.values().forEach(KafkaConsumer::close);
        });
        primaryStage.show();

        getUsername();

        createNewChat();

        loadExistingChats();

        chatComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldChat, newChat) -> {
            if (newChat != null && !newChat.isEmpty()) {
                switchChat();
            }
        });
    }

    private void getUsername() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Username");
        dialog.setHeaderText("Enter your username");
        dialog.setContentText("Username:");

        username = dialog.showAndWait().orElse(null);
    }

    private void loadExistingChats() {
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        consumerProps.put("group.id", username);
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList("chat-creation"));

        Thread receiverThread = new Thread(() -> {
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(100);
                    for (ConsumerRecord<String, String> record : records) {
                        String chatName = record.value();
                        if (!chatNames.contains(chatName)) {
                            Platform.runLater(() -> {
                                chatComboBox.getItems().add(chatName);
                                chatNames.add(chatName);
                            });
                        }
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

    private void createNewChat() {
        String chatName = getChatNameFromUser();
        if (chatName != null && !chatName.isEmpty()) {
            if (chatComboBox.getItems().contains(chatName)) {
                Properties consumerProps = new Properties();
                consumerProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
                consumerProps.put("group.id", username + "-" + chatName);
                consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
                consumer.subscribe(Collections.singletonList(chatName));

                consumers.put(chatName, consumer);
                chatComboBox.getSelectionModel().select(chatName);

                chatArea.clear();
                receiveMessages(chatName);
            } else {
                Properties producerProps = new Properties();
                producerProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
                producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

                producer = new KafkaProducer<>(producerProps);

                producer.send(new ProducerRecord<>("chat-creation", username, chatName));

                Properties consumerProps = new Properties();
                consumerProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
                consumerProps.put("group.id", username + "-" + chatName);
                consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
                consumer.subscribe(Collections.singletonList(chatName));

                consumers.put(chatName, consumer);
                chatComboBox.getItems().add(chatName);
                chatNames.add(chatName);

                chatComboBox.getSelectionModel().select(chatName);

                chatArea.clear();
            }
        }
    }

    private String getChatNameFromUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Chat");
        dialog.setHeaderText("Enter Chat Name");
        dialog.setContentText("Chat Name:");

        return dialog.showAndWait().orElse(null);
    }

    private void switchChat() {
        String selectedChat = chatComboBox.getSelectionModel().getSelectedItem();
        if (selectedChat != null && !selectedChat.isEmpty()) {
            chatArea.clear();
            stopReceivingMessages();
            receiveMessages(selectedChat);
        }
    }

    private void stopReceivingMessages() {
        KafkaConsumer<String, String> consumer = getCurrentConsumer();
        if (consumer != null) {
            consumer.close();
        }
    }

    private void sendMessage() {
        String selectedChat = chatComboBox.getSelectionModel().getSelectedItem();
        String message = messageField.getText().trim();
        if (selectedChat != null && !selectedChat.isEmpty() && !message.isEmpty()) {
            producer.send(new ProducerRecord<>(selectedChat, username, message));
            messageField.clear();
        }
    }

    private void receiveMessages(String chatName) {
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        consumerProps.put("group.id", username + "-" + chatName);
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(chatName));

        consumers.put(chatName, consumer);

        Thread receiverThread = new Thread(() -> {
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        String selectedChat = chatComboBox.getSelectionModel().getSelectedItem();
                        if (Objects.equals(record.topic(), selectedChat)) {
                            LocalTime currentTime = LocalTime.now();
                            String timestamp = currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                            String chatMessage = timestamp + " ---- " + record.key() + ": " + record.value();

                            Platform.runLater(() -> chatArea.appendText(chatMessage + "\n"));
                        }
                    }

                    consumer.commitAsync();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        receiverThread.setDaemon(true);
        receiverThread.start();
    }

    private KafkaConsumer<String, String> getCurrentConsumer() {
        String selectedChat = chatComboBox.getSelectionModel().getSelectedItem();
        return consumers.get(selectedChat);
    }
}