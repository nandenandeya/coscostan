package gui;

import gui.LoginForm;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    private JButton btnAdmin;
    private JButton btnUser;
    private JLabel lblTitle;
    
    public MainMenu() {
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("CosCosTan Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Title label
        lblTitle = new JLabel("SISTEM MANAJEMEN COSCOSTAN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        btnAdmin = new JButton("Login sebagai Admin");
        btnAdmin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdmin.setBackground(new Color(0, 102, 204));
        btnAdmin.setForeground(Color.WHITE);
        btnAdmin.setFocusPainted(false);
        btnAdmin.setPreferredSize(new Dimension(200, 50));
        
        btnUser = new JButton("Masuk sebagai User");
        btnUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUser.setBackground(new Color(76, 175, 80));
        btnUser.setForeground(Color.WHITE);
        btnUser.setFocusPainted(false);
        btnUser.setPreferredSize(new Dimension(200, 50));
        
        buttonPanel.add(btnAdmin);
        buttonPanel.add(btnUser);
        
        // Center panel untuk button
        JPanel centerPanel = new JPanel(new FlowLayout());
        centerPanel.setBackground(new Color(240, 240, 240));
        centerPanel.add(buttonPanel);
        
        // Add components to main panel
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Event listeners
        setupEventListeners();
        
        pack();
    }
    
    private void setupEventListeners() {
        btnAdmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAdminLogin();
            }
        });
        
        btnUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUserDashboard();
            }
        });
    }
    
    private void openAdminLogin() {
        LoginForm loginForm = new LoginForm(this);
        loginForm.setVisible(true);
        this.setVisible(false);
    }
    
    private void openUserDashboard() {
        User userDashboard = new User(this);
        userDashboard.setVisible(true);
        this.setVisible(false);
    }
    
    public void showMainMenu() {
        this.setVisible(true);
    }
}