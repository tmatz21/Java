package ui;

import dao.ApplicationDAO;
import dao.InternshipDAO;
import utils.RoleUtils;
import utils.Session;
import utils.UIFactory;
import utils.UITheme;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Dashboard {

    private final JFrame frame;
    private final String role;
    private final JLabel pageTitle;
    private final JLabel pageSubtitle;
    private final JPanel contentBody;

    private JButton activeButton;
    private JButton dashboardButton;
    private JButton internshipsButton;
    private JButton applicationsButton;
    private JButton postInternshipButton;
    private JButton chatButton;
    private JButton adminButton;

    public Dashboard(String role) {
        this.role = role == null ? "student" : role;
        this.frame = new JFrame("Internship Management System");
        this.pageTitle = UIFactory.createDisplayLabel("Dashboard");
        this.pageSubtitle = UIFactory.createBodyLabel("A better view of your internship work.");
        this.contentBody = new JPanel(new BorderLayout());

        contentBody.setOpaque(false);

        JPanel root = UIFactory.createAppPanel(new BorderLayout(24, 0));
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setSize(1180, 760);
        frame.setMinimumSize(new Dimension(1080, 700));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        showWelcome();
        frame.setVisible(true);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.SIDEBAR, 1, true),
                BorderFactory.createEmptyBorder(24, 18, 24, 18)
        ));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel appName = new JLabel("Internship Hub");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appName.setForeground(UITheme.TEXT_LIGHT);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        // User name
        String displayName = (Session.userName != null && !Session.userName.isEmpty()) ? Session.userName : "User";

        // plain text fallback — avoid emoji rendering issues
        JLabel userNameLabel = new JLabel(displayName);
        userNameLabel.setFont(UITheme.SECTION_FONT);
        userNameLabel.setForeground(new Color(213, 223, 221));
        userNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rolePill = UIFactory.createPillLabel(RoleUtils.displayRole(role) + " access", UITheme.PRIMARY, UITheme.TEXT_LIGHT);
        rolePill.setAlignmentX(Component.LEFT_ALIGNMENT);

        dashboardButton    = UIFactory.createSidebarButton("Overview");
        internshipsButton  = UIFactory.createSidebarButton("Internships");
        applicationsButton = UIFactory.createSidebarButton("Applications");

        dashboardButton.addActionListener(e    -> showWelcome());
        internshipsButton.addActionListener(e  -> showInternships());
        applicationsButton.addActionListener(e -> showApplications());

        sidebar.add(appName);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(userNameLabel);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(rolePill);
        sidebar.add(Box.createVerticalStrut(24));
        sidebar.add(dashboardButton);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(internshipsButton);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(applicationsButton);

        // Post Internship — business / admin only
        if (RoleUtils.canManageInternships(role)) {
            postInternshipButton = UIFactory.createSidebarButton("Post Internship");
            postInternshipButton.addActionListener(e -> new AddInternshipForm(frame, this::showInternships));
            sidebar.add(Box.createVerticalStrut(8));
            sidebar.add(postInternshipButton);
        }

        // Messages — ALL roles
        chatButton = UIFactory.createSidebarButton("Messages");
        chatButton.addActionListener(e -> showChat());
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(chatButton);

        // Admin Panel — admin only
        if ("admin".equalsIgnoreCase(role)) {
            adminButton = UIFactory.createSidebarButton("Admin Panel");
            adminButton.addActionListener(e -> showAdminPanel());
            sidebar.add(Box.createVerticalStrut(8));
            sidebar.add(adminButton);
        }

        sidebar.add(Box.createVerticalGlue());

        JButton logoutButton = UIFactory.createSecondaryButton("Log Out");
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        logoutButton.addActionListener(e -> logout());
        sidebar.add(logoutButton);

        return sidebar;
    }

    private JPanel buildMainArea() {
        JPanel mainArea = UIFactory.createAppPanel(new BorderLayout(0, 18));

        JPanel header = UIFactory.createSurfacePanel(new BorderLayout());
        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pageSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBlock.add(pageTitle);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(pageSubtitle);

        JLabel badge = UIFactory.createPillLabel(
                RoleUtils.canManageInternships(role) ? "Publishing enabled" : "Tracking enabled",
                UITheme.SURFACE_MUTED,
                UITheme.TEXT_PRIMARY
        );

        header.add(titleBlock, BorderLayout.WEST);
        header.add(badge, BorderLayout.EAST);

        mainArea.add(header, BorderLayout.NORTH);
        mainArea.add(contentBody, BorderLayout.CENTER);
        return mainArea;
    }

    private void showWelcome() {
        String displayName = (Session.userName != null && !Session.userName.isEmpty()) ? Session.userName : "there";

        JPanel welcome = new JPanel(new BorderLayout(0, 14));
        welcome.setOpaque(false);

        // Greeting banner
        JPanel greetingPanel = UIFactory.createSurfacePanel(new BorderLayout(0, 4));
        JLabel greeting = new JLabel("Welcome back, " + displayName + "!");
        greeting.setFont(new Font("Segoe UI", Font.BOLD, 22));
        greeting.setForeground(UITheme.PRIMARY);
        JLabel greetingSub = UIFactory.createBodyLabel(
                RoleUtils.canManageInternships(role)
                        ? "Here's what's happening with your listings today."
                        : "Here's a snapshot of your internship journey."
        );
        greetingPanel.add(greeting, BorderLayout.NORTH);
        greetingPanel.add(greetingSub, BorderLayout.CENTER);

        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 14));
        stats.setOpaque(false);
        stats.add(UIFactory.createStatCard(
                "Open internships",
                String.valueOf(InternshipDAO.getAllInternships().size()),
                "Live listings on the platform",
                UITheme.PRIMARY
        ));
        stats.add(UIFactory.createStatCard(
                "Applications",
                String.valueOf(countApplications()),
                RoleUtils.canManageInternships(role) ? "Awaiting your review" : "Submissions from your account",
                UITheme.ACCENT
        ));
        stats.add(UIFactory.createStatCard(
                "Workspace",
                RoleUtils.displayRole(role),
                RoleUtils.canManageInternships(role) ? "Manage postings and approvals" : "Browse and track your progress",
                UITheme.SUCCESS
        ));

        JPanel notes = new JPanel(new GridLayout(1, 2, 14, 14));
        notes.setOpaque(false);
        notes.add(UIFactory.createInfoCard(
                "How this works",
                "Use the left sidebar to navigate between internships, applications, and messages."
        ));
        notes.add(UIFactory.createInfoCard(
                RoleUtils.canManageInternships(role) ? "Business tools" : "Intern tools",
                RoleUtils.canManageInternships(role)
                        ? "Post openings, review applicants, and message students."
                        : "Search listings, apply, and chat with businesses."
        ));

        JPanel lowerSection = new JPanel(new BorderLayout(0, 14));
        lowerSection.setOpaque(false);
        lowerSection.add(stats, BorderLayout.NORTH);
        lowerSection.add(notes, BorderLayout.CENTER);

        welcome.add(greetingPanel, BorderLayout.NORTH);
        welcome.add(lowerSection, BorderLayout.CENTER);

        setView("Dashboard", "Welcome back, " + displayName + ".", welcome, dashboardButton);
    }

    private void showInternships() {
        setView("Internships", "Browse and review available opportunities.",
                new ViewInternshipsPanel(role), internshipsButton);
    }

    private void showApplications() {
        JComponent panel = RoleUtils.canManageInternships(role)
                ? new ViewApplicationsPanel()
                : new MyApplicationsPanel();
        setView("Applications",
                RoleUtils.canManageInternships(role)
                        ? "Review incoming applications and update outcomes."
                        : "Track your application progress and statuses.",
                panel, applicationsButton);
    }

    private void showChat() {
        String subtitle = RoleUtils.canManageInternships(role)
                ? "Chat with students about their applications."
                : "Chat with businesses about your applications.";
        setView("Messages", subtitle, new ChatPanel(Session.userId, role), chatButton);
    }

    private void showAdminPanel() {
        setView("Admin Panel", "Manage users, internships, and applications.",
                new AdminPanel(), adminButton);
    }

    private void setView(String title, String subtitle, JComponent component, JButton button) {
        pageTitle.setText(title);
        pageSubtitle.setText(subtitle);

        contentBody.removeAll();
        contentBody.add(component, BorderLayout.CENTER);
        contentBody.revalidate();
        contentBody.repaint();

        setActiveButton(button);
    }

    private void setActiveButton(JButton button) {
        if (activeButton != null) UIFactory.setSidebarButtonActive(activeButton, false);
        activeButton = button;
        if (activeButton != null) UIFactory.setSidebarButtonActive(activeButton, true);
    }

    private void logout() {
        frame.dispose();
        new LoginForm();
    }

    private int countApplications() {
        ResultSet rs = null;
        try {
            rs = RoleUtils.canManageInternships(role)
                    ? ApplicationDAO.getAllApplications()
                    : ApplicationDAO.getUserApplications(Session.userId);
            int count = 0;
            while (rs != null && rs.next()) count++;
            return count;
        } catch (Exception e) {
            return 0;
        } finally {
            closeResultSet(rs);
        }
    }

    private void closeResultSet(ResultSet rs) {
        if (rs == null) return;
        try {
            Statement st = rs.getStatement();
            Connection conn = st != null ? st.getConnection() : null;
            rs.close();
            if (st != null) st.close();
            if (conn != null) conn.close();
        } catch (Exception ignored) {}
    }
}