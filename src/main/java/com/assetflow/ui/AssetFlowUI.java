package com.assetflow.ui;

import com.assetflow.DatabaseConnection;
import com.assetflow.model.Employee;
import com.assetflow.model.Asset;
import com.assetflow.repository.DashboardRepository;
import com.assetflow.repository.EmployeeRepository;
import com.assetflow.repository.AssetRepository;
import com.assetflow.repository.AllocationRepository;
import com.assetflow.repository.AssetReturnRepository;
import com.assetflow.repository.ExitClearanceRepository;
import com.assetflow.repository.ActivityLogRepository;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

public class AssetFlowUI extends JFrame {
    private final Color sidebarColor = new Color(24, 43, 40);
    private final Color sidebarHover = new Color(43, 69, 59);
    private final Color olive = new Color(76, 112, 76);
    private final Color oliveDark = new Color(39, 66, 48);
    private final Color oliveLight = new Color(229, 238, 222);
    private final Color pageBackground = new Color(243, 246, 241);
    private final Color surface = Color.WHITE;
    private final Color borderColor = new Color(218, 226, 216);
    private final Color textColor = new Color(34, 45, 37);
    private final Color mutedText = new Color(91, 105, 94);
    private final Color blue = new Color(76, 112, 165);
    private final Color green = new Color(47, 126, 84);
    private final Color amber = new Color(173, 117, 43);
    private final Color red = new Color(167, 66, 62);

    private final Font regularFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font mediumFont = new Font("Segoe UI", Font.BOLD, 13);
    private final Font headingFont = new Font("Segoe UI", Font.BOLD, 25);
    private final Font sectionFont = new Font("Segoe UI", Font.BOLD, 16);

    private JPanel contentPanel;
    private JPanel sidebar;
    private JLabel pageTitle;
    private JButton activeNavigationButton;
    private String activePage = "Dashboard";

    private final DashboardRepository dashboardRepository = new DashboardRepository();
    private final EmployeeRepository employeeRepository = new EmployeeRepository();
    private final AssetRepository assetRepository = new AssetRepository();
    private final AllocationRepository allocationRepository = new AllocationRepository();
    private final AssetReturnRepository assetReturnRepository = new AssetReturnRepository();
    private final ExitClearanceRepository exitClearanceRepository = new ExitClearanceRepository();
    private final ActivityLogRepository activityLogRepository = new ActivityLogRepository();

    public AssetFlowUI() {
        setTitle("AssetFlow | IT Asset Lifecycle Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setSize(1440, 900);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        UIManager.put("OptionPane.messageFont", regularFont);
        UIManager.put("OptionPane.buttonFont", mediumFont);
        UIManager.put("Button.font", mediumFont);
        UIManager.put("Label.foreground", textColor);

        buildApplication();
        showDashboard();
        setVisible(true);
    }

    private void buildApplication() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(pageBackground);

        sidebar = new JPanel();
        sidebar.setBackground(sidebarColor);
        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(26, 15, 20, 15));
        buildSidebar();

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(pageBackground);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(surface);
        topBar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, borderColor),
                new EmptyBorder(20, 30, 20, 30)));

        pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(headingFont);
        pageTitle.setForeground(textColor);

        JLabel administrator = new JLabel("●  ASSET ADMINISTRATOR");
        administrator.setFont(new Font("Segoe UI", Font.BOLD, 12));
        administrator.setForeground(oliveDark);

        topBar.add(pageTitle, BorderLayout.WEST);
        topBar.add(administrator, BorderLayout.EAST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(pageBackground);
        contentPanel.setBorder(new EmptyBorder(24, 28, 25, 28));

        main.add(topBar, BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);
        root.add(sidebar, BorderLayout.WEST);
        root.add(main, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void buildSidebar() {
        sidebar.removeAll();

        JLabel brand = new JLabel("AssetFlow");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 27));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("IT ASSET MANAGEMENT");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        subtitle.setForeground(new Color(194, 213, 185));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel workspace = new JLabel("WORKSPACE");
        workspace.setFont(new Font("Segoe UI", Font.BOLD, 11));
        workspace.setForeground(new Color(180, 201, 187));
        workspace.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(7));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(36));
        sidebar.add(workspace);
        sidebar.add(Box.createVerticalStrut(14));

        addNavigation("Dashboard", "showDashboard");
        addNavigation("Employees", "showEmployees");
        addNavigation("IT Assets", "showAssets");
        addNavigation("Allocations", "showAllocations");
        addNavigation("Asset Returns", "showReturns");
        addNavigation("Exit Clearance", "showExitClearance");
        addNavigation("Activity Logs", "showActivityLogs");

        sidebar.add(Box.createVerticalGlue());
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(72, 96, 82));
        separator.setBackground(new Color(72, 96, 82));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JLabel footerTitle = new JLabel("ASSETFLOW WORKSPACE");
        footerTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        footerTitle.setForeground(new Color(194, 213, 185));
        footerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel footerText = new JLabel("Track. Manage. Protect.");
        footerText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerText.setForeground(new Color(205, 216, 207));
        footerText.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(separator);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(footerTitle);
        sidebar.add(Box.createVerticalStrut(7));
        sidebar.add(footerText);
        sidebar.revalidate();
        sidebar.repaint();
    }

    private void addNavigation(String name, String action) {
        JButton button = new JButton(name);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(new Color(231, 239, 232));
        button.setBackground(sidebarColor);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(14, 16, 14, 10));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 49));
        button.setPreferredSize(new Dimension(215, 49));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            switch (action) {
                case "showDashboard": showDashboard(); break;
                case "showEmployees": showEmployees(); break;
                case "showAssets": showAssets(); break;
                case "showAllocations": showAllocations(); break;
                case "showReturns": showReturns(); break;
                case "showExitClearance": showExitClearance(); break;
                case "showActivityLogs": showActivityLogs(); break;
                default: break;
            }
            setActiveNavigation(button, name);
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button != activeNavigationButton) button.setBackground(sidebarHover);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (button != activeNavigationButton) button.setBackground(sidebarColor);
            }
        });

        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(7));
        if (name.equals(activePage)) setActiveNavigation(button, name);
    }

    private void setActiveNavigation(JButton button, String name) {
        if (activeNavigationButton != null) {
            activeNavigationButton.setBackground(sidebarColor);
            activeNavigationButton.setForeground(new Color(231, 239, 232));
            activeNavigationButton.setBorder(new EmptyBorder(14, 16, 14, 10));
        }
        activeNavigationButton = button;
        activePage = name;
        button.setBackground(olive);
        button.setForeground(Color.WHITE);
        button.setBorder(new CompoundBorder(
                new MatteBorder(0, 4, 0, 0, new Color(203, 226, 157)),
                new EmptyBorder(14, 12, 14, 10)));
    }

    private void showPage(String title, JComponent component) {
        pageTitle.setText(title);
        contentPanel.removeAll();
        contentPanel.add(component, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel pageContainer() {
        JPanel panel = new JPanel(new BorderLayout(0, 17));
        panel.setBackground(pageBackground);
        return panel;
    }

    private JPanel whitePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(surface);
        panel.setBorder(new CompoundBorder(
                new LineBorder(borderColor, 1),
                new EmptyBorder(18, 18, 18, 18)));
        return panel;
    }

    private JLabel sectionHeading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(sectionFont);
        label.setForeground(textColor);
        return label;
    }

    private JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(regularFont);
        label.setForeground(mutedText);
        return label;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(mediumFont);
        button.setForeground(Color.WHITE);
        button.setBackground(olive);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 17, 11, 17));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        addButtonHover(button, olive, oliveDark);
        return button;
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(mediumFont);
        button.setForeground(oliveDark);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
                new LineBorder(new Color(190, 205, 189), 1),
                new EmptyBorder(10, 15, 10, 15)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        addButtonHover(button, Color.WHITE, oliveLight);
        return button;
    }

    private void addButtonHover(JButton button, Color normal, Color hover) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hover);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(normal);
            }
        });
    }

    // DASHBOARD
    private void showDashboard() {
        JPanel page = pageContainer();

        JPanel welcome = new JPanel(new BorderLayout(15, 0));
        welcome.setBackground(oliveLight);
        welcome.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel welcomeText = new JPanel();
        welcomeText.setOpaque(false);
        welcomeText.setLayout(new BoxLayout(welcomeText, BoxLayout.Y_AXIS));
        JLabel heading = new JLabel("Welcome to AssetFlow");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 23));
        heading.setForeground(oliveDark);
        JLabel description = mutedLabel("Manage employees, track equipment and oversee the asset lifecycle.");
        welcomeText.add(heading);
        welcomeText.add(Box.createVerticalStrut(7));
        welcomeText.add(description);

        JLabel date = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        date.setFont(mediumFont);
        date.setForeground(oliveDark);
        welcome.add(welcomeText, BorderLayout.CENTER);
        welcome.add(date, BorderLayout.EAST);

        int employees = 0, assets = 0, available = 0, allocated = 0;
        try {
            employees = dashboardRepository.getTotalEmployees();
            assets = dashboardRepository.getTotalAssets();
            available = dashboardRepository.getAvailableAssets();
            allocated = dashboardRepository.getAllocatedAssets();
        } catch (SQLException e) {
            showDatabaseError(e);
        }

        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
        cards.setBackground(pageBackground);
        cards.add(createStatCard("TOTAL EMPLOYEES", employees, olive));
        cards.add(createStatCard("TOTAL ASSETS", assets, blue));
        cards.add(createStatCard("AVAILABLE", available, green));
        cards.add(createStatCard("ALLOCATED", allocated, amber));

        JPanel middle = new JPanel(new GridLayout(1, 2, 16, 0));
        middle.setBackground(pageBackground);

        JPanel statusPanel = whitePanel();
        statusPanel.add(sectionHeading("Asset Status Overview"), BorderLayout.NORTH);
        JPanel statusContent = new JPanel();
        statusContent.setBackground(surface);
        statusContent.setLayout(new BoxLayout(statusContent, BoxLayout.Y_AXIS));
        statusContent.setBorder(new EmptyBorder(18, 0, 0, 0));
        statusContent.add(statusRow("Available assets", available, green));
        statusContent.add(Box.createVerticalStrut(17));
        statusContent.add(statusRow("Allocated assets", allocated, olive));
        statusContent.add(Box.createVerticalStrut(17));
        statusContent.add(statusRow("Maintenance / other", Math.max(0, assets - available - allocated), amber));
        statusPanel.add(statusContent, BorderLayout.CENTER);

        JPanel activityPanel = whitePanel();
        activityPanel.add(sectionHeading("Recent Activity"), BorderLayout.NORTH);
        DefaultTableModel recentModel = createTableModel(new String[]{"Activity", "Date"});
        JTable recentTable = createTable(recentModel);
        try {
            List<Object[]> logs = activityLogRepository.getAllLogs();
            for (int i = 0; i < Math.min(logs.size(), 6); i++) {
                Object[] log = logs.get(i);
                recentModel.addRow(new Object[]{log.length > 1 ? log[1] : "", log.length > 2 ? log[2] : ""});
            }
        } catch (SQLException e) {
            showDatabaseError(e);
        }
        activityPanel.add(new JScrollPane(recentTable), BorderLayout.CENTER);
        middle.add(statusPanel);
        middle.add(activityPanel);

        JPanel quickActions = whitePanel();
        quickActions.add(sectionHeading("Quick Actions"), BorderLayout.NORTH);
        JPanel actions = new JPanel(new GridLayout(1, 5, 10, 0));
        actions.setBackground(surface);
        actions.setBorder(new EmptyBorder(14, 0, 0, 0));
        JButton addEmployee = primaryButton("Add Employee");
        addEmployee.addActionListener(e -> showEmployeeForm());
        JButton addAsset = primaryButton("Add IT Asset");
        addAsset.addActionListener(e -> showAssetForm());
        JButton allocate = secondaryButton("Allocate Asset");
        allocate.addActionListener(e -> showAllocationForm());
        JButton recordReturn = secondaryButton("Record Return");
        recordReturn.addActionListener(e -> showReturnForm());
        JButton clearance = secondaryButton("Exit Clearance");
        clearance.addActionListener(e -> showClearanceForm());
        actions.add(addEmployee);
        actions.add(addAsset);
        actions.add(allocate);
        actions.add(recordReturn);
        actions.add(clearance);
        quickActions.add(actions, BorderLayout.CENTER);

        JPanel dashboardContent = new JPanel(new BorderLayout(0, 16));
        dashboardContent.setBackground(pageBackground);
        dashboardContent.add(welcome, BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(pageBackground);
        center.add(cards, BorderLayout.NORTH);
        center.add(middle, BorderLayout.CENTER);
        dashboardContent.add(center, BorderLayout.CENTER);
        dashboardContent.add(quickActions, BorderLayout.SOUTH);
        page.add(dashboardContent, BorderLayout.CENTER);

        JButton refresh = secondaryButton("Refresh Dashboard");
        refresh.addActionListener(e -> showDashboard());
        page.add(refresh, BorderLayout.SOUTH);
        showPage("Dashboard", page);
    }

    private JPanel createStatCard(String title, int value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(surface);
        card.setBorder(new CompoundBorder(new LineBorder(borderColor, 1), new EmptyBorder(18, 18, 16, 18)));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(mutedText);
        JLabel number = new JLabel(String.valueOf(value));
        number.setFont(new Font("Segoe UI", Font.BOLD, 32));
        number.setForeground(accent);
        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(10, 5));
        card.add(label, BorderLayout.NORTH);
        card.add(number, BorderLayout.CENTER);
        card.add(bar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel statusRow(String label, int count, Color color) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(surface);
        JPanel strip = new JPanel();
        strip.setBackground(color);
        strip.setPreferredSize(new Dimension(5, 34));
        JLabel name = new JLabel(label);
        name.setFont(regularFont);
        name.setForeground(textColor);
        JLabel number = new JLabel(String.valueOf(count));
        number.setFont(new Font("Segoe UI", Font.BOLD, 18));
        number.setForeground(color);
        row.add(strip, BorderLayout.WEST);
        row.add(name, BorderLayout.CENTER);
        row.add(number, BorderLayout.EAST);
        return row;
    }

    // TABLES AND SEARCH
    private DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(regularFont);
        table.setForeground(textColor);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(220, 234, 211));
        table.setSelectionForeground(new Color(32, 54, 38));
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(229, 233, 225));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(oliveDark);
        header.setBackground(new Color(235, 241, 230));
        header.setPreferredSize(new Dimension(100, 41));
        header.setReorderingAllowed(false);
        return table;
    }

    private void showSearchableTable(String title, DefaultTableModel model, String description,
                                     Runnable refreshAction, Runnable addAction) {
        JPanel page = pageContainer();
        JPanel intro = new JPanel();
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));
        intro.setBackground(pageBackground);
        intro.add(mutedLabel(description));

        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setBackground(pageBackground);
        JTextField search = createTextField();
        search.setToolTipText("Search records...");
        search.setPreferredSize(new Dimension(250, 43));

        JButton clear = secondaryButton("Clear");
        JButton refresh = secondaryButton("Refresh");
        JButton add = primaryButton("+ Add");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setBackground(pageBackground);
        buttons.add(clear);
        buttons.add(refresh);
        buttons.add(add);
        searchBar.add(search, BorderLayout.CENTER);
        searchBar.add(buttons, BorderLayout.EAST);

        JPanel searchSection = new JPanel(new BorderLayout(0, 12));
        searchSection.setBackground(pageBackground);
        searchSection.add(intro, BorderLayout.NORTH);
        searchSection.add(searchBar, BorderLayout.CENTER);

        JTable table = createTable(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(borderColor, 1));
        scroll.getViewport().setBackground(Color.WHITE);

        JLabel count = mutedLabel("Displaying " + model.getRowCount() + " of " + model.getRowCount() + " records");
        Runnable updateCount = () -> count.setText("Displaying " + table.getRowCount() + " of " + model.getRowCount() + " records");
        search.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String query = search.getText().trim();
                sorter.setRowFilter(query.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(query)));
                updateCount.run();
            }
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
        });

        clear.addActionListener(e -> search.setText(""));
        refresh.addActionListener(e -> refreshAction.run());
        add.addActionListener(e -> addAction.run());

        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(pageBackground);
        tablePanel.add(count, BorderLayout.NORTH);
        tablePanel.add(scroll, BorderLayout.CENTER);
        page.add(searchSection, BorderLayout.NORTH);
        page.add(tablePanel, BorderLayout.CENTER);
        showPage(title, page);
    }

    // EMPLOYEES
    private void showEmployees() {
        DefaultTableModel model = createTableModel(new String[]{"ID", "Employee Code", "Name", "Department", "Designation", "Email", "Status"});
        try {
            for (Employee employee : employeeRepository.getAllEmployees()) {
                model.addRow(new Object[]{employee.getEmployeeId(), employee.getEmployeeCode(), employee.getEmployeeName(),
                        employee.getDepartment(), employee.getDesignation(), employee.getEmail(), employee.getEmploymentStatus()});
            }
        } catch (SQLException e) { showDatabaseError(e); }
        showSearchableTable("Employees", model, "Manage and review employee records.", this::showEmployees, this::showEmployeeForm);
    }

    private void showEmployeeForm() {
        JTextField code = createTextField(), name = createTextField(), department = createTextField();
        JTextField designation = createTextField(), email = createTextField(), joiningDate = createTextField();
        joiningDate.setText(LocalDate.now().toString());
        JPanel form = formPanel();
        addFormRow(form, 0, "Employee Code *", code);
        addFormRow(form, 1, "Full Name *", name);
        addFormRow(form, 2, "Department *", department);
        addFormRow(form, 3, "Designation *", designation);
        addFormRow(form, 4, "Email *", email);
        addFormRow(form, 5, "Joining Date (YYYY-MM-DD) *", joiningDate);
        if (JOptionPane.showConfirmDialog(this, form, "Add Employee", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        if (isBlank(code, name, department, designation, email, joiningDate)) { showMessage("Please complete all required fields."); return; }
        if (!email.getText().trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) { showMessage("Enter a valid email address."); return; }
        try {
            Date date = Date.valueOf(joiningDate.getText().trim());
            String sql = "INSERT INTO employees (employee_code, employee_name, department, designation, email, joining_date, employment_status) VALUES (?, ?, ?, ?, ?, ?, 'Active')";
            try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
                s.setString(1, code.getText().trim()); s.setString(2, name.getText().trim());
                s.setString(3, department.getText().trim()); s.setString(4, designation.getText().trim());
                s.setString(5, email.getText().trim()); s.setDate(6, date); s.executeUpdate();
            }
            activityLogRepository.logAction("Added employee: " + name.getText().trim());
            showMessage("Employee added successfully.");
            showEmployees();
        } catch (IllegalArgumentException e) { showMessage("Enter the joining date as YYYY-MM-DD."); }
        catch (SQLException e) { showDatabaseError(e); }
    }

    // IT ASSETS
    private void showAssets() {
        DefaultTableModel model = createTableModel(new String[]{"ID", "Asset Code", "Asset Name", "Type", "Serial Number", "Purchase Date", "Condition", "Status"});
        try {
            for (Asset asset : assetRepository.getAllAssets()) {
                model.addRow(new Object[]{asset.getAssetId(), asset.getAssetCode(), asset.getAssetName(), asset.getAssetType(),
                        asset.getSerialNumber(), asset.getPurchaseDate(), asset.getAssetCondition(), asset.getAssetStatus()});
            }
        } catch (SQLException e) { showDatabaseError(e); }
        showSearchableTable("IT Assets", model, "Browse inventory, check availability and track asset condition.", this::showAssets, this::showAssetForm);
    }

    private void showAssetForm() {
        JTextField code = createTextField(), name = createTextField(), serial = createTextField(), purchaseDate = createTextField();
        purchaseDate.setText(LocalDate.now().toString());
        JComboBox<String> type = new JComboBox<>(new String[]{"Laptop", "Desktop", "Monitor", "Keyboard", "Mouse", "Mobile Phone", "Printer", "Other"});
        JComboBox<String> condition = new JComboBox<>(new String[]{"Good", "Fair", "Damaged"});
        styleField(type); styleField(condition);
        JPanel form = formPanel();
        addFormRow(form, 0, "Asset Code *", code); addFormRow(form, 1, "Asset Name *", name);
        addFormRow(form, 2, "Asset Type", type); addFormRow(form, 3, "Serial Number *", serial);
        addFormRow(form, 4, "Purchase Date (YYYY-MM-DD)", purchaseDate); addFormRow(form, 5, "Condition", condition);
        if (JOptionPane.showConfirmDialog(this, form, "Add IT Asset", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        if (isBlank(code, name, serial, purchaseDate)) { showMessage("Please complete all required fields."); return; }
        try {
            Date date = Date.valueOf(purchaseDate.getText().trim());
            String sql = "INSERT INTO assets (asset_code, asset_name, asset_type, serial_number, purchase_date, asset_condition, asset_status) VALUES (?, ?, ?, ?, ?, ?, 'Available')";
            try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
                s.setString(1, code.getText().trim()); s.setString(2, name.getText().trim());
                s.setString(3, type.getSelectedItem().toString()); s.setString(4, serial.getText().trim());
                s.setDate(5, date); s.setString(6, condition.getSelectedItem().toString()); s.executeUpdate();
            }
            activityLogRepository.logAction("Added IT asset: " + name.getText().trim());
            showMessage("IT asset added successfully."); showAssets();
        } catch (IllegalArgumentException e) { showMessage("Enter the purchase date as YYYY-MM-DD."); }
        catch (SQLException e) { showDatabaseError(e); }
    }

    // ALLOCATIONS
    private void showAllocations() {
        DefaultTableModel model = createTableModel(new String[]{"Allocation ID", "Asset", "Employee", "Allocation Date", "Return Date", "Status"});
        try { for (Object[] row : allocationRepository.getAllAllocations()) model.addRow(row); }
        catch (SQLException e) { showDatabaseError(e); }
        JPanel page = pageContainer();
        JPanel intro = new JPanel(new BorderLayout(10, 0)); intro.setBackground(pageBackground);
        intro.add(mutedLabel("Assign available equipment to active employees and review assignments."), BorderLayout.CENTER);
        JButton add = primaryButton("+ Allocate Asset"); add.addActionListener(e -> showAllocationForm()); intro.add(add, BorderLayout.EAST);
        JTable table = createTable(model); JScrollPane scroll = new JScrollPane(table); scroll.setBorder(new LineBorder(borderColor, 1));
        page.add(intro, BorderLayout.NORTH); page.add(scroll, BorderLayout.CENTER); showPage("Allocations", page);
    }

    private void showAllocationForm() {
        JComboBox<ChoiceItem> employeeBox = new JComboBox<>(), assetBox = new JComboBox<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement es = c.prepareStatement("SELECT employee_id, employee_code, employee_name FROM employees WHERE employment_status='Active' ORDER BY employee_name");
             ResultSet er = es.executeQuery()) {
            while (er.next()) employeeBox.addItem(new ChoiceItem(er.getInt("employee_id"), er.getString("employee_code") + " — " + er.getString("employee_name")));
            try (PreparedStatement as = c.prepareStatement("SELECT asset_id, asset_code, asset_name FROM assets WHERE asset_status='Available' ORDER BY asset_name");
                 ResultSet ar = as.executeQuery()) {
                while (ar.next()) assetBox.addItem(new ChoiceItem(ar.getInt("asset_id"), ar.getString("asset_code") + " — " + ar.getString("asset_name")));
            }
        } catch (SQLException e) { showDatabaseError(e); return; }
        if (employeeBox.getItemCount() == 0 || assetBox.getItemCount() == 0) { showMessage("An active employee and an available asset are required."); return; }
        JTextField date = createTextField(); date.setText(LocalDate.now().toString());
        JPanel form = formPanel(); addFormRow(form, 0, "Employee", employeeBox); addFormRow(form, 1, "Available Asset", assetBox); addFormRow(form, 2, "Allocation Date (YYYY-MM-DD)", date);
        if (JOptionPane.showConfirmDialog(this, form, "Allocate Asset", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try {
            Date.valueOf(date.getText().trim());
            ChoiceItem employee = (ChoiceItem) employeeBox.getSelectedItem(), asset = (ChoiceItem) assetBox.getSelectedItem();
            if (employee == null || asset == null) { showMessage("Select an employee and an asset."); return; }
            allocationRepository.allocateAsset(asset.id, employee.id);
            activityLogRepository.logAction("Allocated asset ID " + asset.id + " to employee ID " + employee.id);
            showMessage("Asset allocated successfully."); showAllocations();
        } catch (IllegalArgumentException e) { showMessage("Enter the allocation date as YYYY-MM-DD."); }
        catch (SQLException e) { showDatabaseError(e); }
    }

    // RETURNS
    private void showReturns() {
        DefaultTableModel model = createTableModel(new String[]{"Return ID", "Allocation ID", "Asset", "Employee", "Return Date", "Condition", "Remarks"});
        try { for (Object[] row : assetReturnRepository.getAllReturns()) model.addRow(row); }
        catch (SQLException e) { showDatabaseError(e); }
        JPanel page = pageContainer();
        JPanel intro = new JPanel(new BorderLayout(10, 0)); intro.setBackground(pageBackground);
        intro.add(mutedLabel("Record returned equipment and update its condition."), BorderLayout.CENTER);
        JButton add = primaryButton("+ Record Return"); add.addActionListener(e -> showReturnForm()); intro.add(add, BorderLayout.EAST);
        JTable table = createTable(model); JScrollPane scroll = new JScrollPane(table); scroll.setBorder(new LineBorder(borderColor, 1));
        page.add(intro, BorderLayout.NORTH); page.add(scroll, BorderLayout.CENTER); showPage("Asset Returns", page);
    }

    private void showReturnForm() {
        JComboBox<ChoiceItem> allocationBox = new JComboBox<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT al.allocation_id, a.asset_code, a.asset_name, e.employee_name FROM allocations al JOIN assets a ON al.asset_id=a.asset_id JOIN employees e ON al.employee_id=e.employee_id WHERE al.allocation_status='Allocated' ORDER BY al.allocation_id");
             ResultSet rs = s.executeQuery()) {
            while (rs.next()) allocationBox.addItem(new ChoiceItem(rs.getInt("allocation_id"), "AL-" + rs.getInt("allocation_id") + " | " + rs.getString("asset_code") + " — " + rs.getString("asset_name") + " | " + rs.getString("employee_name")));
        } catch (SQLException e) { showDatabaseError(e); return; }
        if (allocationBox.getItemCount() == 0) { showMessage("There are no active allocations to return."); return; }
        JComboBox<String> condition = new JComboBox<>(new String[]{"Good", "Fair", "Damaged"}); styleField(condition);
        JTextField remarks = createTextField();
        JPanel form = formPanel(); addFormRow(form, 0, "Allocated Asset", allocationBox); addFormRow(form, 1, "Return Condition", condition); addFormRow(form, 2, "Remarks", remarks);
        if (JOptionPane.showConfirmDialog(this, form, "Record Asset Return", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        ChoiceItem selected = (ChoiceItem) allocationBox.getSelectedItem(); if (selected == null) return;
        try {
            assetReturnRepository.returnAsset(selected.id, condition.getSelectedItem().toString(), remarks.getText().trim());
            activityLogRepository.logAction("Recorded return for allocation ID " + selected.id);
            showMessage("Asset return recorded successfully."); showReturns();
        } catch (SQLException e) { showDatabaseError(e); }
    }

    // EXIT CLEARANCE
    private void showExitClearance() {
        DefaultTableModel model = createTableModel(new String[]{"Clearance ID", "Employee Code", "Employee Name", "Exit Date", "Assets Returned", "Data Handover", "Access Revoked", "Status"});
        try { for (Object[] row : exitClearanceRepository.getAllClearances()) model.addRow(row); }
        catch (SQLException e) { showDatabaseError(e); }
        JPanel page = pageContainer();
        JPanel intro = new JPanel(new BorderLayout(10, 0)); intro.setBackground(pageBackground);
        intro.add(mutedLabel("Track equipment returns, data handover and access revocation."), BorderLayout.CENTER);
        JButton add = primaryButton("+ New Clearance"); add.addActionListener(e -> showClearanceForm()); intro.add(add, BorderLayout.EAST);
        JTable table = createTable(model); JScrollPane scroll = new JScrollPane(table); scroll.setBorder(new LineBorder(borderColor, 1));
        page.add(intro, BorderLayout.NORTH); page.add(scroll, BorderLayout.CENTER); showPage("Exit Clearance", page);
    }

    private void showClearanceForm() {
        JComboBox<ChoiceItem> employeeBox = new JComboBox<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT employee_id, employee_code, employee_name FROM employees WHERE employment_status='Active' ORDER BY employee_name");
             ResultSet rs = s.executeQuery()) {
            while (rs.next()) employeeBox.addItem(new ChoiceItem(rs.getInt("employee_id"), rs.getString("employee_code") + " — " + rs.getString("employee_name")));
        } catch (SQLException e) { showDatabaseError(e); return; }
        if (employeeBox.getItemCount() == 0) { showMessage("No active employees are available."); return; }
        JTextField exitDate = createTextField(); exitDate.setText(LocalDate.now().toString());
        JCheckBox assetsReturned = new JCheckBox("All assigned assets returned");
        JCheckBox dataHandover = new JCheckBox("Data handover completed");
        JCheckBox accessRevoked = new JCheckBox("System access revoked");
        styleCheckBox(assetsReturned); styleCheckBox(dataHandover); styleCheckBox(accessRevoked);
        JPanel form = formPanel(); addFormRow(form, 0, "Employee", employeeBox); addFormRow(form, 1, "Exit Date (YYYY-MM-DD)", exitDate);
        addFormRow(form, 2, "Asset Return", assetsReturned); addFormRow(form, 3, "Data Handover", dataHandover); addFormRow(form, 4, "Access Revocation", accessRevoked);
        if (JOptionPane.showConfirmDialog(this, form, "Employee Exit Clearance", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        ChoiceItem employee = (ChoiceItem) employeeBox.getSelectedItem(); if (employee == null) return;
        try {
            Date date = Date.valueOf(exitDate.getText().trim());
            exitClearanceRepository.saveClearance(employee.id, date, assetsReturned.isSelected(), dataHandover.isSelected(), accessRevoked.isSelected());
            activityLogRepository.logAction("Created exit clearance for employee ID " + employee.id);
            showMessage("Exit clearance saved successfully."); showExitClearance();
        } catch (IllegalArgumentException e) { showMessage("Enter the exit date as YYYY-MM-DD."); }
        catch (SQLException e) { showDatabaseError(e); }
    }

    // ACTIVITY LOGS
    private void showActivityLogs() {
        DefaultTableModel model = createTableModel(new String[]{"Log ID", "Action", "Date and Time"});
        try { for (Object[] row : activityLogRepository.getAllLogs()) model.addRow(row); }
        catch (SQLException e) { showDatabaseError(e); }
        JPanel page = pageContainer();
        JPanel intro = new JPanel(new BorderLayout(10, 0)); intro.setBackground(pageBackground);
        intro.add(mutedLabel("Review a chronological history of actions performed in AssetFlow."), BorderLayout.CENTER);
        JButton refresh = secondaryButton("Refresh Logs"); refresh.addActionListener(e -> showActivityLogs()); intro.add(refresh, BorderLayout.EAST);
        JTable table = createTable(model); JScrollPane scroll = new JScrollPane(table); scroll.setBorder(new LineBorder(borderColor, 1));
        page.add(intro, BorderLayout.NORTH); page.add(scroll, BorderLayout.CENTER); showPage("Activity Logs", page);
    }

    // FORM HELPERS
    private JPanel formPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(surface);
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        return form;
    }

    private void addFormRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints left = new GridBagConstraints();
        left.gridx = 0; left.gridy = row; left.weightx = 0.38;
        left.fill = GridBagConstraints.HORIZONTAL; left.anchor = GridBagConstraints.WEST;
        left.insets = new Insets(8, 6, 8, 15);
        JLabel text = new JLabel(label);
        text.setFont(mediumFont); text.setForeground(textColor);
        panel.add(text, left);

        GridBagConstraints right = new GridBagConstraints();
        right.gridx = 1; right.gridy = row; right.weightx = 0.62;
        right.fill = GridBagConstraints.HORIZONTAL; right.anchor = GridBagConstraints.CENTER;
        right.insets = new Insets(8, 0, 8, 6);
        styleField(field);
        panel.add(field, right);
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(22);
        styleField(field);
        return field;
    }

    private void styleField(JComponent field) {
        field.setFont(regularFont);
        field.setForeground(textColor);
        field.setBackground(Color.WHITE);
        field.setOpaque(true);
        if (field instanceof JTextField) {
            ((JTextField) field).setCaretColor(oliveDark);
            field.setBorder(new CompoundBorder(new LineBorder(new Color(190, 205, 189), 1), new EmptyBorder(8, 10, 8, 10)));
        } else if (field instanceof JComboBox) {
            field.setBorder(new LineBorder(new Color(190, 205, 189), 1));
            field.setFocusable(true);
        }
    }

    private void styleCheckBox(JCheckBox box) {
        box.setFont(regularFont);
        box.setForeground(textColor);
        box.setBackground(Color.WHITE);
        box.setFocusPainted(false);
        box.setOpaque(true);
    }

    private boolean isBlank(JTextField... fields) {
        for (JTextField field : fields) if (field.getText().trim().isEmpty()) return true;
        return false;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "AssetFlow", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDatabaseError(SQLException e) {
        JOptionPane.showMessageDialog(this, "A database operation failed.\n\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class ChoiceItem {
        private final int id;
        private final String label;
        private ChoiceItem(int id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }
}
