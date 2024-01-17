import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Main extends JFrame {
    public Main() {
        this.setTitle("Animation");
        this.setBounds(250,300,300,300);
        this.setResizable(false);
        panelAnimation.setBackground(Color.WHITE);

        JButton buttonStart = (JButton)panelButton.add(new JButton("Start"));
        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                startAnimation();
            }
        });



        JButton buttonStop = (JButton)panelButton.add(new JButton("Stop"));

        buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                stopAnimation();
            }
        });

        JButton buttonAdd = (JButton)panelButton.add(new JButton("Add"));

        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                addAnimation();
            }
        });


        this.getContentPane().add(panelAnimation);
        this.getContentPane().add(panelButton, BorderLayout.SOUTH);
        this.setDefaultCloseOperation(3);
    }

    public void startAnimation()
    {
        panelAnimation.start();
    }

    public void stopAnimation()
    {
        panelAnimation.stop();
    }

    public void addAnimation()
    {
        panelAnimation.addBall();
    }

    private JPanel panelButton = new JPanel();
    private PanelAnimation panelAnimation = new PanelAnimation();

    public static void main(String[] args) {
        new Main().setVisible(true);
    }

    class PanelAnimation extends JPanel
    {
        private volatile boolean stopped = false;
        private Object lock = new Object();

        public void addBall()
        {
            listBall.add(new Ball());
            thread = new Thread(threadGroup, new BallRunnable((Ball) listBall.get(listBall.size()-1)));
            thread.start();

            threadGroup.list();
        }

        public void stop()
        {
            stopped = true;
        }


        public void start()
        {
            if(stopped)
            {
                stopped = false;
                synchronized (lock)
                {
                    lock.notifyAll();
                }
            }
        }


        public void paintComponent (Graphics g)
        {
            super.paintComponent(g);

            for (int i =0; i < listBall.size(); i++)
            {
                g.drawImage(Ball.getImg(), ((Ball)listBall.get(i)).x, ((Ball)listBall.get(i)).y, null);
            }
        }

        ArrayList listBall = new ArrayList();
        JPanel panel = this;
        Thread thread;
        ThreadGroup threadGroup = new ThreadGroup("Ball Group");


        public class BallRunnable implements Runnable
        {
            public BallRunnable(Ball ball)
            {
                this.ball = ball;
            }


            public void run()
            {

                while (true)
                {
                    synchronized (lock)
                    {
                        while (stopped)
                        {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                        this.ball.moveBall(panel);
                        repaint();
                    try
                    {
                        Thread.sleep(6);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

            }
            Ball ball;
        }
    }

}

class Ball
{
    public static Image getImg()
    {
        return Ball.ball;
    }
    public void moveBall(JPanel panelBox)
    {
        Rectangle borderPanel = panelBox.getBounds();
        x += dx;
        y += dy;

        if(y + yBall >= borderPanel.getMaxY())
        {
            y = (int)(borderPanel.getMaxY()- yBall);
            dy = -dy;
        }
        if(x + xBall >= borderPanel.getMaxX())
        {
            x = (int)(borderPanel.getMaxX()- xBall);
            dx = -dx;
        }
        if(y < borderPanel.getMinY())
        {
            y = (int)borderPanel.getMinY();
            dy = -dy;
        }
        if(x < borderPanel.getMinX())
        {
            x = (int)borderPanel.getMinX();
            dx = -dx;
        }

    }

    public static Image ball = new ImageIcon("ball1.png").getImage();

    int x = 138;
    int y = 108;

    int dx = 1;
    int dy = 1;

    int xBall = ball.getWidth(null);
    int yBall = ball.getHeight(null);

}