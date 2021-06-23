import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class Application extends JFrame {
    //final private JPanel mainPanel;
    final private JPanel mainPanel;

    final private Habitat drawPanel;

    final private JPanel controlPanel;

    final private JCheckBox checkBoxAllowDialogWindow;
    final private JCheckBox checkBoxCatAI;
    final private JCheckBox checkBoxDogAI;

    private boolean allowDialogWindow;

    final private JButton animalListButton;

    final private JButton startButton;
    final private JButton stopButton;
    final private JButton consoleButton;

    final public JButton showUsersButton;

    final public JToggleButton showTimeButton;
    final public JToggleButton hideTimeButton;

    final public JButton loadCatsButton;
    final public JButton loadDogsButton;
    final public JButton saveCatsButton;
    final public JButton saveDogsButton;

    final private JPanel catEditor;
    final private JPanel dogEditor;

    final private JFormattedTextField catPeriodField;
    final private JFormattedTextField dogPeriodField;

    final private JFormattedTextField catSpeedField;
    final private JFormattedTextField dogSpeedField;

    final public JSlider catProbabilitySlider;
    final public JSlider dogProbabilitySlider;

    final private JFormattedTextField catDurationField;
    final private JFormattedTextField dogDurationField;

    final public long defaultPeriod = 1000;
    final public long defaultDuration = 2500;
    final public long defaultSpeed = 1;

    public JConsole console;

    public PackageHandler serverHandler;

    public userListWindow userlistwindow;

    String usersList = "";
    long ownId = -1;
    int myType = -1;

    public Application() {
        super("CatDog simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("ON WINDOW CLOSING");
                try {
                    saveConfig();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                super.windowClosing(e);
            }
        });
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        mainPanel.setBackground(Color.GRAY);

        drawPanel = new Habitat(this,defaultPeriod, 0.5, defaultDuration, defaultPeriod, 0.5, defaultDuration);
        controlPanel = new JPanel();

        mainPanel.add(drawPanel);
        mainPanel.add(controlPanel, BorderLayout.EAST);

        startButton = new JButton();
        startButton.setText("START");
        startButton.setFocusable(false);
        stopButton = new JButton();
        stopButton.setText("STOP");
        stopButton.setFocusable(false);
        animalListButton = new JButton();
        animalListButton.setText("Show Animal List");
        animalListButton.setFocusable(false);
        consoleButton = new JButton();
        consoleButton.setText("Console");
        consoleButton.setFocusable(false);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        showTimeButton = new JToggleButton();
        showTimeButton.setText("Show time");
        showTimeButton.setFocusable(false);
        hideTimeButton = new JToggleButton();
        hideTimeButton.setText("Hide time");
        hideTimeButton.setFocusable(false);
        showTimeButton.setSelected(true);
        hideTimeButton.setSelected(false);

        showUsersButton = new JButton();
        showUsersButton.setText("Show users");
        showUsersButton.setFocusable(false);
        showUsersButton.setEnabled(false);

        catEditor = new JPanel();
        catEditor.setLayout(new BoxLayout(catEditor, BoxLayout.PAGE_AXIS));
        dogEditor = new JPanel();
        dogEditor.setLayout(new BoxLayout(dogEditor, BoxLayout.PAGE_AXIS));

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(false);

        catPeriodField = new JFormattedTextField(formatter);
        catPeriodField.setToolTipText("Period of spawning cats, milliseconds");
        catPeriodField.setText("" + defaultPeriod);

        dogPeriodField = new JFormattedTextField(formatter);
        dogPeriodField.setToolTipText("Period of spawning dogs, milliseconds");
        dogPeriodField.setText("" + defaultPeriod);

        catSpeedField = new JFormattedTextField(formatter);
        catSpeedField.setToolTipText("Speed of cats");
        catSpeedField.setText("" + defaultSpeed);

        dogSpeedField = new JFormattedTextField(formatter);
        dogSpeedField.setToolTipText("Speed of dogs");
        dogSpeedField.setText("" + defaultSpeed);

        catDurationField = new JFormattedTextField(formatter);
        catDurationField.setToolTipText("Life time of cats, milliseconds");
        catDurationField.setText("" + defaultDuration);

        dogDurationField = new JFormattedTextField(formatter);
        dogDurationField.setToolTipText("Life time of dogs, milliseconds");
        dogDurationField.setText("" + defaultDuration);

        catProbabilitySlider = new JSlider(0, 100, 50);
        catProbabilitySlider.setMinorTickSpacing(10);
        catProbabilitySlider.setMajorTickSpacing(20);
        catProbabilitySlider.setPaintTicks(true);
        catProbabilitySlider.setPaintLabels(true);
        dogProbabilitySlider = new JSlider(0, 100, 50);
        dogProbabilitySlider.setMinorTickSpacing(10);
        dogProbabilitySlider.setMajorTickSpacing(20);
        dogProbabilitySlider.setPaintTicks(true);
        dogProbabilitySlider.setPaintLabels(true);

        catProbabilitySlider.setToolTipText("Probability of cat spawn");
        dogProbabilitySlider.setToolTipText("Probability of dog spawn");

        userlistwindow = new userListWindow();
        userlistwindow.window.setVisible(false);
        console = new JConsole();
        console.window.setVisible(false);


        catEditor.add(new JLabel("         Settings for CATS"));
        catEditor.add(catPeriodField);
        catEditor.add(catProbabilitySlider);
        catEditor.add(catDurationField);
        catEditor.add(catSpeedField);
        catEditor.validate();

        dogEditor.add(new JLabel("         Settings for DOGS"));
        dogEditor.add(dogPeriodField);
        dogEditor.add(dogProbabilitySlider);
        dogEditor.add(dogDurationField);
        dogEditor.add(dogSpeedField);
        dogEditor.validate();


        checkBoxAllowDialogWindow = new JCheckBox("Allow dialog window", false);
        checkBoxCatAI = new JCheckBox("CatAI", true);
        checkBoxDogAI = new JCheckBox("DogAI", true);
        checkBoxAllowDialogWindow.setFocusable(false);
        allowDialogWindow = false;

        JPanel dbButtonPanel = new JPanel();
        dbButtonPanel.setLayout(new GridLayout(1,4));
        loadCatsButton = new JButton("LC");
        loadDogsButton = new JButton("LD");
        saveCatsButton = new JButton("SC");
        saveDogsButton = new JButton("SD");
        dbButtonPanel.add(loadCatsButton);
        dbButtonPanel.add(loadDogsButton);
        dbButtonPanel.add(saveCatsButton);
        dbButtonPanel.add(saveDogsButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(11, 1));

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(consoleButton);
        buttonPanel.add(showTimeButton);
        buttonPanel.add(hideTimeButton);
        buttonPanel.add(showUsersButton);
        buttonPanel.add(animalListButton);
        buttonPanel.add(checkBoxAllowDialogWindow);
        buttonPanel.add(checkBoxCatAI);
        buttonPanel.add(checkBoxDogAI);
        buttonPanel.add(dbButtonPanel);

        JPanel editor = new JPanel();
        editor.setLayout(new BoxLayout(editor, BoxLayout.PAGE_AXIS));

        editor.add(catEditor);
        editor.add(new JSeparator());
        editor.add(dogEditor);

        controlPanel.add(buttonPanel);
        controlPanel.add(editor);


        controlPanel.setLayout(new GridLayout(2, 1));
        //controlPanel.getTopLevelAncestor().validate();
        //drawPanel.repaint();


        initListeners();
        loadConfig();

        final int xWindowSize = 1080, yWindowSize = 600;
        Dimension windowSize = new Dimension(xWindowSize, yWindowSize);
        setPreferredSize(windowSize);
        setVisible(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point middle = new Point(screenSize.width / 2, screenSize.height / 2);
        Point newWindowLocation = new Point(middle.x - getPreferredSize().width / 2, middle.y - getPreferredSize().height / 2);
        setLocation(newWindowLocation);
        pack();
    }

    private void initListeners() {
        Action bPressed = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                startAction();
            }
        };
        Action ePressed = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                stopAction();
            }
        };
        Action tPressed = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                showTimeButton.setSelected(!showTimeButton.isSelected());
                hideTimeButton.setSelected(!showTimeButton.isSelected());
                drawPanel.timeAllowed = showTimeButton.isSelected();
                drawPanel.timeSwitch();
            }
        };
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("B"), "keyB");
        mainPanel.getActionMap().put("keyB", bPressed);
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("E"), "keyE");
        mainPanel.getActionMap().put("keyE", ePressed);
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("T"), "keyT");
        mainPanel.getActionMap().put("keyT", tPressed);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startAction();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopAction();
            }
        });

        showTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (drawPanel.timeAllowed) {
                    showTimeButton.setSelected(true);
                    hideTimeButton.setSelected(false);
                    //drawPanel.timeAllowed=showTimeButton.isVisible();
                    drawPanel.timeSwitch();
                } else {
                    showTimeButton.setSelected(true);
                    hideTimeButton.setSelected(false);
                    drawPanel.timeAllowed = showTimeButton.isSelected();
                    drawPanel.timeSwitch();
                }
            }
        });
        hideTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (drawPanel.timeAllowed) {
                    showTimeButton.setSelected(false);
                    hideTimeButton.setSelected(true);
                    drawPanel.timeAllowed = showTimeButton.isSelected();
                    drawPanel.timeSwitch();
                } else {
                    showTimeButton.setSelected(false);
                    hideTimeButton.setSelected(true);
                    drawPanel.timeAllowed = showTimeButton.isSelected();
                    drawPanel.timeSwitch();
                }
            }
        });
        checkBoxAllowDialogWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allowDialogWindow = checkBoxAllowDialogWindow.isSelected(); // not really?
            }
        });
        checkBoxCatAI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBoxCatAI.isSelected()) {
                    drawPanel.allowCatAI();
                } else {
                    drawPanel.restrictCatAI();
                }
            }
        });
        checkBoxDogAI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBoxDogAI.isSelected()) {
                    drawPanel.allowDogAI();
                } else {
                    drawPanel.restrictDogAI();
                }
            }
        });
        catProbabilitySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                double value = (double) catProbabilitySlider.getValue() / 100;
                drawPanel.changePCat(value);
            }
        });
        dogProbabilitySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                double value = (double) dogProbabilitySlider.getValue() / 100;
                drawPanel.changePDog(value);
            }
        });
        catPeriodField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String periodString = catPeriodField.getText();
                if (periodString == "") return;
                long periodNumber = defaultPeriod;
                try {
                    long l = DecimalFormat.getNumberInstance().parse(periodString).longValue();
                    if (l < 10) {
                        return;
                    }
                    //System.out.println("I parsed this for catPeriod:"+l); //111111.23
                    periodNumber = l;

                } catch (ParseException e) {
                    System.out.println("Invalid input, setting default settings");
                }
                drawPanel.changeTCat(periodNumber);
            }
        });
        dogPeriodField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String periodString = dogPeriodField.getText();
                if (periodString == "") return;
                long periodNumber = defaultPeriod;
                try {
                    long l = DecimalFormat.getNumberInstance().parse(periodString).longValue();
                    if (l < 10) {
                        return;
                    }
                    //System.out.println("I parsed this for dogPeriod:"+l); //111111.23
                    periodNumber = l;

                } catch (ParseException e) {
                    System.out.println("Invalid input, setting default settings");
                }
                drawPanel.changeTDog(periodNumber);
            }
        });
        catDurationField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String duration = catDurationField.getText();
                if (duration == "") return;
                long durationNumber = defaultPeriod;
                try {
                    long l = DecimalFormat.getNumberInstance().parse(duration).longValue();
                    if (l < 10) {
                        return;
                    }
                    //System.out.println("I parsed this for catDuration:"+l); //111111.23
                    durationNumber = l;

                } catch (ParseException e) {
                    System.out.println("Invalid input, setting default settings");
                }
                drawPanel.changeDCat(durationNumber);
            }
        });
        dogDurationField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String duration = dogDurationField.getText();
                if (duration == "") return;
                long durationNumber = defaultPeriod;
                try {
                    long l = DecimalFormat.getNumberInstance().parse(duration).longValue();
                    if (l < 10) {
                        return;
                    }
                    //System.out.println("I parsed this for dogDuration:"+l); //111111.23
                    durationNumber = l;

                } catch (ParseException e) {
                    System.out.println("Invalid input, setting default settings");
                }
                drawPanel.changeDDog(durationNumber);
            }
        });
        catSpeedField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String speedString = catSpeedField.getText();
                if (speedString == "") return;
                long speedNumber = defaultSpeed;
                try {
                    long l = DecimalFormat.getNumberInstance().parse(speedString).longValue();
                    if (l < 1) {
                        return;
                    }
                    //System.out.println("I parsed this for catSpeed:"+l); //111111.23
                    speedNumber = l;

                } catch (ParseException e) {
                    System.out.println("Invalid input, setting default settings");
                }
                drawPanel.myCatAI.setS(speedNumber);
            }
        });
        dogSpeedField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String speedString = dogSpeedField.getText();
                if (speedString == "") return;
                long speedNumber = defaultSpeed;
                try {
                    long l = DecimalFormat.getNumberInstance().parse(speedString).longValue();
                    if (l < 1) {
                        return;
                    }
                    //System.out.println("I parsed this for dogSpeed:"+l); //111111.23
                    speedNumber = l;

                } catch (ParseException e) {
                    System.out.println("Invalid input, setting default settings");
                }
                drawPanel.myDogAI.setS(speedNumber);
            }
        });
        animalListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animalListWindow();
            }
        });
        consoleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                console.window.setVisible(true);
                console.window.setPreferredSize(new Dimension(250, 250));
                Point middle = new Point(Application.super.getX(), Application.super.getY());
                Point newWindowLocation = new Point(middle.x + getWidth() / 2 - console.window.getWidth() / 2, middle.y + getHeight() / 2 - console.window.getHeight() / 2);
                console.window.setLocation(newWindowLocation);
            }
        });
        showUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userlistwindow.window.setVisible(true);
                userlistwindow.relocate();
            }
        });
        loadCatsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCatsDB();
            }
        });
        loadDogsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDogsDB();
            }
        });
        saveCatsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCatsDB();
            }
        });
        saveDogsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDogsDB();
            }
        });
    }

    private void startAction() {
        drawPanel.startSimulation();
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void stopAction() {
        if (allowDialogWindow) {
            dialogWindow();
        } else {
            drawPanel.stopSimulationAndShowResults();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

    public void dialogWindow() {
        drawPanel.pauseSimulation();
        JFrame dialog = new JFrame("Results");
        JPanel insideDialog = new JPanel();
        insideDialog.setLayout(new BoxLayout(insideDialog, BoxLayout.PAGE_AXIS));
        JPanel buttonsInsideDialog = new JPanel();
        buttonsInsideDialog.setLayout(new GridLayout(1, 2));
        JButton buttonEndInsideDialog = new JButton("End");
        JButton buttonContinueInsideDialog = new JButton("Continue");
        buttonsInsideDialog.add(buttonContinueInsideDialog);
        buttonsInsideDialog.add(buttonEndInsideDialog);
        JTextArea textInsideDialog = new JTextArea(drawPanel.createResultTextNoHTML(), 5, 10);
        textInsideDialog.setEditable(false);
        insideDialog.add(textInsideDialog);
        insideDialog.add(buttonsInsideDialog);
        dialog.add(insideDialog);

        Point middle = new Point(Application.super.getX(), Application.super.getY());
        dialog.setPreferredSize(new Dimension(250, 250));
        dialog.pack();
        dialog.setVisible(true);
        Point newWindowLocation = new Point(middle.x + getWidth() / 2 - dialog.getWidth() / 2, middle.y + getHeight() / 2 - dialog.getHeight() / 2);
        dialog.setLocation(newWindowLocation);

        buttonEndInsideDialog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("RESET BUTTON");
                buttonEndInsideDialog.removeActionListener(this::actionPerformed);
                buttonContinueInsideDialog.removeActionListener(this::actionPerformed);
                drawPanel.stopSimulationAndShowResults();
                dialog.dispose();
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });
        buttonContinueInsideDialog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("CANCEL BUTTON");
                buttonEndInsideDialog.removeActionListener(this::actionPerformed);
                buttonContinueInsideDialog.removeActionListener(this::actionPerformed);
                drawPanel.resumeSimulation();
                dialog.dispose();
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
            }
        });
    }

    public void animalListWindow() {
        JFrame dialog = new JFrame("Animal List");
        JPanel insideDialog = new JPanel();
        insideDialog.setLayout(new BoxLayout(insideDialog, BoxLayout.PAGE_AXIS));

        JTextArea textInsideDialog = new JTextArea(drawPanel.existingAnimals());
        textInsideDialog.setEditable(false);
        insideDialog.add(textInsideDialog);
        dialog.add(insideDialog);

        Point middle = new Point(Application.super.getX(), Application.super.getY());
        dialog.setPreferredSize(new Dimension(350, 250));
        dialog.pack();
        dialog.setVisible(true);
        Point newWindowLocation = new Point(middle.x + getWidth() / 2 - dialog.getWidth() / 2, middle.y + getHeight() / 2 - dialog.getHeight() / 2);
        dialog.setLocation(newWindowLocation);
    }

    class userListWindow {
        JFrame window;
        JPanel spaceForUsers;

        class JUserButton extends JPanel {
            JButton buttonC;
            JButton buttonD;
            JLabel name;
            String userid;

            JUserButton(String _userid) {
                super();
                userid = _userid;
                this.setLayout(new GridLayout(1, 3));
                name = new JLabel("User#" + userid);
                buttonC = new JButton("Connect");
                buttonD = new JButton("Disconnect");
                this.add(name);
                this.add(buttonC);
                this.add(buttonD);
                JUserButtonListenersInit();
            }

            JUserButton(String _userid, boolean myself) {
                super();
                userid = _userid;
                this.setLayout(new GridLayout(1, 3));
                name = new JLabel("User#" + userid);
                buttonC = new JButton("Connect");
                buttonC.setEnabled(false);
                buttonD = new JButton("Disconnect");
                buttonD.setEnabled(false);
                this.add(name);
                this.add(buttonC);
                this.add(buttonD);
            }

            private void JUserButtonListenersInit() {
                buttonC.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        serverHandler.askToConnectTo(userid);
                    }
                });
                buttonD.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        serverHandler.askToUnpair();
                    }
                });
            }
        }

        userListWindow() {
            window = new JFrame("User List");
            ;
            window.setVisible(false);
            spaceForUsers = new JPanel();
            spaceForUsers.setLayout(new BoxLayout(spaceForUsers, BoxLayout.PAGE_AXIS));
            window.add(spaceForUsers);
            window.setPreferredSize(new Dimension(350, 250));
            window.pack();
        }

        public void updateList(String _userList) {
            if (_userList.equals("")) return;
            String u = "User#";
            String result = "";
            String[] ids = _userList.split("#");
            spaceForUsers.removeAll();
            for (String i : ids) {
                if (i.equals("" + ownId)) {
                    spaceForUsers.add(new JUserButton(i, true));
                } else {
                    spaceForUsers.add(new JUserButton(i));
                }
            }
            this.window.invalidate();
            this.window.validate();
            this.window.repaint();
        }

        public void relocate() {
            Point middle = new Point(Application.super.getX(), Application.super.getY());
            Point newWindowLocation = new Point(middle.x + getWidth() / 2 - window.getWidth() / 2, middle.y + getHeight() / 2 - window.getHeight() / 2);
            window.setLocation(newWindowLocation);
        }
    }

    class JConsole {
        private JFrame window;
        private JTextArea oldCommandHistory;
        private JFormattedTextField newCommandLine;

        JConsole() {
            window = new JFrame("Console");
            window.setVisible(false);
            window.setResizable(false);
            JPanel insideWindow = new JPanel();
            insideWindow.setLayout(new BorderLayout());
            newCommandLine = new JFormattedTextField();
            newCommandLine.setColumns(20);
            //newCommandLine.setPreferredSize(new Dimension(10,10));
            oldCommandHistory = new JTextArea();
            oldCommandHistory.setLineWrap(true);
            oldCommandHistory.setBackground(new Color(190, 190, 190));
            oldCommandHistory.setEditable(false);
            insideWindow.add(oldCommandHistory, BorderLayout.CENTER);
            insideWindow.add(newCommandLine, BorderLayout.SOUTH);

            newCommandLine.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    printInConsole(">" + newCommandLine.getText());
                    String[] args = newCommandLine.getText().split(" ");
                    switch (args[0]) {
                        case "stop":
                            if (args.length == 2) {
                                if (args[1] == "catspawn") {
                                    drawPanel.setCatSpawn(false);
                                    printInConsole("Stopped spawning cats");
                                }
                                if (args[1] == "dogspawn") {
                                    drawPanel.setDogSpawn(false);
                                    printInConsole("Stopped spawning dogs");
                                }
                            } else {
                                printInConsole("Unknown args");
                            }
                            break;
                        case "continue":
                            if (args.length == 2) {
                                if (args[1] == "catspawn") {
                                    drawPanel.setCatSpawn(true);
                                    printInConsole("Continued spawning cats");
                                }
                                if (args[1] == "dogspawn") {
                                    drawPanel.setDogSpawn(true);
                                    printInConsole("Continued spawning dogs");
                                }
                            } else {
                                printInConsole("Unknown args");
                            }
                            break;
                        case "save":
                            if (startButton.isEnabled()) {
                                printInConsole("Start simulation first!");
                            } else {
                                try {
                                    drawPanel.saveAnimalState();
                                    printInConsole("Saved state of all animals");
                                } catch (IOException ioException) {
                                    printInConsole("Error, couldn't save animals");
                                } catch (ClassNotFoundException classNotFoundException) {
                                    printInConsole("Error, couldn't save animals");
                                }
                            }
                            break;
                        case "load":
                            if (startButton.isEnabled()) {
                                printInConsole("Start simulation first!");
                            } else {
                                try {
                                    FileDialog fd = new FileDialog(window, "Choose a file", FileDialog.LOAD);
                                    fd.setFile("*.dat");
                                    fd.setVisible(true);
                                    String path = fd.getFile();
                                    if (path == null) {
                                        printInConsole("Empty path!");
                                    } else {
                                        String ext = getFileExtension(path);
                                        if ((ext.compareTo("dat") != 0)) {
                                            printInConsole("Wrong format!");
                                        } else {
                                            drawPanel.loadAnimalState(path);
                                            catPeriodField.setText(Long.toString(drawPanel.tCat));
                                            dogPeriodField.setText(Long.toString(drawPanel.tDog));
                                            catDurationField.setText(Long.toString(drawPanel.dCat));
                                            dogDurationField.setText(Long.toString(drawPanel.dDog));
                                            catProbabilitySlider.setValue((int) (drawPanel.pCat * 100));
                                            dogProbabilitySlider.setValue((int) (drawPanel.pDog * 100));
                                            catSpeedField.setText(Integer.toString((int) drawPanel.myCatAI.S));
                                            dogSpeedField.setText(Integer.toString((int) drawPanel.myDogAI.S));
                                            printInConsole("Loaded previous state of all animals");
                                        }
                                    }

                                } catch (IOException ioException) {
                                    printInConsole("Error, couldn't load animals");
                                } catch (ClassNotFoundException classNotFoundException) {
                                    printInConsole("Error, couldn't load animals");
                                }
                            }
                            break;
                        case "connect":
                            if (args.length == 2) {
                                String ip_address;
                                int port;
                                try {
                                    String[] ip_parts = args[1].split(":");
                                    if (ip_parts.length == 2) {
                                        ip_address = ip_parts[0];
                                        port = Integer.parseInt(ip_parts[1]);
                                        if (isIPv4(ip_address)) {
                                            printInConsole("Connecting...");
                                            createConnectionToServer(ip_address, port);
                                        } else {
                                            printInConsole("Bad address");
                                        }
                                    } else {
                                        printInConsole("Bad address");
                                    }
                                } catch (Exception ex) {
                                    System.out.println(ex);
                                }
                            } else {
                                printInConsole("Unknown args");
                            }
                            break;
                        case "disconnect":
                            if (serverHandler.onConnection == false) {
                                printInConsole("Already disconnected");
                            } else {
                                printInConsole("Disconnected");
                                serverHandler.onConnection = false;
                            }
                            break;
                        default:
                            printInConsole("Command not found");
                            break;

                    }
                    newCommandLine.setText("");
                }
            });

            window.add(insideWindow);
            Point middle = new Point(Application.super.getX(), Application.super.getY());
            window.setPreferredSize(new Dimension(250, 250));
            window.pack();
            Point newWindowLocation = new Point(middle.x + getWidth() / 2 - window.getWidth() / 2, middle.y + getHeight() / 2 - window.getHeight() / 2);
            window.setLocation(newWindowLocation);
        }

        public boolean isIPv4(String ipAddress) {
            boolean isIPv4 = false;

            if (ipAddress != null) {
                try {
                    InetAddress inetAddress = InetAddress.getByName(ipAddress);
                    isIPv4 = (inetAddress instanceof Inet4Address) && inetAddress.getHostAddress().equals(ipAddress);
                } catch (Exception ex) {
                }
            }

            return isIPv4;
        }

        public int countChar(String str, char c) {
            int count = 0;

            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == c)
                    count++;
            }

            return count;
        }

        public void printInConsole(String str) {
            oldCommandHistory.setText(oldCommandHistory.getText() + str + "\n");
            while (countChar(oldCommandHistory.getText(), 'n') > 19) {
                oldCommandHistory.setText(oldCommandHistory.getText().substring(oldCommandHistory.getText().indexOf('\n') + 1));
            }
        }
    }

    public void printInConsole(String input) {
        if (console == null) {
            System.out.println("No console to print in: " + input);
        } else {
            if (console.window.isDisplayable()) {
                console.printInConsole("@ " + input);
            } else {
                System.out.println("No console to print in: " + input);
            }
        }
    }

    public static String getFileExtension(String fullName) {
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    public void saveConfig() {
        FileOutputStream configFile = null;
        ObjectOutputStream configOutputStream = null;
        try {
            configFile = new FileOutputStream("config.conf", false);
            configOutputStream = new ObjectOutputStream(configFile);
            //App config
            configOutputStream.writeObject(showTimeButton.isSelected());
            configOutputStream.writeObject(allowDialogWindow);
            //Hab config
            drawPanel.saveConfig(configOutputStream);
            configOutputStream.close();
            configFile.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Couldn't save configurations to file");
            try {
                configOutputStream.close();
                configFile.close();
            } catch (Exception exception) {
                return;
            }

        }
        System.out.println("Config successfully saved");
        return;
    }

    public void loadConfig() {
        FileInputStream configFile = null;
        ObjectInputStream configInputStream = null;
        try {
            configFile = new FileInputStream("config.conf");
            configInputStream = new ObjectInputStream(configFile);
            //App config
            showTimeButton.setSelected((boolean) configInputStream.readObject());
            {
                hideTimeButton.setSelected(!(showTimeButton.isSelected()));
                drawPanel.timeAllowed = showTimeButton.isSelected();
                drawPanel.timeSwitch();
            }
            allowDialogWindow = (boolean) configInputStream.readObject();
            checkBoxAllowDialogWindow.setSelected(allowDialogWindow);
            //Hab config
            drawPanel.loadConfig(configInputStream);
            //Closing file
            configInputStream.close();
            configFile.close();
            //Fix app things to hab params
            checkBoxCatAI.setSelected(drawPanel.myCatAIallowed);
            checkBoxDogAI.setSelected(drawPanel.myDogAIallowed);
            catPeriodField.setText(Long.toString(drawPanel.tCat));
            dogPeriodField.setText(Long.toString(drawPanel.tDog));
            catDurationField.setText(Long.toString(drawPanel.dCat));
            dogDurationField.setText(Long.toString(drawPanel.dDog));
            catProbabilitySlider.setValue((int) (drawPanel.pCat * 100));
            dogProbabilitySlider.setValue((int) (drawPanel.pDog * 100));
            catSpeedField.setText(Integer.toString((int) drawPanel.myCatAI.S));
            dogSpeedField.setText(Integer.toString((int) drawPanel.myDogAI.S));
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            try {
                configInputStream.close();
                configFile.close();
            } catch (Exception exception) {
                return;
            }
            System.out.println("Couldn't load configurations from file");
            return;
        }
        System.out.println("Config successfully loaded");
        // update interface to config
    }

    void createConnectionToServer(String _ip_address, int _port) {
        System.out.println("Starting connection");
        Socket sock = null;
        DataOutputStream outStream = null;
        DataInputStream inputStream = null;
        try {
            sock = new Socket(_ip_address, _port);
            sock.setSoTimeout(PackageHandler.TIMEOUT);
            outStream = new DataOutputStream(sock.getOutputStream());
            inputStream = new DataInputStream(sock.getInputStream());

            outStream.writeInt(RequestType.PING_TYPE);
            outStream.flush();

            // successful connection
            serverHandler = new PackageHandler(sock, inputStream, outStream);
            serverHandler.start();


        } catch (Exception e) {
            System.out.println("Connection error");
            System.out.println(e);
            try {
                outStream.close();
                inputStream.close();
                sock.close();
            } catch (Exception ex) {
            }
        }
    }

    class PackageHandler extends Thread {
        Socket serverSocket;
        DataInputStream inputStream;
        DataOutputStream outputStream;
        boolean onConnection;
        public static final int TIMEOUT = 15000;

        PackageHandler(Socket _s, DataInputStream _i, DataOutputStream _o) {
            serverSocket = _s;
            inputStream = _i;
            outputStream = _o;
            onConnection = true;
            onConnect();
        }

        @Override
        public void run() {
            int type;
            String message;
            while (onConnection) {
                try {
                    type = inputStream.readInt();
                    switch (type) {
                        case RequestType.PING_TYPE:
                            synchronized (outputStream) {
                                outputStream.writeInt(RequestType.PING_TYPE);
                                outputStream.flush();
                            }
                            break;
                        case RequestType.LIST_TYPE:
                            ownId = inputStream.readLong();
                            usersList = inputStream.readUTF();
                            onListUpdate();
                            break;
                        case RequestType.GAME_TYPE:
                            int animalType = inputStream.readInt();
                            int x = inputStream.readInt();
                            int y = inputStream.readInt();
                            drawPanel.spawnAnimal(animalType, x, y);
                            break;
                        case RequestType.PAIR_TYPE:
                            myType=inputStream.readInt();
                            printInConsole("I've paired, now i'm: "+myType);
                            break;
                        case RequestType.UNPAIR_TYPE:
                            myType=-1;
                            printInConsole("I unpaired");
                            break;
                        default:
                            System.out.println("Type is " + type + ". Bad request type.");
                            onConnection=false;
                    }
                } catch (java.net.SocketTimeoutException e) {
                    System.out.println("No response. Disconnected.");
                    onConnection = false;
                } catch (java.io.EOFException e) {
                    System.out.println("Server closed stream. Disconnected.");
                    onConnection = false;
                } catch (Exception e) {
                    System.out.println("Unknown error occurred");
                    System.out.println(e);
                    onConnection = false;
                }
            }
            onDisconnect();
        }

        public void askToConnectTo(String userID) {
            if (onConnection) {
                synchronized (outputStream) {
                    try {
                        outputStream.writeInt(RequestType.PAIR_TYPE);
                        outputStream.writeUTF(userID);
                        outputStream.flush();
                    } catch (Exception e) {
                        System.out.println("Error occurred while pairing");
                        System.out.println(e);
                        onConnection = false;
                    }
                }
            }
        }

        public void askToUnpair() {
            if (onConnection) {
                synchronized (outputStream) {
                    try {
                        outputStream.writeInt(RequestType.UNPAIR_TYPE);
                        outputStream.flush();
                        myType=-1;
                        printInConsole("I unpaired");
                    } catch (Exception e) {
                        System.out.println("Error occurred while pairing");
                        System.out.println(e);
                        onConnection = false;
                    }
                }
            }
        }

        public void sendGameRequest(int anitype,int x,int y){
            synchronized (outputStream){
                try {
                    outputStream.writeInt(RequestType.GAME_TYPE);
                    outputStream.writeInt(anitype);
                    outputStream.writeInt(x);
                    outputStream.writeInt(y);
                    outputStream.flush();
                    System.out.println("Sent my friend game request: "+anitype+" "+x+" "+y);
                } catch (Exception e) {
                    System.out.println("Error occurred while game request");
                    System.out.println(e);
                    onConnection = false;
                }
            }
        }
    }

    public void onConnect() {
        printInConsole("Connected");
        showUsersButton.setEnabled(true);
    }

    public void onListUpdate() {
        userlistwindow.updateList(usersList);
    }

    public void onDisconnect() {
        printInConsole("Disconnected");
        showUsersButton.setEnabled(false);
        usersList = "";
        serverHandler = null;
        myType=-1;
    }

    public void saveCatsDB(){
        drawPanel.pauseSimulation();
        TestDB localdb = new TestDB();
        localdb.addCats(drawPanel.animals);
        localdb.close();
        drawPanel.resumeSimulation();
    }
    public void saveDogsDB(){
        drawPanel.pauseSimulation();
        TestDB localdb = new TestDB();
        localdb.addDogs(drawPanel.animals);
        localdb.close();
        drawPanel.resumeSimulation();
    }

    public void loadCatsDB(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                TestDB localdb = new TestDB();
                drawPanel.createDBCats(localdb.getCats(drawPanel.previousTimePassed));
                localdb.close();
            }
        });
        t.start();
    }
    public void loadDogsDB(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                TestDB localdb = new TestDB();
                drawPanel.createDBDogs(localdb.getDogs(drawPanel.previousTimePassed));
                localdb.close();
            }
        });
        t.start();
    }

    public static void main(String[] args) {
        new Application();
    }
}
