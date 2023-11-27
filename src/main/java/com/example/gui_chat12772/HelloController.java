package com.example.gui_chat12772;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HelloController {
    @FXML
    private TextField textField;
    @FXML
    private TextArea messageBox;
    @FXML
    private Button sendBtn;
    @FXML
    private Button connectBtn;
    private DataOutputStream out;

    @FXML
    protected void onSendHandler(){
        try {
            String msg = textField.getText();
            messageBox.appendText(msg+"\n");
            textField.clear();
            out.writeUTF(msg);
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
            connectBtn.setDisable(true);
            Thread thread = new Thread(()->{
                try {
                    while (true){
                        String response = is.readUTF();
                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
                        String msg = jsonObject.get("msg").toString();
                        messageBox.appendText(msg+"\n");
                    }
                }catch (IOException e){
                    System.out.println("Потеряно соединение с сервером");
                }catch (ParseException e){
                    e.printStackTrace();
                }
            });
            thread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}