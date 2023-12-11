package com.example.gui_chat12772;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HelloController {
    @FXML
    public VBox usersBox;
    //public TextArea listBox;
    @FXML
    private TextField textField;
    @FXML
    private TextArea messageBox;
    @FXML
    private Button sendBtn;
   /* @FXML
    private Button connectBtn;*/
    private DataOutputStream out;
    private Thread thread;
    private int toId = 0;
    @FXML
    protected void onSendHandler() {
        try {
            JSONObject jsonObject = new JSONObject(); // Создали пустой JSON
            String msg = textField.getText(); // Прочитали что ввёл пользователь
            jsonObject.put("msg", msg); // Добавили сообщение пользователя в JSON
            jsonObject.put("to_id", toId); // Добавили получателя сообщения в JSON
            messageBox.appendText("me>: "+msg+"\n"); // Печатаем на экран своё же сообщение
            textField.clear(); // Очищаем поле в которое пользователь вводил сообщение
            out.writeUTF(jsonObject.toJSONString()); // Передаём JSON на сервер
            textField.requestFocus();   // переведем фокус в поле ввода
        } catch (IOException e) {
            messageBox.appendText("Нет  подключения к серверу...\n");
            textField.clear();
            throw new RuntimeException(e);
        }
    }
    @FXML
    protected void onConnect(){
        try {
            Socket socket = new Socket("127.0.0.1", 9123);
            DataInputStream is = new DataInputStream(socket.getInputStream()); // Поток ввода
            out = new DataOutputStream(socket.getOutputStream()); // Поток вывода
            sendBtn.setDisable(false); // Активируем кнопку отправки (до подключения она выключена)
            textField.requestFocus(); // Ставим каретку в поле ввода сообщения
            thread = new Thread(()->{ // Поток для приёма сообщений с сервера
                try {
                    while (true){
                        String response = is.readUTF(); // Ждём сообщение от сервера (в формате JSON)
                        JSONParser jsonParser = new JSONParser(); // Парсер для JSON
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(response); // Парсим JSON
                        if(jsonObject.containsKey("msg")){ // Проверяем что сервер прислал msg
                            String msg = jsonObject.get("msg").toString(); // Достаём сообщение
                            messageBox.appendText(msg + "\n"); // Печатаем на экран
                        } else if (jsonObject.containsKey("onlineUsers")) { // Проверяем что сервер прислал onlineUsers
                            JSONArray jsonArray = (JSONArray) jsonObject.get("onlineUsers"); // Достаём список
                            Platform.runLater(()->{
                                usersBox.getChildren().clear(); // Очищаем предыдущий список пользователей
                                addUserButton("Общий чат", 0);
                                jsonArray.forEach(user->{ // Перебираем список пользователей
                                    JSONObject jsonUser = (JSONObject) user; // уточняем, что user это JSONObject
                                    addUserButton(jsonUser.get("name").toString(), Integer.parseInt(jsonUser.get("id").toString()));
                                });
                            });
                        }
                    }
                }catch (IOException e){
                    System.out.println("Потеряно соединение с сервером");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addUserButton(String name, int id){
        Button userBtn = new Button(); // Создаём кнопку
        userBtn.setText(name); // Пишем на кнопке имя пользователя
        userBtn.setPrefWidth(200); // Устанавливаем ширину кнопки
        userBtn.setOnAction((e)->{ // Устанавливаем функцию-обработчик при клике на кнопку
            toId = id; // Кто получатель
            messageBox.clear(); // Очищаем сообщения чата
        });
        usersBox.getChildren().add(userBtn); // Добавляем кнопку на экран
    }
}