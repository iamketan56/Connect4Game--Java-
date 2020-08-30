package com.Ketansharma.connect;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
       FXMLLoader loader=new FXMLLoader(getClass().getResource("app_layout.fxml"));
        GridPane rootGridPane=loader.load();

        controller=loader.getController();
        controller.createPlayground();
        MenuBar menuBar=createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        Pane menuPane= (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);
        Scene scene=new Scene(rootGridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect-4-Game");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
private MenuBar createMenu()
    {
        Menu fileMenu=new Menu("File");
        Menu helpMenu=new Menu("Help");

        MenuItem newGame=new MenuItem("New game");
newGame.setOnAction(event -> controller.resetGame());

        MenuItem resetGame=new MenuItem("Reset game");
        resetGame.setOnAction(event -> controller.resetGame());

        SeparatorMenuItem separatorMenuItem=new SeparatorMenuItem();

        MenuItem exitGame=new MenuItem("Exit game");
        exitGame.setOnAction(event ->exitGame());
        MenuItem aboutGame=new MenuItem("About Connect-4-Game");
        aboutGame.setOnAction(event -> aboutGame());
        SeparatorMenuItem separator=new SeparatorMenuItem();
        MenuItem aboutMe=new MenuItem("About me");
        aboutMe.setOnAction(event -> aboutMe());
        fileMenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);
        helpMenu.getItems().addAll(aboutGame,separator,aboutMe);
        MenuBar menuBar=new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);
        return menuBar;
    }

    private void aboutMe() {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About The Developer");
        alert.setHeaderText("Ketan Sharma Dwivedi");
        alert.setContentText("I am interesting to develop web application and mobile application as well" +
                ".Connect-4-Game is my first application");
        alert.show();
    }

    private void aboutGame() {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect-4-Game");
        alert.setHeaderText("How to play");
        alert.setContentText("Connect-4-Game is a two player connection game in which the " +
                "players first choose a color and then take turns dropping colored disc " +
                "from the top into a seven column,six row vertically suspended grid. "+
                "The pieces fall straight down,occupying the next available space within the column."+
                "The objective of the game is to be the first to form a horizontal,vertical,"+
                "or diagonal line of four of one's own discs.Connect-4-Game is a solved game."+
                "The first player can always win by playing the right moves");
        alert.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
