package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel implements Observer
{
    private RobotModel m_robotModel;
    private final Timer m_timer = initTimer();
    
    private static Timer initTimer() 
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }
    
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100; 
    private volatile double m_robotDirection = 0; 

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;
    
    private static final double maxVelocity = 0.1; 
    private static final double maxAngularVelocity = 0.001; 
    
    public GameVisualizer() 
    {
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onModelUpdateEvent();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (m_robotModel != null) {
                    m_robotModel.setTargetPosition(e.getPoint().x, e.getPoint().y);
                }
                repaint();
            }
        });
        setDoubleBuffered(true);
    }

    public void setRobotModel(RobotModel model) {
        this.m_robotModel = model;
        model.addObserver(this);
        if (m_robotModel != null) {
            m_robotPositionX = m_robotModel.getRobotPositionX();
            m_robotPositionY = m_robotModel.getRobotPositionY();
            m_robotDirection = m_robotModel.getRobotDirection();
            m_targetPositionX = m_robotModel.getTargetPositionX();
            m_targetPositionY = m_robotModel.getTargetPositionY();
        }
    }

    protected void onModelUpdateEvent() {
        if (m_robotModel != null) {
            m_robotModel.onModelUpdateEvent();
        }
    }
    
    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }
    
    private static int round(double value)
    {
        return (int)(value + 0.5);
    }
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g; 
        drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), m_robotDirection);
        drawTarget(g2d, m_targetPositionX, m_targetPositionY);
    }
    
    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        int robotCenterX = round(m_robotPositionX); 
        int robotCenterY = round(m_robotPositionY);
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY); 
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX  + 10, robotCenterY, 5, 5);
    }
    
    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0); 
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof RobotModel) {
            if (arg instanceof RobotModel.PositionData) {
                RobotModel.PositionData data = (RobotModel.PositionData) arg;
                m_robotPositionX = data.x();
                m_robotPositionY = data.y();
                m_robotDirection = data.direction();
                m_targetPositionX = data.targetX();
                m_targetPositionY = data.targetY();
            } else if (arg instanceof RobotModel.TargetData) {
                RobotModel.TargetData data = (RobotModel.TargetData) arg;
                m_targetPositionX = data.x();
                m_targetPositionY = data.y();
            }
        }
    }
}
