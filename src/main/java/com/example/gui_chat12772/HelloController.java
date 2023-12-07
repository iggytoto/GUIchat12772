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
            JSONObject jsonObject = new JSONObject();
            String msg = textField.getText();
            jsonObject.put("msg", msg);
            jsonObject.put("to_id", toId);
            messageBox.appendText("me>: "+msg+"\n");
            textField.clear();
            out.writeUTF(jsonObject.toJSONString());
            textField.requestFocus();   // переведем фокус в поле ввода
        } catch (NullPointerException e) {
//            throw new RuntimeException(e);
            messageBox.appendText("Нет  подключения к серверу...\n");
            textField.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    protected void onConnect(){
        try {
            Socket socket = new Socket("127.0.0.1", 9123);
            DataInputStream is = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            sendBtn.setDisable(false);

            textField.requestFocus();
            thread = new Thread(()->{
                try {
                    while (true){
                        String response = is.readUTF();
                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
                        if(jsonObject.containsKey("msg")){
                            String msg = jsonObject.get("msg").toString();
                            messageBox.appendText(msg + "\n");
                        } else if (jsonObject.containsKey("onlineUsers")) {
                            JSONArray jsonArray = (JSONArray) jsonObject.get("onlineUsers");
                            Platform.runLater(()->{
                                usersBox.getChildren().clear();
                                jsonArray.forEach(user->{
                                    JSONObject jsonUser = (JSONObject) user; // уточняем, что user это JSONObject
                                    Button userBtn = new Button();
                                    userBtn.setText(jsonUser.get("name").toString());
                                    userBtn.setPrefWidth(200);
                                    userBtn.setOnAction((e)->{
                                        toId = Integer.parseInt(jsonUser.get("id").toString()); // Кто получатель
                                        messageBox.clear();
                                    });
                                    usersBox.getChildren().add(userBtn);
                                    //listBox.appendText(user + "\n");
                                });
                            });
                            //listBox.clear();
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
}