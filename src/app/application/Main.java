package app.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import app.channel.Channel;
import javafx.application.Application;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import settings.SettingManager;
import streamService.StreamService;
import updateFramework.UpdateWorker;

public class Main extends Application {

    public static Stage mainStage;
    public static ScheduledService<Void> updateThread;
    private static List<Thread> workerThreads;

    @Override
    public void start(final Stage primaryStage) {
        mainStage = primaryStage;

        primaryStage.setMinHeight(450);
        primaryStage.setMinWidth(600);
        primaryStage.setWidth(600);
        primaryStage.setHeight(450);

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/GUI.fxml"));
            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("lightStyle.css").toString());

            primaryStage.setTitle("Livestreamer GUI v3.0");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/icon.jpg")));
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initThreads();
    }

    @Override
    public void stop() {
        SettingManager.getInstance().saveSettings();
    }

    private void initThreads() {
        workerThreads = new ArrayList<Thread>();
        updateThread = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        workerThreads.clear();
                        for (StreamService s : SettingManager.getInstance().getStreamServices()) {
                            for (Channel c : s.getChannels().get()) {
                                Thread t = new Thread(new UpdateWorker(c));
                                workerThreads.add(t);
                                t.setDaemon(false);
                                t.start();
                            }
                        }
                        for (Thread t : workerThreads) {
                            try {
                                t.join(SettingManager.getInstance().getTimeout());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                };
                return task;
            }
        };
        updateThread.setPeriod(Duration.seconds(60.0));
        updateThread.setRestartOnFailure(true);
        updateThread.start();
        updateThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                updateThread.reset();
                for (StreamService ss : SettingManager.getInstance().getStreamServices()) {
                    ss.getChannels().sort(new Comparator<Channel>() {
                        @Override
                        public int compare(Channel c1, Channel c2) {
                            if (c1.isOnline() && !c2.isOnline()) {
                                return -1;
                            }
                            if (!c1.isOnline() && c2.isOnline()) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
