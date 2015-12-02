package snake;
import com.leapmotion.leap.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;
/**
 *
 * @author bensu
 */
class SampleListener extends Listener {
    public int appY_2, appY_1;
    public boolean replay=false;
    public boolean inGame=true;
    public int screenHeight=760;
    public float vectorX;
    public float vectorY;
    public int a=2;
    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }
    public void onConnect(Controller controller) {
        System.out.println("Connected"); //to indicate whether the leap motion connection is successful
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE); //to accept circle gesture
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
    }
    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
    }
    public void onExit(Controller controller) {
        System.out.println("Exited");
    }
    public void onFrame(Controller controller) { //to get the most recent frame and information
        Frame frame = controller.frame(); 
        GestureList gestures = frame.gestures(); 
        for(Gesture gesture : frame.gestures()){
            switch (gesture.type()) {
                case TYPE_CIRCLE:
                    replay=true; //drawing a circle enables replay
                case TYPE_SWIPE:
                    SwipeGesture swipe = new SwipeGesture(gesture);
                    Vector swipeDirection = swipe.direction();
                    float dirX = swipeDirection.get(0);
                    float dirY = swipeDirection.get(1);
                    //System.out.println(vectorX + " " + vectorY);
                    if(dirX<0 && Math.abs(dirX)>Math.abs(dirY)){
                        a=1; //left
                    }
                    else if(dirX>0 && Math.abs(dirX)>Math.abs(dirY)){
                        a=2; //right
                    }
                    else if(dirY>0 && Math.abs(dirY)>Math.abs(dirX)){
                        a=3; //up
                    }
                    else if(dirY<0 && Math.abs(dirY)>Math.abs(dirX)){
                        a=4; //down
                    }
                break;
            }
        } 
    }
    public int checkSwipeDir(){
        return a;
    }
    public boolean getReplay(){
        return replay;
    }
}
class Game extends JPanel implements ActionListener{
    public static int screenWidth=1366;
    public static int screenHeight=740;
    public boolean replay=false;
    public boolean inGame=true;
    public int snakeSize=3;
    public int sqSize=40;
    public int corner=sqSize;
    public int rows=16;
    public int cols=25;
    public int time=250;
    private final int x[] = new int[rows*cols];
    private final int y[] = new int[rows*cols];
    public int dots;
    public boolean rightDirection=true;
    public boolean upDirection=false;
    public boolean downDirection=false;
    public boolean leftDirection=false;
    public int food_x;
    public int food_y;
    public int score=0;
    public boolean eaten=false;
    Controller controller = new Controller(); //initializes leap
    SampleListener listener = new SampleListener();
    //check checker = new check(); //creates timer to refresh frames
    public Game(){
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(screenWidth,screenHeight));
        InitGame();
        controller.addListener(listener); //starts leap
    }
    private void InitGame(){
        replay=false;
        inGame=true;
        score=0;
        dots=3;
        Timer timer = new Timer(true);
        TimerTask taskToExecute = new check();
        timer.scheduleAtFixedRate(taskToExecute, 0, time); //refreshes the frame at every 20 miliseconds
        for (int z = 0; z < dots; z++) {
            x[z] = corner * 11 - z * sqSize;
            y[z] = corner * 7;
        }
        locateFood();
    }  
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        doDrawing(g);
    }
    private void move(){
        if(eaten){
            dots++;
            eaten=false;
        }
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }
        if(rightDirection){
            x[0]+=sqSize;
        }
        if(leftDirection){
            x[0]-=sqSize;
        }
        if(upDirection){
            y[0]-=sqSize;
        }
        if(downDirection){
            y[0]+=sqSize;
        }
    }
    private void checkGameOver(){
        if(x[0]<corner || x[0]>cols*sqSize || y[0]<corner || y[0]>rows*sqSize){
            inGame=false;
        }
        for(int i=1; i<=dots; i++){
            if(x[0]==x[i]&&y[0]==y[i]){
                inGame=false;
            }
        }
    }
    private void locateFood() {
        int r = (int)(Math.random()*cols+1)*(sqSize);
        food_x = r;
        r = (int)(Math.random()*rows+1)*(sqSize);
        food_y = r;
    }
    private void checkCollision(){
        if (x[0]==food_x && y[0]==food_y){
            locateFood();
            score+=10;
            eaten=true;
        }
    }
    private void doDrawing(Graphics g){
        Font big1 = new Font("Helvetica", Font.BOLD,400);
        Font small1 = new Font("Helvetica", Font.BOLD,25);
        Font smallest = new Font("Helvetica", Font.BOLD,20);
        Font big2 = new Font("Helvetica", Font.BOLD,100);
        Font small2 = new Font("Helvetica", Font.BOLD,50);
        if(inGame==true){ //paints game objects when game isn't over
            g.setColor(Color.white);
            //g.drawRect(corner,2*corner,((screenWidth-140)/sqSize)*20,((screenHeight-70)/sqSize)*10);
            int gridH=corner;
            g.drawRect(corner, corner, sqSize*cols, sqSize*rows);
            /*for(int a=1;a<=rows;a++){
                int gridCrn=corner;
                for(int b=1; b<=cols;b++){
                    g.drawRect(gridCrn, gridH, sqSize, sqSize);
                    gridCrn+=sqSize;
                }
                gridH+=sqSize;
            }*/
            //g.drawRect(1100,400,200,200);
            //g.fillOval(1190,490,20,20);
            g.setFont(small2);
            g.drawString("Score:" + score, 1100, 200);
            g.setColor(Color.gray);
            g.setFont(smallest);
            g.drawString("Bensu Sicim", screenWidth-150, screenHeight-50);
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.setColor(Color.gray);
                    g.fillRect(x[z], y[z], sqSize, sqSize);
                } else {
                    g.setColor(Color.white);
                    g.fillRect(x[z], y[z], sqSize, sqSize);
                }
            }
            g.setColor(Color.RED);
            g.fillRect(food_x, food_y, sqSize, sqSize);
            Toolkit.getDefaultToolkit().sync();
        }
        else if(inGame==false){ //game over page
            g.setColor(Color.white);
            g.setFont(big2);
            g.drawString("Game Over", 400, screenHeight/2-100);
            g.setFont(small2);
            g.drawString("Score:" + score, 550, screenHeight/2);
            g.setFont(small2);
            g.drawString("Draw a circle with your finger to replay", 225, screenHeight/2+200);
            g.setFont(smallest);
            g.setColor(Color.gray);
            g.drawString("Bensu Sicim", screenWidth-150, screenHeight-50);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint(); //To change body of generated methods, choose Tools | Templates.
    }
    public class check extends TimerTask{ //refresh class, when timer reaches the indicated time (every 20 miliseconds)
        public void run(){
            //System.out.println(replay);
            if(inGame){
                replay=false;
                checkSwipe();
                move();
                checkGameOver();
                checkCollision();
                //System.out.println(leftDirection + "" + rightDirection + "" + upDirection + "" + downDirection);
            }
            else if (inGame=false){
                replay=false;
                replay=listener.getReplay(); //checks if replay is true from the leap
                if(replay==true){
                    score=0;
                    dots=3;
                    for (int z = 0; z < dots; z++) {
                        x[z] = corner*11 - z * sqSize;
                        y[z] = corner*7;
                    }
                    inGame=true;
                    replay=false;
                }
            }
            repaint();
        }
    }
    public void checkSwipe() {
            int vector=listener.checkSwipeDir();
            //System.out.println(vector);
            if(vector!=0){
            if (vector==2 && !leftDirection) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if (vector==1 && !rightDirection) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if (vector==4 && !upDirection) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
            if (vector==3 && !downDirection) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
public class Snake extends JFrame{
    public Snake(){
        add(new Game());
        setResizable(false);
        pack();
        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                JFrame ex = new Snake();
                ex.setVisible(true);
            }
        });
    }
}
