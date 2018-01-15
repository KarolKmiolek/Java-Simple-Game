/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

/**
 *
 * @author Kai_Neisti
 */

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Image;
import java.util.*;
import javax.swing.ImageIcon;


class Surface extends JPanel implements ActionListener {

    
    //timer delay
    private int DELAY = 10;
    //just regular timer
    private Timer timer;
    //store obsticals positions
    private List<int[]> block = new ArrayList<int[]>();
    //store time passed since game started
    private int time = 0;
    //determine if is menu or game active 
    private boolean menu = true;
    //save the highest score during game instance
    private int highScore = 0;
    //determine position of player relative to ground
    private int position_y=0;
    //control jump height
    private int jump = 0;
    //variable used to check key pressed time
    private int key_time=0;
    //determine time for active special
    private int anim_time=0;
    //determine active special
    private int anim_type=0;
    //points aquired during game
    private int points=0;
    //reduce collision check ex. 1-always, 3-every third loop
    private int coll_check=1;
    //random number generator
    Random r = new Random();
    //bumping player
    int bump=0;

    public Surface() {

        initTimer();
        this.setFocusable(true);
        this.requestFocus();
        this.addKeyListener(new KeyListener(){
            @Override
            public void keyPressed(KeyEvent evt) {
                if(key_time==0)
                key_time=time;
            }
 
            @Override
            public void keyReleased(KeyEvent evt) {
                if (time-key_time<10)
                {
                    short_stroke();
                    key_time=0;
                }
                else if(time-key_time<25&&anim_type==0)
                {
                    if(points>0)
                    {
                        points-=1;
                        anim_type=2;
                        anim_time=0;
                    }
                }
                else if(time-key_time>=25&&anim_type==0)
                {
                    if(points>2)
                    {
                        points-=3;
                        anim_time=25;
                        anim_type=1;
                    }
                }
                key_time=0;
            }
 
            @Override
            public void keyTyped(KeyEvent evt) {
            }
        });   
    }
    private void initTimer() {

        timer = new Timer(DELAY, this);
        timer.start();
    }
    public Timer getTimer() {
        
        return timer;
    }
    //reset game params
    private void reset()
    {
        menu=false;
        jump=0;
        position_y=0;
        time=0;
        block=new ArrayList<int[]>();
        points=3;
        anim_type=0;
        anim_time=0;
    }
    //if menu is active starts game, else jump
    private void short_stroke()
    {
        if(menu==true)
        {
            //reset game parameters
            reset();
        }
        else if (position_y<1)
        {
            jump=100;
        }
    }
    //Calculation part
    private void doCalculation()
    {
        //computing part
            //passed time count
            time ++;
            //jumping mechanism
            if(jump>0)
            {
                position_y+=jump*jump/300;
                jump-=4;
            }
            else if(position_y>0)
            {
                jump-=4;
                position_y -=jump*jump/300;
            }
            else if(jump<0)
            {
                position_y=0;
            }
            //bumping
            if(time%10==0&&position_y==0)
            if(bump==2)
            {
                bump=0;
            }
            else
            {
                bump=2;
            }
            
            //creating obsticals
            if(time%50==0)
            {
                block.add(new int[]{800,(r.nextInt(150))+150});
            }
            //delete passed obsticals
            if(time%1000==0)
            {
                for(int i=0;i<block.size();i++)
                {
                    if(block.get(i)[0]<-50)
                    {
                        block.remove(i);
                        i=0;
                    }
                }
               
            }
            //move obsticals
            if(anim_type!=1)
            for(int[] obj: block)
                {
                obj[0]-=6;
                }
            //animation end check
            anim_time--;
            if(anim_time==0) 
            {
                anim_type=0;
                block=new ArrayList<int[]>();
            }
            //key press reset
            if(time-key_time>50)
                key_time=0;
            //points adding
            if(time%2000==1999)
                points++;
            //anim 2
            if(anim_type==2)
            {
                anim_type=0;
                if(position_y>0)
                {
                    position_y=0;
                    jump=0;
                }
            }
    }
    //Collision test, ends game if collision
    private void doCollCheck()
    {
        if(time%coll_check==0&&anim_type!=1)
            for(int[] obj: block)
            {
                //collision test
                if(anim_type!=0);
                if(obj[0]<100&&obj[0]>0&&obj[1]<350-position_y&&obj[1]+50>300-position_y)
                {
                    menu=true;
                    if(time>highScore)
                        highScore=time;
                }
            }
    }
    //Drawing game
    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        if(menu)
        {
            g2d.setPaint(Color.black);
            g2d.drawString("High Score : "+Integer.toString(highScore), 300, 500);
            g2d.drawString("Press button to start", 300, 550);
        }
        else
        {
            doCollCheck();
            doCalculation();
//drawing part
            //dwaw floor
            g2d.setPaint(Color.black);
            g2d.drawLine(0, 350, 800, 350);
            //draw player
            g2d.setPaint(Color.black);
            g2d.fillRoundRect(60, 300-position_y+bump, 25, 25,10,10);
            g2d.setPaint(Color.black);
            g2d.fillRoundRect(50, 315-position_y+bump, 50, 25,5,5);
            g2d.setPaint(Color.gray);
            g2d.fillOval(50, 325-position_y+bump, 25, 25);
            g2d.fillOval(80, 325-position_y+bump, 25, 25);
            g2d.setPaint(Color.yellow);
            g2d.fillArc(25, 295-position_y+bump, 150, 50,320,90);
            //draw obsticals
            if(anim_type==1)
            {
                //obsticals destrucrtion
                if(anim_time>20)
                {
                    g2d.setPaint(Color.white);
                    for(int[] obj: block)
                    {
                        g2d.fillOval(obj[0]+12, obj[1]+12, 25, 25);
                    }
                }
                else if(anim_time>15)
                {
                    for(int[] obj: block)
                    {
                        g2d.setPaint(Color.yellow);
                        g2d.fillOval(obj[0]+7, obj[1]+7, 35, 35);
                        g2d.setPaint(Color.white);
                        g2d.fillOval(obj[0]+12, obj[1]+12, 25, 25);
                    }
                }
                else if(anim_time>10)
                {
                    for(int[] obj: block)
                    {
                        g2d.setPaint(Color.orange);
                        g2d.fillOval(obj[0], obj[1], 50, 50);
                        g2d.setPaint(Color.yellow);
                        g2d.fillOval(obj[0]+7, obj[1]+7, 35, 35);
                        g2d.setPaint(Color.white);
                        g2d.fillOval(obj[0]+12, obj[1]+12, 25, 25);
                    }
                }
                else if(anim_time>5)
                {
                    for(int[] obj: block)
                    {
                        g2d.setPaint(Color.red);
                        g2d.fillOval(obj[0]-7, obj[1]-7, 65, 65);
                        g2d.setPaint(Color.orange);
                        g2d.fillOval(obj[0], obj[1], 50, 50);
                        g2d.setPaint(Color.yellow);
                        g2d.fillOval(obj[0]+7, obj[1]+7, 35, 35);
                        g2d.setPaint(Color.white);
                        g2d.fillOval(obj[0]+12, obj[1]+12, 25, 25);
                    }
                }
                else if(anim_time>1)
                {
                    g2d.setPaint(Color.gray);
                    for(int[] obj: block)
                    {
                        g2d.fillOval(obj[0]-7, obj[1]-7, 65, 65);
                    }
                }
            }
            else
            {
                g2d.setPaint(Color.blue);
                for(int[] obj: block)
                {
                g2d.fillRect(obj[0], obj[1], 50, 50);
                }
            }
            //draw current time passed ( score )
            g2d.setPaint(Color.red);
            g2d.drawString("Score : "+Integer.toString(time), 50, 500);
            //draw key press time
            if(key_time>0)
            g2d.fillRect(300, 500, (time-key_time)*4, 30);
            g2d.drawRect(300, 500, 200, 30);
            g2d.drawLine(400, 450, 400, 530);
            g2d.drawLine(340, 450, 340, 530);
            g2d.drawString("Explosion (3)", 401, 460);
            g2d.drawString("Ground (1)", 341, 470);
            g2d.drawString("Points : "+points, 400, 400);
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}


public class JavaApplication1 extends JFrame{

    Surface surface = new Surface();
    public JavaApplication1() {

        initUI();
    }
    

    private void initUI() {

        add(surface);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Timer timer = surface.getTimer();
                timer.stop();
            }
            
        });

        setTitle("Simple game");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    }
    
   
 

    public static void main(String[] args) {

        
        EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {

                JavaApplication1 ex = new JavaApplication1();
                ex.setVisible(true);
            }
        });
    }
    
}
