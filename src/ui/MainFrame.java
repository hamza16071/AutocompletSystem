package ui;

import services.AutocompleteService;
import services.WebSearchService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class MainFrame extends JFrame {

    // ══════════════════════════════════════════════════════════════════
    // THEME COLOURS — Light
    // ══════════════════════════════════════════════════════════════════
    private static final Color L_BG = Color.WHITE;
    private static final Color L_BORDER_IDLE = new Color(218, 220, 224);
    private static final Color L_BORDER_FOCUS = new Color(26, 115, 232);
    private static final Color L_HOVER_ROW = new Color(241, 243, 244);
    private static final Color L_DIVIDER = new Color(232, 234, 237);
    private static final Color L_TEXT = new Color(32, 33, 36);
    private static final Color L_SUBTEXT = new Color(95, 99, 104);
    private static final Color L_ICON = new Color(154, 160, 166);
    private static final Color L_BTN_BG = new Color(248, 249, 250);
    private static final Color L_BTN_BORDER = new Color(218, 220, 224);
    private static final Color L_BTN_HOVER = new Color(232, 234, 237);
    private static final Color L_STATUS_BG = new Color(248, 249, 250);
    private static final Color L_POPUP_BG = Color.WHITE;

    // ══════════════════════════════════════════════════════════════════
    // THEME COLOURS — Dark
    // ══════════════════════════════════════════════════════════════════
    private static final Color D_BG = new Color(18, 18, 18);
    private static final Color D_BORDER_IDLE = new Color(60, 60, 60);
    private static final Color D_BORDER_FOCUS = new Color(138, 180, 248);
    private static final Color D_HOVER_ROW = new Color(40, 40, 40);
    private static final Color D_DIVIDER = new Color(50, 50, 50);
    private static final Color D_TEXT = new Color(232, 234, 237);
    private static final Color D_SUBTEXT = new Color(154, 160, 166);
    private static final Color D_ICON = new Color(120, 125, 130);
    private static final Color D_BTN_BG = new Color(32, 33, 36);
    private static final Color D_BTN_BORDER = new Color(60, 60, 60);
    private static final Color D_BTN_HOVER = new Color(50, 50, 50);
    private static final Color D_STATUS_BG = new Color(25, 25, 25);
    private static final Color D_POPUP_BG = new Color(28, 28, 28);

    // Logo colours (same in both themes)
    private static final Color[] LOGO_COLORS = {
            new Color(66, 133, 244), // D blue
            new Color(234, 67, 53), // i red
            new Color(251, 188, 5), // s yellow
            new Color(52, 168, 83), // h green
            new Color(66, 133, 244), // a blue
    };

    // ══════════════════════════════════════════════════════════════════
    // LIVE THEME (changes on toggle)
    // ══════════════════════════════════════════════════════════════════
    private boolean darkMode = false;

    private Color bg() {
        return darkMode ? D_BG : L_BG;
    }

    private Color borderIdle() {
        return darkMode ? D_BORDER_IDLE : L_BORDER_IDLE;
    }

    private Color borderFocus() {
        return darkMode ? D_BORDER_FOCUS : L_BORDER_FOCUS;
    }

    private Color hoverRow() {
        return darkMode ? D_HOVER_ROW : L_HOVER_ROW;
    }

    private Color divider() {
        return darkMode ? D_DIVIDER : L_DIVIDER;
    }

    private Color text() {
        return darkMode ? D_TEXT : L_TEXT;
    }

    private Color subtext() {
        return darkMode ? D_SUBTEXT : L_SUBTEXT;
    }

    private Color icon() {
        return darkMode ? D_ICON : L_ICON;
    }

    private Color btnBg() {
        return darkMode ? D_BTN_BG : L_BTN_BG;
    }

    private Color btnBorder() {
        return darkMode ? D_BTN_BORDER : L_BTN_BORDER;
    }

    private Color btnHover() {
        return darkMode ? D_BTN_HOVER : L_BTN_HOVER;
    }

    private Color statusBg() {
        return darkMode ? D_STATUS_BG : L_STATUS_BG;
    }

    private Color popupBg() {
        return darkMode ? D_POPUP_BG : L_POPUP_BG;
    }

    // ══════════════════════════════════════════════════════════════════
    // FONTS
    // ══════════════════════════════════════════════════════════════════
    private static final Font F_SEARCH = sf(17, Font.PLAIN);
    private static final Font F_SUGGEST = sf(14, Font.PLAIN);
    private static final Font F_BTN = sf(13, Font.PLAIN);
    private static final Font F_STATUS = sf(11, Font.PLAIN);

    private static Font sf(int size, int style) {
        for (String n : new String[] { "Segoe UI", "Helvetica Neue", "Calibri", "Arial" }) {
            Font f = new Font(n, style, size);
            if (!f.getFamily().equals("Dialog"))
                return f;
        }
        return new Font("SansSerif", style, size);
    }

    // ══════════════════════════════════════════════════════════════════
    // TITLE
    // ══════════════════════════════════════════════════════════════════
    private static final String APP_TITLE = "Autocomplete";
    private static final String FRAME_TITLE = "Autocomplete System";

    // ══════════════════════════════════════════════════════════════════
    // STATE
    // ══════════════════════════════════════════════════════════════════
    private final AutocompleteService svc;

    // UI refs
    private JTextField searchField;
    private JPanel searchWrapper;
    private JPanel rootPanel;
    private JPanel centerWrapper;
    private JPanel logoPanel;
    private JPanel statusBar;
    private JLabel statusLabel;
    private JLabel correctionLabel; // for "Did you mean?"
    private JLabel starBtn;

    // Popup
    private JWindow popup;
    private JList<String> suggList;
    private DefaultListModel<String> suggModel;

    private boolean suppress = false;
    private Timer debounceTimer;

    // ══════════════════════════════════════════════════════════════════
    // CONSTRUCTOR
    // ══════════════════════════════════════════════════════════════════
    public MainFrame(AutocompleteService svc) {
        this.svc = svc;

        debounceTimer = new Timer(120, e -> performSearch());
        debounceTimer.setRepeats(false);

        buildFrame();
        buildUI();
        buildPopup();
        setVisible(true);
        searchField.requestFocusInWindow();
    }

    // ══════════════════════════════════════════════════════════════════
    // FRAME
    // ══════════════════════════════════════════════════════════════════
    private void buildFrame() {
        setTitle(FRAME_TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 640);
        setMinimumSize(new Dimension(650, 440));
        setLocationRelativeTo(null);
    }

    // ══════════════════════════════════════════════════════════════════
    // MAIN UI
    // ══════════════════════════════════════════════════════════════════
    private void buildUI() {
        rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(bg());

        // TOP BAR (star button)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(10, 16, 0, 16));

        starBtn = new JLabel("☆") {
            private boolean over = false;
            {
                setFont(new Font("Segoe UI", Font.PLAIN, 22));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setToolTipText("Dark / Light theme toggle");
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        over = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        over = false;
                        repaint();
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        toggleTheme();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (over) {
                    g2.setColor(darkMode ? new Color(255, 255, 255, 25) : new Color(0, 0, 0, 12));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                }
                g2.setColor(darkMode ? new Color(251, 188, 5) : new Color(95, 99, 104));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(36, 36);
            }
        };
        topBar.add(starBtn, BorderLayout.EAST);
        rootPanel.add(topBar, BorderLayout.NORTH);

        // CENTER
        centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(bg());

        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);

        col.add(Box.createVerticalStrut(10));
        col.add(makeLogoPanel());
        col.add(Box.createVerticalStrut(32));
        col.add(makeSearchBar());

        // Correction label
        correctionLabel = new JLabel(" ");
        correctionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        correctionLabel.setForeground(subtext());
        correctionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        correctionLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        correctionLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String text = correctionLabel.getText();
                if (text.startsWith("Did you mean: ")) {
                    String suggestion = text.substring("Did you mean: ".length());
                    searchField.setText(suggestion);
                    performSearch();
                }
            }
        });
        col.add(correctionLabel);

        col.add(Box.createVerticalStrut(22));
        col.add(makeButtons());

        centerWrapper.add(col);
        rootPanel.add(centerWrapper, BorderLayout.CENTER);

        buildStatusBar();
        setContentPane(rootPanel);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                repositionPopup();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                repositionPopup();
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════
    // LOGO
    // ══════════════════════════════════════════════════════════════════
    private JPanel makeLogoPanel() {
        logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(bg());
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                Font logoFont = new Font("Segoe UI", Font.BOLD, 72);
                g2.setFont(logoFont);
                FontMetrics fm = g2.getFontMetrics();
                String text = APP_TITLE;
                int totalW = fm.stringWidth(text);
                int x = (getWidth() - totalW) / 2;
                int y = 80;
                int ci = 0;
                for (char c : text.toCharArray()) {
                    Color col = (ci < LOGO_COLORS.length) ? LOGO_COLORS[ci] : LOGO_COLORS[0];
                    g2.setColor(col);
                    g2.drawString(String.valueOf(c), x, y);
                    x += fm.charWidth(c);
                    ci++;
                }
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(600, 92);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, 92);
            }
        };
        logoPanel.setOpaque(false);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return logoPanel;
    }

    // ══════════════════════════════════════════════════════════════════
    // SEARCH BAR
    // ══════════════════════════════════════════════════════════════════
    private JPanel makeSearchBar() {
        searchWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth() - 1, h = getHeight() - 1, r = h;
                int alpha = darkMode ? 40 : 16;
                g2.setColor(new Color(0, 0, 0, alpha));
                g2.fillRoundRect(3, 5, w - 3, h - 3, r, r);
                g2.setColor(new Color(0, 0, 0, alpha - 6));
                g2.fillRoundRect(2, 4, w - 2, h - 2, r, r);
                g2.setColor(new Color(0, 0, 0, alpha - 10));
                g2.fillRoundRect(1, 3, w - 1, h - 1, r, r);
                g2.setColor(darkMode ? new Color(32, 33, 36) : Color.WHITE);
                g2.fillRoundRect(0, 0, w, h, r, r);
                boolean focused = searchField != null && searchField.isFocusOwner();
                g2.setColor(focused ? borderFocus() : borderIdle());
                g2.setStroke(new BasicStroke(focused ? 1.8f : 1f));
                g2.drawRoundRect(0, 0, w, h, r, r);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(620, 56);
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        searchWrapper.setOpaque(false);
        searchWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchWrapper.add(makeSearchIcon(), BorderLayout.WEST);

        searchField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(subtext());
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int yy = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString("Kuch bhi likhein — Roman Urdu, English, Mixed...", 2, yy);
                    g2.dispose();
                }
            }
        };
        searchField.setOpaque(false);
        searchField.setBorder(new EmptyBorder(0, 0, 0, 0));
        searchField.setFont(F_SEARCH);
        searchField.setForeground(text());
        searchField.setCaretColor(borderFocus());
        searchField.setBackground(new Color(0, 0, 0, 0));
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                searchWrapper.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                searchWrapper.repaint();
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                triggerDebounce();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                triggerDebounce();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        navigate(+1);
                        e.consume();
                        break;
                    case KeyEvent.VK_UP:
                        navigate(-1);
                        e.consume();
                        break;
                    case KeyEvent.VK_ENTER:
                        onEnter();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        hidePopup();
                        break;
                }
            }
        });
        searchWrapper.add(searchField, BorderLayout.CENTER);
        searchWrapper.add(makeMicIcon(), BorderLayout.EAST);
        return searchWrapper;
    }

    private JPanel makeSearchIcon() {
        return new JPanel() {
            {
                setOpaque(false);
                setPreferredSize(new Dimension(52, 56));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(icon());
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(15, 19, 16, 16);
                g2.drawLine(28, 32, 36, 40);
                g2.dispose();
            }
        };
    }

    private JPanel makeMicIcon() {
        JPanel micPanel = new JPanel() {
            {
                setOpaque(false);
                setPreferredSize(new Dimension(52, 56));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(icon());
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(20, 14, 11, 15, 5, 5);
                g2.drawArc(13, 22, 24, 14, 0, -180);
                g2.drawLine(25, 36, 25, 41);
                g2.drawLine(18, 41, 32, 41);
                g2.dispose();
            }
        };
        micPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        micPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Thread(() -> {
                    SwingUtilities.invokeLater(() -> updateStatus("Listening... Speak now! (Windows Speech)"));
                    try {
                        // Using ProcessBuilder to avoid deprecation warning
                        ProcessBuilder pb = new ProcessBuilder(
                                "powershell",
                                "-Command",
                                "Add-Type -AssemblyName System.Speech; " +
                                        "$recognizer = New-Object System.Speech.Recognition.SpeechRecognitionEngine; " +
                                        "$recognizer.SetInputToDefaultAudioDevice(); " +
                                        "$result = $recognizer.Recognize(); " +
                                        "Write-Host $result.Text");
                        pb.redirectErrorStream(true);
                        Process process = pb.start();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String spoken = reader.readLine();
                        process.waitFor();

                        if (spoken != null && !spoken.trim().isEmpty()) {
                            String finalSpoken = spoken.trim();
                            SwingUtilities.invokeLater(() -> {
                                searchField.setText(finalSpoken);
                                performSearch();
                                updateStatus("Voice: \"" + finalSpoken + "\"");
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> updateStatus("Could not recognize. Try again."));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        SwingUtilities.invokeLater(() -> updateStatus("Error: " + ex.getMessage()));
                    }
                }).start();
            }
        });
        return micPanel;
    }

    // ══════════════════════════════════════════════════════════════════
    // BUTTONS (with History, Web Search, Clear History)
    // ══════════════════════════════════════════════════════════════════
    private JPanel makeButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        // p.add(makeGBtn("Disha Talash"));
        // p.add(makeGBtn("Mujhe Pata Hai"));

        JButton historyBtn = new JButton("History");
        styleButton(historyBtn);
        historyBtn.addActionListener(e -> showHistory());
        p.add(historyBtn);

        JButton webBtn = new JButton("Web Search");
        styleButton(webBtn);
        webBtn.addActionListener(e -> {
            String q = searchField.getText().trim();
            if (!q.isEmpty())
                WebSearchService.searchOnGoogle(q);
            else
                JOptionPane.showMessageDialog(this, "Kuch likhiye pehle!");
        });
        p.add(webBtn);

        JButton clearBtn = new JButton("Clear History");
        styleButton(clearBtn);
        clearBtn.addActionListener(e -> {
            svc.clearHistory();
            updateStatus("History cleared!");
        });
        p.add(clearBtn);

        return p;
    }

    private void styleButton(JButton btn) {
        btn.setFont(F_BTN);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setForeground(icon());
    }

    private JButton makeGBtn(String label) {
        return new JButton(label) {
            private boolean over = false;
            {
                setFocusPainted(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setFont(F_BTN);
                setPreferredSize(new Dimension(160, 38));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        over = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        over = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(over ? btnHover() : btnBg());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.setColor(over ? (darkMode ? new Color(80, 80, 80) : new Color(196, 199, 202)) : btnBorder());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.setColor(text());
                g2.setFont(F_BTN);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
    }

    // ══════════════════════════════════════════════════════════════════
    // STATUS BAR
    // ══════════════════════════════════════════════════════════════════
    private void buildStatusBar() {
        int total = 0;
        try {
            total = svc.getTotalWords();
        } catch (Exception ignored) {
        }
        statusLabel = new JLabel("  " + total + " words loaded  |  Type to search...");
        statusLabel.setFont(F_STATUS);
        statusLabel.setForeground(subtext());
        statusLabel.setBorder(new EmptyBorder(4, 12, 4, 12));
        JLabel ver = new JLabel(FRAME_TITLE + " v1.0  ");
        ver.setFont(F_STATUS);
        ver.setForeground(subtext());
        statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(statusBg());
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, divider()));
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(ver, BorderLayout.EAST);
        rootPanel.add(statusBar, BorderLayout.SOUTH);
    }

    // ══════════════════════════════════════════════════════════════════
    // POPUP
    // ══════════════════════════════════════════════════════════════════
    private void buildPopup() {
        popup = new JWindow(this);
        popup.setBackground(new Color(0, 0, 0, 0));
        suggModel = new DefaultListModel<>();
        suggList = new JList<>(suggModel);
        suggList.setFont(F_SUGGEST);
        suggList.setBackground(popupBg());
        suggList.setForeground(text());
        suggList.setFixedCellHeight(46);
        suggList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggList.setSelectionBackground(hoverRow());
        suggList.setSelectionForeground(text());
        suggList.setCellRenderer(new SuggestionRenderer());
        suggList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = suggList.locationToIndex(e.getPoint());
                if (i >= 0)
                    selectSuggestion(suggModel.get(i));
            }
        });
        suggList.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int i = suggList.locationToIndex(e.getPoint());
                if (i >= 0)
                    suggList.setSelectedIndex(i);
            }
        });
        JPanel wrap = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                for (int s = 5; s >= 0; s--) {
                    g2.setColor(new Color(0, 0, 0, darkMode ? 40 : 6 + s * 2));
                    g2.fillRoundRect(s, s + 2, w - 2 * s, h - 2 * s, 12, 12);
                }
                g2.setColor(popupBg());
                g2.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);
                g2.setColor(divider());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
                g2.dispose();
            }
        };
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(6, 2, 6, 2));
        wrap.add(suggList, BorderLayout.CENTER);
        popup.add(wrap);
    }

    private class SuggestionRenderer implements ListCellRenderer<String> {
        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JPanel row = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    g.setColor(isSelected ? hoverRow() : popupBg());
                    g.fillRect(0, 0, getWidth(), getHeight());
                    if (index > 0) {
                        g.setColor(divider());
                        g.drawLine(46, 0, getWidth() - 10, 0);
                    }
                }
            };
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(0, 4, 0, 8));
            JPanel ico = new JPanel() {
                {
                    setOpaque(false);
                    setPreferredSize(new Dimension(44, 46));
                }

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(icon());
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawOval(12, 15, 13, 13);
                    g2.drawLine(22, 25, 28, 31);
                    g2.dispose();
                }
            };
            JLabel txt = new JLabel(value);
            txt.setFont(F_SUGGEST);
            txt.setForeground(text());
            txt.setBorder(new EmptyBorder(0, 6, 0, 0));
            JLabel arrow = new JLabel("↗");
            arrow.setFont(sf(13, Font.PLAIN));
            arrow.setForeground(icon());
            arrow.setPreferredSize(new Dimension(30, 46));
            arrow.setHorizontalAlignment(SwingConstants.CENTER);
            row.add(ico, BorderLayout.WEST);
            row.add(txt, BorderLayout.CENTER);
            row.add(arrow, BorderLayout.EAST);
            return row;
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // THEME TOGGLE
    // ══════════════════════════════════════════════════════════════════
    private void toggleTheme() {
        darkMode = !darkMode;
        starBtn.setText(darkMode ? "★" : "☆");
        rootPanel.setBackground(bg());
        centerWrapper.setBackground(bg());
        statusBar.setBackground(statusBg());
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, divider()));
        statusLabel.setForeground(subtext());
        suggList.setBackground(popupBg());
        suggList.setForeground(text());
        suggList.setSelectionBackground(hoverRow());
        searchField.setForeground(text());
        searchField.setCaretColor(borderFocus());
        SwingUtilities.updateComponentTreeUI(this);
        searchWrapper.repaint();
        logoPanel.repaint();
        rootPanel.repaint();
        repaint();
    }

    // ══════════════════════════════════════════════════════════════════
    // HISTORY
    // ══════════════════════════════════════════════════════════════════
    private void showHistory() {
        List<String> history = svc.getSearchHistory(15);
        System.out.println("History Size = " + history.size());
        System.out.println(history);

        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No search history yet!");
            return;
        }
        String[] historyArray = history.toArray(new String[0]);
        String selected = (String) JOptionPane.showInputDialog(this,
                "Recent searches (click to search again):",
                "Search History",
                JOptionPane.PLAIN_MESSAGE,
                null,
                historyArray,
                historyArray[0]);
        if (selected != null && !selected.isEmpty()) {
            searchField.setText(selected);
            performSearch();
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // SEARCH LOGIC
    // ══════════════════════════════════════════════════════════════════
    private void triggerDebounce() {
        if (suppress)
            return;
        if (debounceTimer.isRunning())
            debounceTimer.restart();
        else
            debounceTimer.start();
    }

    private void performSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) {
            hidePopup();
            correctionLabel.setText(" ");
            return;
        }

        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() {
                return svc.getSuggestions(q, 8);
            }

            @Override
            protected void done() {
                try {
                    List<String> suggs = get();
                    String correction = svc.getCorrection(q);
                    if (correction != null && !correction.equalsIgnoreCase(q)) {
                        correctionLabel.setText("Did you mean: " + correction);
                        correctionLabel.setForeground(borderFocus());
                    } else {
                        correctionLabel.setText(" ");
                    }

                    if (suggs == null || suggs.isEmpty()) {
                        hidePopup();
                        return;
                    }
                    if (!searchField.getText().trim().equals(q))
                        return;
                    svc.recordSearch(q);
                    suggModel.clear();
                    suggs.forEach(suggModel::addElement);
                    suggList.clearSelection();
                    repositionPopup();
                    popup.setVisible(true);
                    SwingUtilities.invokeLater(() -> searchField.requestFocusInWindow());
                    updateStatus(suggs.size() + " suggestions for \"" + q + "\"");
                } catch (Exception ignored) {
                }
            }
        }.execute();
    }

    private void repositionPopup() {
        if (!searchWrapper.isShowing())
            return;
        try {
            Point loc = searchWrapper.getLocationOnScreen();
            int x = loc.x;
            int y = loc.y + searchWrapper.getHeight() - 6;
            int w = searchWrapper.getWidth();
            int h = Math.min(suggModel.size(), 8) * 46 + 14;
            popup.setBounds(x, y, w, h);
            popup.validate();
        } catch (IllegalComponentStateException ignored) {
        }
    }

    private void hidePopup() {
        popup.setVisible(false);
        suggList.clearSelection();
    }

    private void navigate(int dir) {
        if (!popup.isVisible() || suggModel.isEmpty())
            return;
        int cur = suggList.getSelectedIndex();
        int nxt = Math.max(0, Math.min(suggModel.size() - 1, cur + dir));
        suggList.setSelectedIndex(nxt);
        suppress = true;
        searchField.setText(suggModel.get(nxt));
        searchField.setCaretPosition(searchField.getText().length());
        suppress = false;
    }

    private void onEnter() {
        int i = suggList.getSelectedIndex();
        if (i >= 0 && popup.isVisible()) {
            selectSuggestion(suggModel.get(i));
        } else {
            String q = searchField.getText().trim();
            if (!q.isEmpty()) {
                svc.recordSearch(q);
                hidePopup();
            }
        }
    }

    private void selectSuggestion(String word) {
        suppress = true;
        searchField.setText(word);
        searchField.setCaretPosition(word.length());
        suppress = false;
        hidePopup();
        svc.recordSearch(word);
        updateStatus("Selected: " + word);
        searchField.requestFocusInWindow();
    }

    private void updateStatus(String msg) {
        if (statusLabel == null)
            return;
        int total = 0;
        try {
            total = svc.getTotalWords();
        } catch (Exception ignored) {
        }
        statusLabel.setText("  " + total + " words  |  " + msg);
        statusLabel.setForeground(subtext());
    }
}