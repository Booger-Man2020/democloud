package com.example.democloud;

import com.example.democloud.Controller.Network;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private ListView<String> clientView;
    @FXML
    private ListView<String> serverView;

    private String homeDir;
    private Network network;
    private byte[] buf;

    private void readLoop() {
        try {
            while (true) {
                String command = network.readString();
                if (command.equals("#list")) {
                    Platform.runLater(() -> serverView.getItems().clear());
                    int len = network.readInt();
                    for (int i = 0; i < len; i++) {
                        String file = network.readString();
                        Platform.runLater(() -> serverView.getItems().add(file));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Connection lost");
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            homeDir = System.getProperty("user.home");
            clientView.getItems().clear();
            clientView.getItems().addAll(getFiles(homeDir));
            network = new Network(8180);
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private List<String> getFiles(String dir) {

        String[] list = new File(dir).list();
        assert list != null;
        return Arrays.asList(list);
    }

    public void upload(ActionEvent actionEvent) throws IOException {
       //   network = new Network(8081);

        network.getOs().writeUTF("#file#");
        String file = clientView.getSelectionModel().getSelectedItem();
        network.getOs().writeUTF(file);
        File toSend = Path.of(homeDir).resolve(file).toFile();
        network.getOs().writeLong(toSend.length());
        try (FileInputStream fis = new FileInputStream(toSend)) {
            while (fis.available() > 0) {
                int read = fis.read(buf);
                network.getOs().write(buf, 0, read);
//            }
            }
            //  network.getOs().;
        }
    }



    public void download(ActionEvent actionEvent) throws Exception {
        String dir = System.getProperty("serverDir");
        serverView.getItems().addAll(dir);
        String file = serverView.getSelectionModel().getSelectedItem();
        network.getIs().readUTF();
        File toSend = Path.of(dir).resolve(file).toFile();
        network.getIs().readLong();
        try (FileInputStream fis = new FileInputStream(toSend)) {
            while (fis.available() > 0) {
                int read = fis.read(buf);
                network.getOs().write(buf, 0, read);
//            }
            }

        }
    }

}
