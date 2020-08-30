package com.Ketansharma.connect;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
    private static final int COLUMNS=7;
    private static final int ROWS=6;
    private static final int CIRCLE_DIAMETER=80;
    private static final String discColor1="#24303E";
    private static final String discColor2="#4CAA88";

       private static String PLAYER_ONE="Player One";
    private static String PLAYER_TWO="Player Two";
    private boolean isPlayerOneTurn=true;
    private Disc[][] insertedDiscsArray=new Disc[ROWS][COLUMNS];//for structcal chanegs


    @FXML
    public GridPane rootGridPane;
    @FXML
    public Pane insertedDiscsPane;
    @FXML
    public Label playerNameLabel;
    @FXML
    public TextField Player_One;
    public TextField Player_Two;
    @FXML
    Button SetName;

    private boolean isAllowedToInsert=true;//help to avoid same color disc multiple time

    public void createPlayground()
    {
        Platform.runLater(()->SetName.requestFocus());
        Shape rectangleWithHoles=GameStructuralGrid();
        rootGridPane.add(rectangleWithHoles,0,1);
        List<Rectangle> rectangleList=CreateClickableColumns();
        for(Rectangle rectangle:rectangleList) {
            rootGridPane.add(rectangle, 0, 1);
        }
        SetName.setOnAction(event ->
        {
            PLAYER_ONE=Player_One.getText();
            PLAYER_TWO=Player_Two.getText();
            playerNameLabel.setText(isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO);
        });
    }
    private Shape GameStructuralGrid()
    {
        Shape rectangleWithHoles=new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
        for(int row=0;row<ROWS;row++) {

            for (int col = 0; col < COLUMNS; col++) {
                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2);
                circle.setCenterX(CIRCLE_DIAMETER / 2);
                circle.setCenterY(CIRCLE_DIAMETER / 2);
                circle.setSmooth(true);
                circle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
                circle.setTranslateY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
            }
        }
        rectangleWithHoles.setFill(Color.WHITE);

        return rectangleWithHoles;
    }
    private List<Rectangle> CreateClickableColumns()
    {
        List<Rectangle> rectangleList=new ArrayList<>();
        for(int r=0;r<COLUMNS;r++) {
            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(r*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER / 4);
            rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
            final int column=r;
            rectangle.setOnMouseClicked(event -> {
                if(isAllowedToInsert) {
                    isAllowedToInsert=false;
                    insertedDisc(new Disc(isPlayerOneTurn), column);
                }
            });
            rectangleList.add(rectangle);
        }
        return rectangleList;

    }

    private void insertedDisc(Disc disc,int column) {

        int row=ROWS-1;
        while (row >= 0)
        {
            if(getDiscIfPresent(row,column)==null)
            {
                break;
            }
            else
            {
                row--;
            }
        }
        if(row<0)              //full
            return;
        insertedDiscsArray[row][column]=disc;
        insertedDiscsPane.getChildren().add(disc);
        disc.setTranslateX(column*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER / 4);
        int currentRow=row;
        TranslateTransition translateTransition=new TranslateTransition(Duration.seconds(0.5),disc);

        translateTransition.setToY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
        translateTransition.setOnFinished(event -> {
            isAllowedToInsert=true;//next disc is allowed
            if(gameEnded(currentRow , column))
            {
               gameOver();
               return;
            }
            isPlayerOneTurn=!isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO);
        });
        translateTransition.play();
    }



    private boolean gameEnded(int row, int column) {

        //vertical point
    List<Point2D> verticalPoint= IntStream.rangeClosed(row-3,row+3)//help to get row value
                .mapToObj(r->new Point2D(r,column)).//Point 2D x,y
                    collect(Collectors.toList());

//Horizontal point
        List<Point2D> horizontalPoint= IntStream.rangeClosed(column-3,column+3)//help to get row value
                .mapToObj(c->new Point2D(row,c)).//Point 2D x,y
                collect(Collectors.toList());
        Point2D startpoint1=new Point2D(row-3,column+3);
        List<Point2D> diagonal1points=IntStream.rangeClosed(0,6).
                mapToObj(i->startpoint1.add(i,-i)).
                collect(Collectors.toList());
        Point2D startpoint2=new Point2D(row-3,column-3);
        List<Point2D> diagonal2points=IntStream.rangeClosed(0,6).
                mapToObj(i->startpoint2.add(i,i)).
                collect(Collectors.toList());
        boolean isEnded=checkCombination(verticalPoint)|| checkCombination(horizontalPoint)||checkCombination(diagonal1points)||checkCombination(diagonal2points);
        return isEnded;
    }

    private boolean checkCombination(List<Point2D> points) {
        int chain=0;
        for(Point2D point: points)
        {
            int rowIndexForArray=(int) point.getX();
            int columnIndexForArray=(int) point.getY();
            Disc disc=getDiscIfPresent(rowIndexForArray,columnIndexForArray);
            if(disc !=null && disc.isPlayerOneMove==isPlayerOneTurn) {
                chain++;
                if (chain == 4) {
                    return true;
                }
            }
            else {
                chain=0;
            }
        }
        return  false;
    }

    private Disc getDiscIfPresent(int row,int column)//prevent to array index out bounds
    {
        if(row>=ROWS || row<0 || column>=COLUMNS || column<0)
        {
            return null;
        }
        else {
            return insertedDiscsArray[row][column];
        }
    }
    private void gameOver() {

        String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
        System.out.println("Winner is:" + winner);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect-4-Game");
        alert.setHeaderText("The Winner is :" + winner);
        alert.setContentText("Want to play again?");
        ButtonType yesbtn = new ButtonType("Yes");
        ButtonType nobtn = new ButtonType("No,Exit");
        alert.getButtonTypes().setAll(yesbtn, nobtn);
        Platform.runLater(() -> {
            Optional<ButtonType> btnclicked = alert.showAndWait();
            if (btnclicked.isPresent() && btnclicked.get() == yesbtn) {
                resetGame();
                //new game
            } else {
                Platform.exit();
                System.exit(0);
                //exit game
            }

        });
    }
    public void resetGame() {

        insertedDiscsPane.getChildren().clear();
        for(int row=0; row<insertedDiscsArray.length;row++)
        {
            for(int col=0;col<insertedDiscsArray.length;col++)
            {
             insertedDiscsArray[row][col]=null;
            }
        }
        isPlayerOneTurn=true;//by default player one is first start
        playerNameLabel.setText(PLAYER_ONE);
        createPlayground();
    }

    private static class Disc extends Circle
{
    private final  boolean isPlayerOneMove;
    public Disc(boolean isPlayerOneMove)
    {
        this.isPlayerOneMove=isPlayerOneMove;
        setRadius(CIRCLE_DIAMETER/2);
        setCenterX(CIRCLE_DIAMETER/2);
        setCenterY(CIRCLE_DIAMETER/2);
        setFill(isPlayerOneMove?Color.valueOf(discColor1):Color.valueOf(discColor2));
    }
}
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
