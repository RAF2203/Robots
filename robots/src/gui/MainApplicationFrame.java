package gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.*;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final WindowSettings settings;
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        settings = WindowSettings.getInstance();
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);
        RobotModel model = new RobotModel();

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);
        restoreWindow(logWindow);

        GameWindow gameWindow = new GameWindow(model);
        gameWindow.setSize(800,  800);
        addWindow(gameWindow);
        restoreWindow(gameWindow);

        PositionWindow positionWindow = new PositionWindow();
        positionWindow.setSize(400, 200);
        positionWindow.setRobotModel(model);
        addWindow(positionWindow);
        restoreWindow(positionWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveAllWindowsState();
                System.exit(0);
            }
        });
    }

    private void restoreWindow(JInternalFrame frame){
        String windowID = null;
        if (frame instanceof LogWindow) { windowID = ((LogWindow) frame).getWindowID(); }
        else if (frame instanceof GameWindow) { windowID = ((GameWindow) frame).getWindowID(); }
        else if (frame instanceof PositionWindow) { windowID = ((PositionWindow) frame).getWindowID(); }

        if (windowID != null) {
            WindowSettings.WindowState state = settings.getStateByID(windowID);
            if (state != null) {
                Rectangle dimensions = state.getDimensions();
                try {
                    frame.setBounds(dimensions);
                    frame.setIconifiable(state.getFolded());
                    }
                catch (Exception _) {
                    //ignore?
                }
            }
        }
    }

    private void saveAllWindowsState() {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof LogWindow) {
                settings.SaveWindowState(
                        ((LogWindow) frame).getWindowID(),
                        frame.getBounds(),
                        frame.isIcon()
                );
            } else if (frame instanceof GameWindow) {
                settings.SaveWindowState(
                        ((GameWindow) frame).getWindowID(),
                        frame.getBounds(),
                        frame.isIcon()
                );
            } else if (frame instanceof PositionWindow) {
                settings.SaveWindowState(
                        ((PositionWindow) frame).getWindowID(),
                        frame.getBounds(),
                        frame.isIcon()
                );
            }
        }
        settings.saveToFile();
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
//
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
//
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
    ////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        return menuBar;
//    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu lookAndFeelMenu = createSystemLookAndFeelMenu();

        createCrossplatformLookAndFeelMenu(lookAndFeelMenu);

        JMenu testMenu = createTestMenu();

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(createExitMenu());
        return menuBar;
    }

    private JMenu createSystemLookAndFeelMenu(){
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        {
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }
        return lookAndFeelMenu;
    }

    private void createCrossplatformLookAndFeelMenu(JMenu lookAndFeelMenu) {
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);
    }

    private JMenu createTestMenu(){
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug("Новая строка");
            });
            testMenu.add(addLogMessageItem);
        }
        return testMenu;
    }

    private JMenuItem createExitMenu() {
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.setMnemonic(KeyEvent.VK_V);
        exitItem.getAccessibleContext().setAccessibleDescription(
                "Завершение программы");

        exitItem.addActionListener((event) -> {
            Object[] options = {"Да", "Нет"};
            int result = JOptionPane.showOptionDialog(
                    this,
                    "Вы действительно хотите выйти?",
                    "Подтверждение выхода",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (result == JOptionPane.YES_OPTION) {
                saveAllWindowsState();
                System.exit(0);
            }
        });

        return exitItem;
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}