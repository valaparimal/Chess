import java.io.File;
import java.util.Stack;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Chess extends Application{

    private Stage currentStage;
    private Button playButton;
    private StackPane rootPane;
    private GridPane chessBord;
    private StackPane[][] oneBox;
    private Scene scene;
    private Text[][] texts;
    private String[] black= {"♜","♞","♝","♚","♛","♟"};
    private String[] white = {"♖","♘","♗","♔","♕","♙"};
    private Stack<String> armyContainer ;
    private boolean[][] canArmyMove;
    private boolean[][] canArmyAttack;
    private boolean isContinueGame;
    private Text result;
    private HBox hBox = new HBox();
    private boolean continueGame;
    private static int[] storeIndexOfChackedKing;
    private String backgroundImagePath = "C:\\java\\JavaFx\\src\\ChessBackground(1).jpg";
    private String spinnerSoundString ="C:\\java\\JavaFx\\src\\spinnerSound.mp3";
    private String walkSoundString ="C:\\java\\JavaFx\\src\\walkSound.mp3";
    private String winnerSound = "C:\\Programing Languages\\Game\\Zero-Chokadi\\Crystal_Piano.m4a";
    private Media sound = new Media(new File(spinnerSoundString).toURI().toString());
    private MediaPlayer soundPlayer;
    private MediaView soundContainerVeiw = new MediaView(soundPlayer);

    public static void main(String args[]){
        Application.launch(args);
    }

    public void start(Stage primaryStage){

        currentStage=primaryStage;
        createStartButton();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess");
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();

    }

    public void createStartButton(){

        playButton = new Button();
        playButton.setText("Play");
        playButton.setFont(Font.font(40));
        playButton.setBackground(Background.fill(Color.BROWN));
        playButton.setTextFill(Color.WHITE);
        playButton.setOpacity(0.9);
        playButton.setShape(new Circle(40));
        
        playButton.setBorder(Border.stroke(Color.BLACK));
        playButton.setCursor(Cursor.HAND);

        Image backgroundImage = new Image(new File(backgroundImagePath).toURI().toString());
        ImageView backgrouImageView = new ImageView(backgroundImage);
        backgrouImageView.setFitHeight(700);
        backgrouImageView.setFitWidth(700);
        backgrouImageView.setOpacity(0.7);

        StackPane buttonContainerStackPane = new StackPane(backgrouImageView,playButton,soundContainerVeiw);
        buttonContainerStackPane.setBackground(Background.fill(Color.LIGHTYELLOW));

        scene = new Scene(buttonContainerStackPane,800,800);
        currentStage.setScene(scene);

        playButton.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent arg0){

                sound = new Media(new File(spinnerSoundString).toURI().toString());
                soundPlayer = new MediaPlayer(sound);
                soundPlayer.play();
                RotateTransition rotateTransition = new RotateTransition(Duration.millis(500),backgrouImageView);
                rotateTransition.setByAngle(90); 
                rotateTransition.setAxis(Rotate.Y_AXIS);
                rotateTransition.play();
                new Thread(()->{
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                    }
                    playButton.setFont(Font.font(0));
                }).start();

                playButton.setOnMouseExited(new EventHandler<MouseEvent>(){
                    public void handle(MouseEvent arg0){
                        
                        new Thread(()->{
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }   
                            soundPlayer.pause();
                        }).start();
                        rootPane=buttonContainerStackPane;
                        playChess(currentStage);
                    }
                });
            }
        });


        playButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent arg0)
            {
                playButton.setFont(Font.font(38));
            }
        });

        playButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent arg0)
            {
                playButton.setFont(Font.font(40));
            }
        });
    }

    public void playChess(Stage newStage){
        rootPane.getChildren().remove(0);
        rootPane.getChildren().remove(0);
        rootPane.getChildren().remove(0);
        armyContainer = new Stack<String>();
        canArmyMove = new boolean[8][8];
        canArmyAttack = new boolean[8][8];
        storeIndexOfChackedKing = new int[2];
        storeIndexOfChackedKing[0]=storeIndexOfChackedKing[1]=-1;

        chessBord = new GridPane();
        oneBox = new StackPane[8][8];
        texts = new Text[8][8];
        result = new Text();
        result.setFont(Font.font(100));
        result.setFill(Color.GREEN);
        hBox.setMaxWidth(0);
        hBox.setMaxHeight(0);

        for(int i = 0; i<8 ; i++)
        {
            for(int j = 0 ; j<8 ; j++)
            {
                oneBox[i][j] = new StackPane();
                oneBox[i][j].setMinHeight(100);
                oneBox[i][j].setMinWidth(100);

                Rectangle box = new Rectangle(100, 100);
                box.setStyle("-fx-background-radius :100;");
                texts[i][j] = new Text();
                texts[i][j].setFont(Font.font(50));
                texts[i][j].setOpacity(0.7);
                if(i%2==0)
                {
                    if(j%2 == 0)
                    {
                        box.setFill(Color.BROWN);
                        box.setOpacity(0.8);
                    }
                    else
                    {
                        box.setFill(Color.LIGHTYELLOW);
                    }
                }
                else
                {
                    if(j%2 != 0)
                    {
                        box.setFill(Color.BROWN);
                        box.setOpacity(0.8);
                    }
                    else
                    {
                        box.setFill(Color.LIGHTYELLOW);
                    }
                }

                oneBox[i][j].getChildren().addAll(box,texts[i][j]);
                chessBord.add(oneBox[i][j],j,i);

                final int k=i;
                final int l=j;
                oneBox[i][j].setOnMouseMoved(new EventHandler<MouseEvent>() { 
                    public void handle(MouseEvent arg0)
                    {
                        if(((Text) oneBox[k][l].getChildren().get(1)).getText() != "" && !oneBox[k][l].isDisable())
                        {
                            oneBox[k][l].setCursor(Cursor.HAND);
                        }
                    }
                });
            }
        }


        texts[0][0].setText("♜");
        texts[0][1].setText("♞");
        texts[0][2].setText("♝");
        texts[0][3].setText("♚");
        texts[0][4].setText("♛");
        texts[0][5].setText("♝");
        texts[0][6].setText("♞");
        texts[0][7].setText("♜");
        for(int i=0 ; i<8 ; i++)
        { 
            texts[1][i].setText("♟");
        }

        texts[7][0].setText("♖");
        texts[7][1].setText("♘");
        texts[7][2].setText("♗");
        texts[7][3].setText("♔");
        texts[7][4].setText("♕");
        texts[7][5].setText("♗");
        texts[7][6].setText("♘");
        texts[7][7].setText("♖");
        for(int i=0 ; i<8 ; i++)
        {
            texts[6][i].setText("♙");
        }
        
        rootPane.getChildren().addAll(chessBord,result,hBox,soundContainerVeiw);

        chessBord.setRotationAxis(Rotate.Y_AXIS);
        chessBord.setRotate(270);
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(500),chessBord);
        rotateTransition.setByAngle(90); 
        rotateTransition.setAxis(Rotate.Y_AXIS);
        rotateTransition.play();
        

        for(int i=0; i<2 ; i++)
        {
            for(int j=0; j<8 ; j++)
            {
                oneBox[i][j].setDisable(true);
            }
        }
        
        for(int i=0 ; i<8 ; i++)
        {
            for(int j=0 ; j<8 ; j++)
            {
                final int k=i;
                final int l=j;

                oneBox[k][l].setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @SuppressWarnings("null")
                    public void handle(MouseEvent arg0)
                    {
                            if(((Text) oneBox[k][l].getChildren().get(1)).getText() == "o" || ((Shape) oneBox[k][l].getChildren().get(0)).getFill().equals(Color.YELLOW))
                            {
                                sound = new Media(new File(walkSoundString).toURI().toString());
                                soundPlayer = new MediaPlayer(sound);
                                soundPlayer.play();
                                
                                for(int i=0; i<8 ; i++)
                                {
                                    for(int j=0; j<8 ; j++)
                                    {
                                        if(texts[i][j].getOpacity() == 1)
                                        {
                                            System.out.println("move");
                                            ((Text) oneBox[k][l].getChildren().get(1)).setText(texts[i][j].getText());
                                            ((Text) oneBox[k][l].getChildren().get(1)).setOpacity(0.7);
                                            
                                            texts[i][j].setText("");
                                            texts[i][j].setOpacity(0.7);

                                            circleClean(false);
                                            colorClean(Color.YELLOW,Color.RED);
                                            if(texts[0][l].getText().equals(white[5]))
                                            {
                                                givePowerToPown(0,l,white);
                                            }
                                            else if(texts[7][l].getText().equals(black[5]))
                                            {
                                                givePowerToPown(7,l,black);
                                            }

                                            i=8;
                                            break;
                                        }
                                        
                                    }
                                }

                                int z;
                                for( z=0 ; z<6 ; z++)
                                {
                                    // it is white
                                    if(((Text) oneBox[k][l].getChildren().get(1)).getText()==white[z])
                                    {
                                        try {
                                            // see, is there check for black?
                                            if(moveCkeck(false,true ,white))
                                            {
                                                System.out.println("checked");
                                                try {
                                                    ((Shape) oneBox[storeIndexOfChackedKing[0]][storeIndexOfChackedKing[1]].getChildren().get(0)).setFill(Color.RED);
                                                    storeIndexOfChackedKing[0]=storeIndexOfChackedKing[1]=(Integer) null;
                                                    circleClean(false);
                                                    colorClean(false);
                                                } catch (Exception e) {
                                                    System.out.println(e.getMessage());
                                                }
                                                if(!defanceToChack(black, white))
                                                {
                                                    System.out.println("checkmate");
                                                    gameOver(white);
                                                }
                                            }
                                            else
                                            {
                                                circleClean(false);
                                                colorClean(false);
                                                if(!defanceToChack(black, white))
                                                {
                                                    gameOver(null);
                                                }
                                            }
                                        } catch (Exception e) {}
                                        circleClean(false);
                                        colorClean(false);
                                        

                                        //set disable to white
                                        for(int i=0; i<8 ; i++)
                                        {
                                            for(int j=0; j<8 ; j++)
                                            {
                                                boolean isWhite = false;
                                                for(int p=0 ; p<6 ; p++)
                                                {
                                                    if(((Text) oneBox[i][j].getChildren().get(1)).getText() == white[p] || ((Text) oneBox[i][j].getChildren().get(1)).getText() == "")
                                                    {
                                                        oneBox[i][j].setDisable(true);
                                                        isWhite=true;
                                                    }
                                                }
                                                if(!isWhite)
                                                {
                                                    oneBox[i][j].setDisable(false);
                                                } 
                                            }
                                            
                                        }

                                        break;
                                    }
                                }

                                // it is black
                                if(z==6)
                                {
                                    // see, is there check for white?

                                    try {
                                        if(moveCkeck(false, true,black))
                                        {
                                            System.out.println("chcked");
                                            
                                            try {
                                                ((Shape) oneBox[storeIndexOfChackedKing[0]][storeIndexOfChackedKing[1]].getChildren().get(0)).setFill(Color.RED);
                                                storeIndexOfChackedKing[0]=storeIndexOfChackedKing[1]=(Integer) null;
                                                circleClean(false);
                                                colorClean(false);
                                            } catch (Exception e) {
                                                System.out.println(e.getMessage());
                                            }
    
                                            if(!defanceToChack(white,black))
                                            {
                                                System.out.println("checkmate");
                                                gameOver(black);
                                            }
                                        }
                                        else
                                        {
                                            circleClean(false);
                                            colorClean(false);
    
                                            if(!defanceToChack(white,black))
                                            {
                                                gameOver(null);
                                            }
                                        }
                                    } catch (Exception e) {}

                                    circleClean(false);
                                    colorClean(false);
                                    
                                    for(int i=0; i<8 ; i++)
                                    {
                                        for(int j=0; j<8 ; j++)
                                        {
                                            boolean isBlack = false;
                                            for(int p=0 ; p<6 ; p++)
                                            {
                                                if(((Text) oneBox[i][j].getChildren().get(1)).getText() == black[p] || ((Text) oneBox[i][j].getChildren().get(1)).getText() == "")
                                                {
                                                    oneBox[i][j].setDisable(true);
                                                    isBlack=true;
                                                }
                                            }
                                            if(!isBlack)
                                            {
                                                oneBox[i][j].setDisable(false);
                                            }
                                        }
                                    }
                                }
                            }
                            else
                            {
                                circleClean(false);
                                colorClean(false);
                                
                                texts[k][l].setOpacity(1);

                                try {
                                    if(((Text) oneBox[k][l].getChildren().get(1)).getText() == "♟")
                                    {
                                        blackpown(true,true,k,l);
                                        texts[k][l].setOpacity(1);
                                    }
                                    else if(((Text) oneBox[k][l].getChildren().get(1)).getText() == "♜" || ((Text) oneBox[k][l].getChildren().get(1)).getText()=="♖")
                                    {
                                        if(((Text) oneBox[k][l].getChildren().get(1)).getText() == "♜")
                                        {
                                            rook(true,true,k, l,white);
                                        }
                                        else{
                                            rook(true,true,k, l, black);
                                        }
                                        texts[k][l].setOpacity(1);
                                    }
                                    else if(((Text) oneBox[k][l].getChildren().get(1)).getText() == "♞" || ((Text) oneBox[k][l].getChildren().get(1)).getText()=="♘")
                                    {
                                        if(((Text) oneBox[k][l].getChildren().get(1)).getText() == "♞")
                                        {
                                            horse(true,true,k, l, white);
                                        }
                                        else
                                        {
                                            horse(true,true,k, l, black);
                                        }
                                        texts[k][l].setOpacity(1);
                                    }
                                    else if(((Text) oneBox[k][l].getChildren().get(1)).getText() == "♝" || ((Text) oneBox[k][l].getChildren().get(1)).getText()=="♗")
                                    {
                                        if(((Text) oneBox[k][l].getChildren().get(1)).getText() == "♝")
                                        {
                                            camel(true,true,k, l, white);
                                        }
                                        else
                                        {
                                            camel(true,true,k, l, black);
                                        }
                                        texts[k][l].setOpacity(1);
                                    }
                                    else if(((Text) oneBox[k][l].getChildren().get(1)).getText() == "♚" || ((Text) oneBox[k][l].getChildren().get(1)).getText()=="♔")
                                    {
                                        if(((Text) oneBox[k][l].getChildren().get(1)).getText() == "♚" )
                                        {
                                            king(k, l, white);
                                        }
                                        else
                                        {
                                            king(k, l, black);
                                        }
                                        texts[k][l].setOpacity(1);
                                    }
                                    else if(((Text) oneBox[k][l].getChildren().get(1)).getText() == "♛" || ((Text) oneBox[k][l].getChildren().get(1)).getText()=="♕")
                                    {
                                        if(((Text) oneBox[k][l].getChildren().get(1)).getText() == "♛" )
                                        {
                                            queen(true,true,k, l,white);
                                        }
                                        else{
                                            queen(true,true,k, l,black);
                                        }
                                        texts[k][l].setOpacity(1);
                                    }
                                    else if(((Text) oneBox[k][l].getChildren().get(1)).getText() == "♙")
                                    {
                                        whitepown(true,true,k, l);
                                        texts[k][l].setOpacity(1);
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {}

                                try {
                                    ((Shape) oneBox[storeIndexOfChackedKing[0]][storeIndexOfChackedKing[1]].getChildren().get(0)).setFill(Color.RED);
                                    storeIndexOfChackedKing[0]=storeIndexOfChackedKing[1]=-1;
                                } catch (Exception e) {}
                            }

                    }
                });
            }
        }

        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent arg0)
            {
                for(int k=0; k<8 ; k++)
                {
                    for(int l=0; l<8 ; l++)
                    {
                        if(((Text) oneBox[k][l].getChildren().get(1)).getText() == "o")
                        {
                            ((Text) oneBox[k][l].getChildren().get(1)).setFill(Color.GREEN);
                            oneBox[k][l].setDisable(false);
                        }
                        else
                        {
                            ((Text) oneBox[k][l].getChildren().get(1)).setFill(Color.BLACK);
                        }
                    }
                }
            }
        });
    }

    // functions

    public void circleClean(boolean makeCircle)
    {
        for(int i=0; i<8 ; i++)
        {
            for(int j=0; j<8 ; j++)
            {
                if(makeCircle && canArmyMove[i][j])
                {
                    texts[i][j].setText("o");
                    canArmyMove[i][j]=false;
                    continue;
                }
                if(texts[i][j].getText() == "o")
                {
                    texts[i][j].setText("");
                }
                texts[i][j].setOpacity(0.7);
            }
        }
    }


    public void colorClean(boolean canPaint)
    {
        for(int i=0; i<8 ; i++)
        {
            for(int j=0; j<8 ; j++)
            {
                if(canPaint && canArmyAttack [i][j]==true)
                {
                    ((Shape) oneBox[i][j].getChildren().get(0)).setFill(Color.YELLOW);
                    canArmyAttack[i][j]=false;
                    continue;
                }
                if(((Shape) oneBox[i][j].getChildren().get(0)).getFill().equals(Color.YELLOW))
                {
                    oneBox[i][j].setDisable(true);
                    if(i%2==0)
                    {
                        if(j%2 == 0)
                        {
                            ((Shape) oneBox[i][j].getChildren().get(0)).setFill(Color.BROWN);
                        }
                        else
                        {
                            ((Shape) oneBox[i][j].getChildren().get(0)).setFill(Color.LIGHTYELLOW);
                        }
                    }
                    else
                    {
                        if(j%2 != 0)
                        {
                            ((Shape) oneBox[i][j].getChildren().get(0)).setFill(Color.BROWN);
                        }
                        else
                        {
                            ((Shape) oneBox[i][j].getChildren().get(0)).setFill(Color.LIGHTYELLOW);
                        }
                    }
                }
            }
        }                    
    }

    public void colorClean(Color c1 , Color c2)
    {
        for(int i=0; i<8 ; i++)
        {
            for(int j=0; j<8 ; j++)
            {
                if(((Shape) oneBox[i][j].getChildren().get(0)).getFill().equals(c1) || ((Shape) oneBox[i][j].getChildren().get(0)).getFill().equals(c2))
                {
                    if(((Shape) oneBox[i][j].getChildren().get(0)).getFill().equals(Color.YELLOW))oneBox[i][j].setDisable(true);
                    if(i%2==0)
                    {
                        if(j%2 == 0)
                        {
                            ((Shape) oneBox[i][j].getChildren().get(0)).setFill(Color.BROWN);
                        }
                        else
                        {
                            ((Shape) oneBox[i][j].getChildren().get(0)).setFill(Color.LIGHTYELLOW);
                        }
                    }
                    else
                    {
                        if(j%2 != 0)
                        {
                            ((Shape) oneBox[i][j].getChildren().get(0)).setFill(Color.BROWN);
                        }
                        else
                        {
                            ((Shape) oneBox[i][j].getChildren().get(0)).setFill(Color.LIGHTYELLOW);
                        }
                    }
                }
            }
        }                    
    }

    public boolean isThisRightMove(String[] oppoArmy)
    {
        boolean move;
        // see , if there is check for me then don't move
        if(moveCkeck(false,false,oppoArmy))
        {
            move=false;
        }
        else{
            move=true;
        }
        circleClean(false);
        colorClean(false);

        return move;
    }

    public boolean isChackORAttack(boolean saveKing,boolean chackPaint,final int k , final int l , final int newk , final int newl , String[] oppoArmy)
    {
        for(int i=0 ; i<6 ; i++)
        {
            if(texts[newk][newl].getText().equals(oppoArmy[i]))
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,newk,newl,oppoArmy);
                }
                
                if(move)
                {
                    if(saveKing) 
                    {
                        canArmyAttack[newk][newl]=true;
                    }
                    oneBox[newk][newl].setDisable(false);
    
                    if(i==3)
                    {
                        if(chackPaint)
                        {
                            storeIndexOfChackedKing[0]=newk;
                            storeIndexOfChackedKing[1]=newl;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean defanceToChack(String[] myArmy , String[] oppoArmy)
    {
        isContinueGame=false;
        Stack<String> armyContainerStack = new Stack<String>();
        for(int i=0 ; i<8 ; i++)
        {
            for(int j=0 ; j<8 ; j++)
            {
                for(int z = 0 ; z<6 ; z++)
                {
                    if(texts[i][j].getText().equals(oppoArmy[z]))armyContainerStack.push(oppoArmy[z]);
                    if(texts[i][j].getText().equals(myArmy[z]))
                    {
                        armyContainerStack.push(myArmy[z]);
                        if(z==0)
                        {
                            rook(true, false,i, j, oppoArmy);
                        }
                        else if(z==1)
                        {
                            horse(true, false,i, j, oppoArmy);
                        }
                        else if (z==2) {
                            camel(true,false, i, j, oppoArmy);
                        }
                        else if (z==3) {
                            king(i, j, oppoArmy);
                        }
                        else if(z==4)
                        {
                            queen(true,false, i, j, oppoArmy);
                        }
                        else {
                            if(myArmy[5].equals(white[5]))
                            {
                                whitepown(true,false, i, j);
                            }
                            else{
                                blackpown(true,false, i, j);
                            }
                        }
                    }
                }
                System.out.println(i + " " + j+ " " +isContinueGame);
                if (isContinueGame) {
                    if(armyContainerStack.size()<3) continue; // if, in armyContainerStack only two value then may be it is kings
                    return true;
                }
            }
        }

        if(armyContainerStack.size()==2)return false;
        
        return isContinueGame;
    }

    // Army function


    public boolean blackpown(boolean saveKing,boolean chackPaint, int k , int l)
    {
        if(((Text) oneBox[k+1][l].getChildren().get(1)).getText() == "")
        {
            boolean move=true;
            
            if(saveKing)
            {
                armyContainer.push(texts[k][l].getText());
                texts[k+1][l].setText(texts[k][l].getText());
                texts[k][l].setText("");
                move=isThisRightMove(white);
                texts[k+1][l].setText("");
                texts[k][l].setText(armyContainer.pop());
            }

            if(move)
            {
                if(saveKing)
                {
                    canArmyMove[k+1][l]=true;
                    isContinueGame=true;
                }
            }
            
            if(k==1)
            {
                if(((Text) oneBox[k+2][l].getChildren().get(1)).getText() == "")
                {
                    if (saveKing) {                       
                        armyContainer.push(texts[k][l].getText());
                        texts[k+2][l].setText(texts[k][l].getText());
                        texts[k][l].setText("");
                        move=isThisRightMove(white);
                        texts[k+2][l].setText("");
                        texts[k][l].setText(armyContainer.pop());

                        if (move) {
                            canArmyMove[k+2][l]=true;
                        }
                    }
                }
            }
        }
        
        for(int i=0; i<6 ; i++)
        {
            try {
                if(((Text) oneBox[k+1][l-1].getChildren().get(1)).getText() == white[i])
                {
                    boolean move=true;
                    if(saveKing)
                    {
                        armyContainer.push(texts[k][l].getText());
                        armyContainer.push(texts[k+1][l-1].getText());
                        texts[k+1][l-1].setText(texts[k][l].getText());
                        texts[k][l].setText("");

                        move=isThisRightMove(white);
                        
                        texts[k+1][l-1].setText(armyContainer.pop());
                        texts[k][l].setText(armyContainer.pop());
                    }

                    if(move)
                    {
                        if(saveKing) {
                            canArmyAttack[k+1][l-1]=true;
                            isContinueGame=true;
                        }
                        oneBox[k+1][l-1].setDisable(false);
                        if(i==3)
                        {
                            if (chackPaint) {
                                storeIndexOfChackedKing[0]=k+1;
                                storeIndexOfChackedKing[1]=l-1;
                            }
                            return true;
                        }
                    }
                }
            } catch (Exception e) { }

            try {
                if(((Text) oneBox[k+1][l+1].getChildren().get(1)).getText() == white[i])
                {
                    boolean move=true;
                    if(saveKing)
                    {
                        armyContainer.push(texts[k][l].getText());
                        armyContainer.push(texts[k+1][l+1].getText());
                        texts[k+1][l+1].setText(texts[k][l].getText());
                        texts[k][l].setText("");

                        move=isThisRightMove(white);
                        
                        texts[k+1][l+1].setText(armyContainer.pop());
                        texts[k][l].setText(armyContainer.pop());
                    }

                    if(move)
                    {
                        if(saveKing) {
                            canArmyAttack[k+1][l+1]=true;
                            isContinueGame=true;
                        }
                        oneBox[k+1][l+1].setDisable(false);
                        if(i==3)
                        {
                            if (chackPaint) {
                                storeIndexOfChackedKing[0]=k+1;
                                storeIndexOfChackedKing[1]=l+1;
                            }
                            return true;
                        }
                    }
                }
            } catch (Exception e) {}
        } 
        if (saveKing) {
            circleClean(true);
            colorClean(true);
        }
        else
        {
            colorClean(false);
        }
        return false;                        
    }

    public boolean rook(boolean saveKing,boolean chackPaint, int k , int l,String[] oppoArmy)
    {
        try {
            int down=k;
            while(((Text) oneBox[++down][l].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,down,l,oppoArmy);
                    if(move)
                    {
                        canArmyMove[down][l]=true;
                    }
                }
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,down,l,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
        } catch (Exception e) {}

        try {
            int up=k;
            while(((Text) oneBox[--up][l].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,up,l,oppoArmy);
                    if(move)
                    {
                        canArmyMove[up][l]=true;
                    }
                }
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,up,l,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
        } catch (Exception e) {}

        try {
            int right=l;
            while(((Text) oneBox[k][++right].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,k,right,oppoArmy);
                    if(move)
                    {
                        canArmyMove[k][right]=true;
                    }
                }
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,k,right,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
        } catch (Exception e) {}
        
        try {
            int left=l;
            while(((Text) oneBox[k][--left].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,k,left,oppoArmy);
                    if (move) {
                        canArmyMove[k][left]=true;
                    }
                }
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,k,left,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
        } catch (Exception e) {} 

        if (saveKing) {
            circleClean(true);
            colorClean(true);
        }
        else
        {
            colorClean(false);
        }
        return false;
    }

    public boolean horse(boolean saveKing ,boolean chackPaint, int k , int l,String[] oppoArmy)
    {
        try {
            int down=k+2;
            int downR=l+1;
            if(((Text) oneBox[down][downR].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,down,downR,oppoArmy);
                    if(move)
                    {
                        canArmyMove[down][downR]=true;
                    }
                }
            }
            else
            {
                if(isChackORAttack(saveKing,chackPaint,k,l,down,downR,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
                {
                    return true;
                }
            }
        } catch (Exception e) {}

        try {
            int up=k-2;
            int upR = l+1;
            if(((Text) oneBox[up][upR].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,up,upR,oppoArmy);
                    if (move) {
                        
                        canArmyMove[up][upR]=true;
                    }
                }
            }
            else
            {
                if(isChackORAttack(saveKing,chackPaint,k,l,up,upR,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
                {
                    return true;
                }
            }
            
        } catch (Exception e) {}

        try {
            int down=k+2;
            int downL=l-1;
            if(((Text) oneBox[down][downL].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,down,downL,oppoArmy);
                    if (move) {
                        canArmyMove[down][downL]=true;
                    }
                }
            }
            else
            {
                if(isChackORAttack(saveKing,chackPaint,k,l,down,downL,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
                {
                    return true;
                }
            }
        } catch (Exception e) {}
        
        try {
            int up=k-2;
            int upL=l-1;
            if(((Text) oneBox[up][upL].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,up,upL,oppoArmy);
                    if (move) {
                        canArmyMove[up][upL]=true;
                    }
                }
            }
            else
            {
                if(isChackORAttack(saveKing,chackPaint,k,l,up,upL,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
                {
                    return true;
                }
            }
        } catch (Exception e) {}

        //  

        try {
            int rightU=k+1;
            int right=l+2;
            if(((Text) oneBox[rightU][right].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,rightU,right,oppoArmy);
                    if (move) {
                        canArmyMove[rightU][right]=true;
                    }
                }
            }
            else
            {
                if(isChackORAttack(saveKing,chackPaint,k,l,rightU,right,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
                {
                    return true;
                }
            }
        } catch (Exception e) {}

        try {
            int rightD=k-1;
            int right=l+2;
            if(((Text) oneBox[rightD][right].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,rightD,right,oppoArmy);
                    if (move) {
                        canArmyMove[rightD][right]=true;
                    }
                }
            }
            else
            {
                if(isChackORAttack(saveKing,chackPaint,k,l,rightD,right,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
                {
                    return true;
                }
            }
        } catch (Exception e) {}

        try {
            int leftU=k-1;
            int left=l-2;
            if(((Text) oneBox[leftU][left].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,leftU,left,oppoArmy);
                    if (move) {
                        canArmyMove[leftU][left]=true;
                    }
                }
            }
            else
            {
                if(isChackORAttack(saveKing,chackPaint,k,l,leftU,left,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
                {
                    return true;
                }
            }
        } catch (Exception e) {}
        
        try {
            int leftD=k+1;
            int left=l-2;
            if(((Text) oneBox[leftD][left].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,leftD,left,oppoArmy);
                    if (move) {
                        canArmyMove[leftD][left]=true;
                    }
                }
            }
            else
            {    
                if(isChackORAttack(saveKing,chackPaint,k,l,leftD,left,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
                {
                    return true;
                }
            }
        } catch (Exception e) {}
 
        if (saveKing) {
            circleClean(true);
            colorClean(true);
        }
        else
        {
            colorClean(false);
        }
        return false;
    }

    public boolean camel (boolean saveKing ,boolean chackPaint,int k , int l , String[] oppoArmy)
    {
        try {
            int down=k;
            int downR=l;
            while(((Text) oneBox[++down][++downR].getChildren().get(1)).getText() == "")
            {
                if(saveKing)
                {
                    boolean move=isChangePlace(k,l,down,downR,oppoArmy);
                    if (move) {
                        canArmyMove[down][downR]=true;
                    }
                }
            }
            
            if(isChackORAttack(saveKing,chackPaint,k,l,down,downR,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
        } catch (Exception e) {}

        try {
            int up=k;
            int upR = l;
            while(((Text) oneBox[--up][++upR].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,up,upR,oppoArmy);
                    if (move) {
                        canArmyMove[up][upR]=true;
                    }
                }
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,up,upR,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
        } catch (Exception e) {}

        try {
            int down=k;
            int downL=l;
            while(((Text) oneBox[++down][--downL].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,down,downL,oppoArmy);
                    if (move) {
                        canArmyMove[down][downL]=true;
                    }
                }
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,down,downL,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
        } catch (Exception e) {}
        
        try {
            int up=k;
            int upL=l;
            while(((Text) oneBox[--up][--upL].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,up,upL,oppoArmy);
                    if (move) {
                        canArmyMove[up][upL]=true;
                    }
                }
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,up,upL,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
        } catch (Exception e) {}
 
        if (saveKing) {
            circleClean(true);
            colorClean(true);
        }
        else
        {
            colorClean(false);
        }
        return false;
    }

    public void king(final int k,final int l,String[] oppoArmy)
    {
        try {
            int down=k;
            kingProcess(k,l,++down,l,oppoArmy);
            
        } catch (Exception e) {}

        try {
            int up=k;
            kingProcess(k,l,--up,l,oppoArmy);
            
        } catch (Exception e) {}

        try {
            int right=l;
            kingProcess(k,l,k,++right,oppoArmy);
            
        } catch (Exception e) {}
        
        try {
            int left=l;
            kingProcess(k,l,k,--left,oppoArmy);
        } catch (Exception e) {}

        try {
            int down=k;
            int downR=l;
            kingProcess(k,l,++down,++downR,oppoArmy);
        } catch (Exception e) {}

        try {
            int up=k;
            int upR = l;
            kingProcess(k,l,--up,++upR,oppoArmy);
        } catch (Exception e) {}

        try {
            int down=k;
            int downL=l;
            kingProcess(k,l,++down,--downL,oppoArmy);
        } catch (Exception e) {}
        
        try {
            int up=k;
            int upL=l;
            kingProcess(k,l,--up,--upL,oppoArmy);
        } catch (Exception e) {}

        circleClean(true);
        colorClean(true);
    }

    public void kingProcess(final int k , final int l , final int newk , final int newl, String[] oppoArmy)
    {
        if(((Text) oneBox[newk][newl].getChildren().get(1)).getText() == "")
        {
            armyContainer.push(texts[k][l].getText());
            ((Text) oneBox[newk][newl].getChildren().get(1)).setText(texts[k][l].getText());
            ((Text) oneBox[k][l].getChildren().get(1)).setText("");

            if(!moveCkeck(false, false,oppoArmy))
            {
                canArmyMove[newk][newl]=true;
                isContinueGame=true;
            }
            else
            {
                ((Text) oneBox[newk][newl].getChildren().get(1)).setText("");
                oneBox[newk][newl].setDisable(true);
            }
            ((Text) oneBox[k][l].getChildren().get(1)).setText(armyContainer.pop());
            colorClean(false);
            
        }
        for(int temp=0; temp<6 ; temp++)
        {
            if(((Text) oneBox[newk][newl].getChildren().get(1)).getText().equals(oppoArmy[temp]))
            {                
                armyContainer.push(texts[k][l].getText());
                armyContainer.push(texts[newk][newl].getText());
                ((Text) oneBox[newk][newl].getChildren().get(1)).setText(texts[k][l].getText());
                ((Text) oneBox[k][l].getChildren().get(1)).setText("");

                boolean willDath=false;
                if(moveCkeck(false,false, oppoArmy))
                {
                    willDath=true;
                    oneBox[newk][newl].setDisable(true);
                }
                texts[newk][newl].setText(armyContainer.pop());
                texts[k][l].setText(armyContainer.pop());
                colorClean(false);
                if(!willDath)
                {
                    isContinueGame=true;
                    canArmyAttack[newk][newl]=true;
                    oneBox[newk][newl].setDisable(false);
                }
            }
        }
    }

    public boolean moveCkeck(boolean saveKing ,boolean chackPaint,String[] oppoArmy)
    {
        for(int i=0; i<8 ; i++)
        {
            for(int j=0 ; j<8 ; j++)
            {
                for(int z=0; z<6 ; z++)
                {
                    if(texts[i][j].getText().equals(oppoArmy[z]))
                    {
                        if(z==0)
                        {
                            if(oppoArmy[0].equals(white[0]))//if true then color of rook is white, oppArmy for rook is black
                            {
                                if(rook(saveKing,chackPaint,i,j,black))
                                {
                                    System.out.println("rook");
                                    return true;
                                }
                            }
                            else
                            {
                                if(rook(saveKing,chackPaint,i,j,white))
                                {
                                    System.out.println("rook");
                                    return true;
                                }
                            }
                        }
                        else if(z==1)
                        {
                            if(oppoArmy[1].equals(white[1]))//if true then color of horse is white, oppArmy for horse is black
                            {
                                if(horse(saveKing,chackPaint,i,j,black))
                                {
                                    System.out.println("horse");
                                    return true;
                                }
                            }
                            else
                            {
                                if(horse(saveKing,chackPaint,i,j,white))
                                {
                                    System.out.println("horse");
                                    return true;
                                }   
                            }
                        }
                        else if(z==2)
                        {
                            if(oppoArmy[2].equals(white[2]))//if true then color of camel is white, oppArmy for camel is black
                            {
                                if(camel(saveKing,chackPaint,i,j,black))
                                {
                                    System.out.println("camel");
                                    return true;
                                }
                            }
                            else
                            {
                                if(camel(saveKing,chackPaint,i,j,white))
                                {
                                    System.out.println("camel");
                                    return true;
                                }   
                            }
                        }
                        else if(z==3)
                        {
                            if(oppoArmy[3].equals(white[3]))//if true then color of king is white, oppArmy for king is black
                            {
                                if(isBehindeKing(i, j, black[3]))
                                {
                                    System.out.println("king");
                                    return true;
                                }
                            }
                            else
                            {
                                if(isBehindeKing(i, j, white[3]))
                                {
                                    System.out.println("king");
                                    return true;
                                }   
                            }
                        }
                        else if(z==4)
                        {
                            if(oppoArmy[4].equals(white[4])) // if true then color of queen is white. so,oppoarmy is black
                            {
                                if(queen(saveKing,chackPaint,i,j,black))
                                {
                                    System.out.println("queen");
                                    return true;
                                }
                            }
                            else{
                                if(queen(saveKing,chackPaint,i,j,white))
                                {
                                    System.out.println("queen");
                                    return true;
                                }    
                            }
                        }
                        else if(oppoArmy[5].equals(white[5])) // if true then color of oppopown is white
                        {
                            if(whitepown(saveKing,chackPaint,i, j))
                            {
                                System.out.println("whitepown");
                                return true;
                            } 
                        }
                        else if(oppoArmy[5].equals(black[5]))
                        {
                            if(blackpown(saveKing,chackPaint,i, j))
                            {
                                System.out.println("blackpown");
                                return true;
                            }        
                        }
                        circleClean(false);                    
                    }
                }
            }
        }  
        return false;                         
    }

    public boolean isBehindeKing(final int k , final int l , String oppoKing)
    {
        try {
            if(texts[k-1][l-1].getText().equals(oppoKing))return true;
        } catch (Exception e) {}
        try {
            if(texts[k-1][l].getText().equals(oppoKing))return true;
        } catch (Exception e) {}
        try {
            if(texts[k-1][l+1].getText().equals(oppoKing))return true;
        } catch (Exception e) {}
        try {
            if(texts[k][l-1].getText().equals(oppoKing))return true;
        } catch (Exception e) {}
        try {
            if(texts[k][l+1].getText().equals(oppoKing))return true;
        } catch (Exception e) {}
        try {
            if(texts[k+1][l-1].getText().equals(oppoKing))return true;
        } catch (Exception e) {}
        try {
            if(texts[k+1][l].getText().equals(oppoKing))return true;
        } catch (Exception e) {}
        try {
            if(texts[k+1][l+1].getText().equals(oppoKing))return true;
        } catch (Exception e) {}

        return false;
    }

    public boolean queen(boolean saveKing,boolean chackPaint,int k , int  l,String[] oppoArmy)
    {
        try {
            int down=k;
            while(((Text) oneBox[++down][l].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,down,l,oppoArmy);
                    if (move) {
                        canArmyMove[down][l]=true;
                    }
                }
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,down,l,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
        } catch (Exception e) {}

        try {
            int up=k;
            while(((Text) oneBox[--up][l].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,up,l,oppoArmy);
                    if (move) {
                        canArmyMove[up][l]=true;
                    }
                }
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,up,l,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
            
        } catch (Exception e) {}

        try {
            int right=l;
            while(((Text) oneBox[k][++right].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,k,right,oppoArmy);
                    if (move) {
                        canArmyMove[k][right]=true;
                    }
                }
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,k,right,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
            
        } catch (Exception e) {}
        
        try {
            int left=l;
            while(((Text) oneBox[k][--left].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,k,left,oppoArmy);
                    if (move) {
                        canArmyMove[k][left]=true;
                    }
                }
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,k,left,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
            
        } catch (Exception e) {}

        try {
            int down=k;
            int downR=l;
            while(((Text) oneBox[++down][++downR].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,down,downR,oppoArmy);
                    if (move) {
                        canArmyMove[down][downR]=true;
                    }
                }
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,down,downR,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
            
        } catch (Exception e) {}

        try {
            int up=k;
            int upR = l;
            while(((Text) oneBox[--up][++upR].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,up,upR,oppoArmy);
                    if (move) {
                        canArmyMove[up][upR]=true;
                    }
                }
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,up,upR,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
            
        } catch (Exception e) {}

        try {
            int down=k;
            int downL=l;
            while(((Text) oneBox[++down][--downL].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,down,downL,oppoArmy);
                    if (move) {
                        canArmyMove[down][downL]=true;
                    }
                }
                ((Text) oneBox[down][downL].getChildren().get(1)).setText("o");
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,down,downL,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
            
        } catch (Exception e) {}
        
        try {
            int up=k;
            int upL=l;
            while(((Text) oneBox[--up][--upL].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                if(saveKing)
                {
                    move=isChangePlace(k,l,up,upL,oppoArmy);
                    if (move) {
                        canArmyMove[up][upL]=true;
                    }
                }
            }
            if(isChackORAttack(saveKing,chackPaint,k,l,up,upL,oppoArmy)) // it is return true if it give chack to oppoKing , if it will attack then paint color to yellow
            {
                return true;
            }
            
        } catch (Exception e) {}
        
        if (saveKing) {
            circleClean(true);
            colorClean(true);
        }
        else
        {
            colorClean(false);
        }

        return false;
    }

    public boolean whitepown(boolean saveKing ,boolean chackPaint,final int k , final int l)
    {
        try {
            if(((Text) oneBox[k-1][l].getChildren().get(1)).getText() == "")
            {
                boolean move=true;
                
                if(saveKing)
                {
                    armyContainer.push(texts[k][l].getText());
                    texts[k-1][l].setText(texts[k][l].getText());
                    texts[k][l].setText("");
                    move=isThisRightMove(black);
                    texts[k-1][l].setText("");
                    texts[k][l].setText(armyContainer.pop());
                }

                if(move)
                {
                    if (saveKing) {
                        canArmyMove[k-1][l]=true;
                        isContinueGame=true;
                    }
                }

                if(k==6)
                {
                    if(((Text) oneBox[k-2][l].getChildren().get(1)).getText() == "")
                    {
                        if (saveKing) {
                            armyContainer.push(texts[k][l].getText());
                            texts[k-2][l].setText(texts[k][l].getText());
                            texts[k][l].setText("");
                            move=isThisRightMove(black);
                            texts[k-2][l].setText("");
                            texts[k][l].setText(armyContainer.pop());
                            if (move) {
                                canArmyMove[k-2][l]=true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {}
        
        for(int i=0; i<6 ; i++)
        {
            try {
                if(((Text) oneBox[k-1][l-1].getChildren().get(1)).getText().equals(black[i]))
                {
                    boolean move=true;
                    if(saveKing)
                    {
                        armyContainer.push(texts[k][l].getText());
                        armyContainer.push(texts[k-1][l-1].getText());
                        texts[k-1][l-1].setText(texts[k][l].getText());
                        texts[k][l].setText("");

                        move=isThisRightMove(black);
                        
                        texts[k-1][l-1].setText(armyContainer.pop());
                        texts[k][l].setText(armyContainer.pop());
                    }
                    
                    if(move)
                    {
                        if(saveKing)
                        {
                            canArmyAttack[k-1][l-1]=true;
                            isContinueGame=true;
                        }
                        oneBox[k-1][l-1].setDisable(false);
                        if(i==3)
                        {
                            if (chackPaint) {
                                storeIndexOfChackedKing[0]=k-1;
                                storeIndexOfChackedKing[1]=l-1;
                            }
                            return true;
                        }
                    }
                }
                
            } catch (Exception e) {}
            try {
                if(((Text) oneBox[k-1][l+1].getChildren().get(1)).getText().equals(black[i]))
                {
                    boolean move=true;
                    if(saveKing)
                    {
                        armyContainer.push(texts[k][l].getText());
                        armyContainer.push(texts[k-1][l+1].getText());
                        texts[k-1][l+1].setText(texts[k][l].getText());
                        texts[k][l].setText("");

                        move=isThisRightMove(black);
                        
                        texts[k-1][l+1].setText(armyContainer.pop());
                        texts[k][l].setText(armyContainer.pop());
                    }
                    
                    if(move)
                    {
                        if(saveKing)
                        {
                            canArmyAttack[k-1][l+1]=true;
                            isContinueGame=true;
                        }
                        oneBox[k-1][l+1].setDisable(false);
                        if(i==3)
                        {
                            if (chackPaint) {
                                storeIndexOfChackedKing[0]=k-1;
                                storeIndexOfChackedKing[1]=l+1;
                            }
                            return true;
                        }
                    }
                }
                
            } catch (Exception e) {}
        } 


        if (saveKing) {
            circleClean(true);
            colorClean(true);
        }
        else
        {
            colorClean(false);
        }
    return false;                      
    }
    
    public boolean isChangePlace(int k, int l, int newk, int newl, String[] oppoArmy)
    {
        boolean move;
        armyContainer.push(texts[k][l].getText());
        armyContainer.push(texts[newk][newl].getText());
        texts[newk][newl].setText(texts[k][l].getText());
        texts[k][l].setText("");

        move=isThisRightMove(oppoArmy);
        
        texts[newk][newl].setText(armyContainer.pop());
        texts[k][l].setText(armyContainer.pop());

        if(move)
        {
            isContinueGame=true;
        }
        return move;
    }

    public void givePowerToPown(final int k ,final int l ,String[] myArmy)
    {
        rootPane.getChildren().get(2).resize(150, 500);
        Text queen = new Text(myArmy[4]);
        Text rook = new Text(myArmy[0]);
        Text horse = new Text(myArmy[1]);
        Text camel = new Text(myArmy[2]);

        hBox.getChildren().addAll(queen,rook,horse,camel);
        for(int i=0 ; i<4 ; i++)
        {
            ((Text)hBox.getChildren().get(i)).setFont(Font.font(100));
            ((Text)hBox.getChildren().get(i)).setCursor(Cursor.HAND);
            hBox.getChildren().get(i).setOnMouseClicked(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent arg0)
                {
                    texts[k][l].setText(((Text)arg0.getTarget()).getText());
                    oneBox[k][l].setDisable(true);
                    oneBox[k][l].getChildren().get(1).setOpacity(0.7);
                    ((HBox)rootPane.getChildren().get(2)).getChildren().clear();
                    rootPane.getChildren().get(2).resize(0, 0);
                    chessBord.setOpacity(1);
                    continueGame=false;
                }
            });
        }
        chessBord.setOpacity(0);
        continueGame=true;
        new Thread(()->{
            while(continueGame);
        }).start();

    }

    public void gameOver(String[] winArmy)
    {
        System.out.println("Hello");
        soundPlayer.pause();
        sound = new Media(new File(winnerSound).toURI().toString());
        soundPlayer = new MediaPlayer(sound);
        soundPlayer.play();
        System.out.println("ABC");
        Pane tempPane = new Pane();// for manage scene at end of game
        Button closeButton = new Button("X");
        closeButton.setFont(Font.font(30));
        closeButton.setBackground(Background.fill(Color.LIGHTYELLOW));
        closeButton.setBorder(Border.stroke(Color.RED));
        closeButton.setLayoutX(720);
        closeButton.setLayoutY(10);
        closeButton.setCursor(Cursor.HAND);

        if(winArmy !=null)
        {
            if(winArmy[3].equals(white[3]))
            {
                result.setText("White Win !");
            }
            else
            {
                result.setText("Black Win !");
            }
        }
        else
        {
            result.setText("Match Drow !");   
        }
        chessBord.setDisable(true);
        chessBord.setOpacity(0.5);
System.err.println("hi");
        tempPane.getChildren().addAll(rootPane,closeButton,soundContainerVeiw);
        scene = new Scene(tempPane,800,800);
        currentStage.setScene(scene);
        currentStage.show();

        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent arg0){

                sound = new Media(new File(spinnerSoundString).toURI().toString());
                soundPlayer = new MediaPlayer(sound);
                soundPlayer.play();
                new Thread(()->{
                    for(int i=0 ; i<3000 ; i+=100)
                    {
                        chessBord.setRotationAxis(Rotate.Z_AXIS);
                        chessBord.setRotate(i);
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    closeButton.setFont(Font.font(0));
                    soundPlayer.pause();
                }).start();

                closeButton.setOnMouseExited(new EventHandler<MouseEvent>(){
                    public void handle(MouseEvent arg0){
                        createStartButton();
                        System.out.println("Closed");
                    }
                });
            }
        });

        closeButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent arg0){
                closeButton.setBackground(Background.fill(Color.BROWN));
                closeButton.setTextFill(Color.WHITE);
                closeButton.setOpacity(0.9);
            }
        });

        closeButton.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent arg0){
                closeButton.setBackground(Background.fill(Color.WHITE));
                closeButton.setTextFill(Color.BLACK);
                closeButton.setOpacity(1);
            }
        });

        
    }
}
