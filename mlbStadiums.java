import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class mlbStadiums extends JFrame {
    private JLabel stadiumImageLabel;
    private JTextField userGuessField;
    private JLabel feedbackLabel;
    private JLabel timerLabel;
    private JLabel progressLabel;
    private Map<String, String> stadiumMap;  // Store stadium images and team names
    private String currentStadium;
    private int stadiumCount = 0;
    private long startTime;

    public mlbStadiums() {
        // Create the homepage
        createHomePage();
    }

    
    private void createHomePage() {
        JFrame homeFrame = new JFrame("MLB Stadium Guessing Game");
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setLayout(new BorderLayout());
        
        // Create a JPanel to hold the image and directions
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS)); // Vertical layout
        centerPanel.setBackground(Color.CYAN);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Add a picture to the homepage
        JLabel imageLabel = new JLabel();
        ImageIcon homeImage = new ImageIcon(getClass().getResource("/actually.png")); // Replace with your image file
        imageLabel.setIcon(homeImage);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the image
    
        // Add directions for the game
        JTextArea directionsArea = new JTextArea("Welcome to the MLB Stadium Guessing Game!\n\n"
            + "Instructions:\n"
            + "1. You will see a stadium image.\n"
            + "2. Guess the team that plays in that stadium by typing in the name.\n"
            + "3. Press 'Submit' or hit 'Enter' to check your answer.\n"
            + "4. Try to guess as many as you can in 30 rounds!\n");
        directionsArea.setEditable(false);
        directionsArea.setLineWrap(true);
        directionsArea.setWrapStyleWord(true);
        directionsArea.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the directions
        directionsArea.setMargin(new Insets(10, 10, 10, 10)); // Add some margin
        directionsArea.setPreferredSize(new Dimension(400, 200)); // Set preferred size for readability
    
        // Add components to the center panel
        centerPanel.add(imageLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        centerPanel.add(directionsArea);
    
        JButton startButton = new JButton("Start Game");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        startButton.setBackground(Color.BLACK); // Button color
        startButton.setForeground(Color.BLACK); // Text color
        startButton.setFont(new Font("Arial", Font.BOLD, 16)); // Change font
        startButton.setBorder(BorderFactory.createEtchedBorder()); // Add border
        startButton.setFocusPainted(false); // Remove focus outline
        startButton.setPreferredSize(new Dimension(150, 40)); // Button size
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                homeFrame.dispose(); // Close the home frame
                startGame(); // Start the actual game
            }
        });
    
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
        centerPanel.add(startButton); // Add the start button to the center panel
    
        homeFrame.add(centerPanel, BorderLayout.CENTER); // Add center panel to the home frame
        homeFrame.setSize(500, 600);
        homeFrame.setVisible(true);
        homeFrame.setResizable(false);
    }
    

    private void startGame() {
        // Initialize game window
        setTitle("MLB Stadium Guessing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set fixed size for the JFrame and prevent resizing
        setSize(600, 400);
        setResizable(false); // Prevent the frame from being resized

        // Initialize components
        stadiumImageLabel = new JLabel();
        userGuessField = new JTextField(20); // Set a width for the text field
        feedbackLabel = new JLabel("Guess the team's stadium!");
        timerLabel = new JLabel("Time: 0 seconds");
        progressLabel = new JLabel("Stadium: 0 / 30");

        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(100, 30)); // Set size for the submit button

        // Load stadium images and team names
        loadStadiumData();

        // Select a random stadium
        setRandomStadium();

        // Create a panel for the top section (timer, progress, and feedback)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 1)); // Display in a 3-row layout
        topPanel.add(timerLabel);
        topPanel.add(progressLabel);
        topPanel.add(feedbackLabel);

        // Create a panel for input and button
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout()); // Set FlowLayout for input panel
        inputPanel.add(userGuessField);
        inputPanel.add(submitButton);
        inputPanel.setSize(500, 500);

        // Add components to the window
        add(topPanel, BorderLayout.NORTH); // Add top panel with timer, progress, feedback
        add(stadiumImageLabel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH); // Add the input panel at the bottom

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkGuess(); // Handle answer checking
            }
        });

        // Add key listener to allow "Return" key to submit the answer
        userGuessField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    checkGuess(); // Handle answer checking
                }
            }
        });

        // Start the timer
        startTime = System.currentTimeMillis();
        new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                timerLabel.setText("Time: " + elapsedTime + " seconds");
            }
        }).start();

        pack();
        setVisible(true);

        // Focus the user input field
        userGuessField.requestFocus();
    }

    private void loadStadiumData() {
        stadiumMap = new HashMap<>();
        // Populate with filenames and corresponding team names
        stadiumMap.put("dodgers.jpg", "Dodgers");
        stadiumMap.put("redsox.jpg", "Red Sox");
        stadiumMap.put("angels.jpg", "Angels");
        stadiumMap.put("astros.jpg", "Astros");
        stadiumMap.put("athletics.jpg", "Athletics");
        stadiumMap.put("bluejays.jpg", "Blue Jays");
        stadiumMap.put("braves.jpg", "Braves");
        stadiumMap.put("brewers.jpg", "Brewers");
        stadiumMap.put("cardinals.jpg", "Cardinals");
        stadiumMap.put("cleveland.jpg", "Guardians");
        stadiumMap.put("cubs.jpg", "Cubs");
        stadiumMap.put("diamondbacks.jpg", "Diamondbacks");
        stadiumMap.put("giants.jpg", "Giants");
        stadiumMap.put("mariners.jpg", "Mariners");
        stadiumMap.put("marlins.jpg", "Marlins");
        stadiumMap.put("mets.jpg", "Mets");
        stadiumMap.put("nationals.jpg", "Nationals");
        stadiumMap.put("orioles.jpg", "Orioles");
        stadiumMap.put("padres.jpg", "Padres");
        stadiumMap.put("phillies.jpg", "Phillies");
        stadiumMap.put("pirates.jpg", "Pirates");
        stadiumMap.put("rangers.jpg", "Rangers");
        stadiumMap.put("rays.jpg", "Rays");
        stadiumMap.put("reds.jpg", "Reds");
        stadiumMap.put("rockies.jpg", "Rockies");
        stadiumMap.put("royals.jpg", "Royals");
        stadiumMap.put("tigers.jpg", "Tigers");
        stadiumMap.put("twins.jpg", "Twins");
        stadiumMap.put("whitesox.jpg", "White Sox");
        stadiumMap.put("yankees.jpg", "Yankees");
    }

    private void setRandomStadium() {
        Random rand = new Random();
        Object[] keys = stadiumMap.keySet().toArray();
        currentStadium = (String) keys[rand.nextInt(keys.length)];
        ImageIcon stadiumImage = new ImageIcon(getClass().getResource("/" + currentStadium));
        stadiumImageLabel.setIcon(stadiumImage);
        stadiumCount++; // Increment the stadium count
        progressLabel.setText("Stadium: " + stadiumCount + " / 30"); // Update progress label

        // Check if the game is finished
        if (stadiumCount >= 30) {
            showFinalScore();
        }

        // Focus the user input field after setting a new stadium
        userGuessField.requestFocus();
    }

    private void checkGuess() {
        String userGuess = userGuessField.getText().trim(); // Trim to avoid extra spaces
        String correctAnswer = stadiumMap.get(currentStadium);

        // Check if the guess is correct
        if (userGuess.equalsIgnoreCase(correctAnswer)) {
            feedbackLabel.setText("Correct! The team is " + correctAnswer + ".");
            playSound("correct.wav"); // Play correct answer sound
            setRandomStadium(); // Set a new stadium
        } else {
            feedbackLabel.setText("Incorrect. Try again!");
            playSound("incorrect.wav"); // Play incorrect answer sound
        }

        // Clear the text field
        userGuessField.setText("");
    }

    private void playSound(String soundFile) {
        try {
            File sound = new File(soundFile); // Load sound file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(sound);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace(); // Handle exceptions
        }
    }

    private void showFinalScore() {
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        JOptionPane.showMessageDialog(this,
                "Game Over! You completed " + stadiumCount + " stadiums in " + elapsedTime + " seconds.",
                "Final Score", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0); // Exit the game
    }

    public static void main(String[] args) {
        new mlbStadiums(); // Launch the game
    }
}
