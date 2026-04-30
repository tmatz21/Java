package utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIFactory {

    private UIFactory() {
    }

    public static JPanel createAppPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(UITheme.BACKGROUND);
        return panel;
    }

    public static JPanel createSurfacePanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(UITheme.CARD);
        panel.setBorder(createCardBorder());
        return panel;
    }

    public static JPanel createMutedPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(UITheme.SURFACE_MUTED);
        panel.setBorder(createSoftBorder(18, 18, 18, 18));
        return panel;
    }

    public static JLabel createDisplayLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.DISPLAY_FONT);
        label.setForeground(UITheme.TEXT_PRIMARY);
        return label;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.TITLE_FONT);
        label.setForeground(UITheme.TEXT_PRIMARY);
        return label;
    }

    public static JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.SECTION_FONT);
        label.setForeground(UITheme.TEXT_PRIMARY);
        return label;
    }

    public static JLabel createBodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.BODY_FONT);
        label.setForeground(UITheme.TEXT_SECONDARY);
        return label;
    }

    public static JTextArea createWrappedBodyText(String text) {
        return createWrappedText(text, UITheme.BODY_FONT, UITheme.TEXT_SECONDARY);
    }

    public static JTextArea createWrappedTitleText(String text) {
        return createWrappedText(text, UITheme.TITLE_FONT, UITheme.TEXT_PRIMARY);
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, UITheme.PRIMARY_DARK, UITheme.TEXT_LIGHT, UITheme.PRIMARY_DARK);
    }

    public static JButton createSecondaryButton(String text) {
        return createButton(text, UITheme.BUTTON_SECONDARY, UITheme.TEXT_LIGHT, UITheme.BUTTON_SECONDARY);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, UITheme.BUTTON_DANGER, UITheme.TEXT_LIGHT, UITheme.BUTTON_DANGER);
    }

    public static JButton createSidebarButton(String text) {
        JButton button = createButton(text, UITheme.SIDEBAR, UITheme.TEXT_LIGHT, UITheme.SIDEBAR);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.SIDEBAR, 1, true),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));
        button.setFocusPainted(false);
        button.putClientProperty("active", Boolean.FALSE);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!Boolean.TRUE.equals(button.getClientProperty("active"))) {
                    button.setBackground(UITheme.SIDEBAR_HOVER);
                    button.putClientProperty("buttonBorderColor", UITheme.SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!Boolean.TRUE.equals(button.getClientProperty("active"))) {
                    button.setBackground(UITheme.SIDEBAR);
                    button.putClientProperty("buttonBorderColor", UITheme.SIDEBAR);
                }
            }
        });
        return button;
    }

    public static void setSidebarButtonActive(JButton button, boolean active) {
        button.putClientProperty("active", active);
        button.setBackground(active ? UITheme.SIDEBAR_ACTIVE : UITheme.SIDEBAR);
        button.putClientProperty("buttonBorderColor", active ? UITheme.SIDEBAR_ACTIVE : UITheme.SIDEBAR);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(active ? UITheme.SIDEBAR_ACTIVE : UITheme.SIDEBAR, 1, true),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));
    }

    public static JTextField createTextField(String label) {
        JTextField field = new JTextField();
        styleInput(field, label);
        return field;
    }

    public static JPasswordField createPasswordField(String label) {
        JPasswordField field = new JPasswordField();
        styleInput(field, label);
        return field;
    }

    public static JTextArea createTextArea(String label) {
        JTextArea area = new JTextArea(5, 20);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(UITheme.BODY_FONT);
        area.setForeground(UITheme.TEXT_PRIMARY);
        area.setBackground(UITheme.CARD);
        area.setCaretColor(UITheme.TEXT_PRIMARY);
        area.setBorder(createInputBorder(label));
        area.setMargin(new Insets(10, 12, 10, 12));
        return area;
    }

    public static void setBoxFieldSize(JComponent component, int height) {
        component.setMinimumSize(new Dimension(160, height));
        component.setPreferredSize(new Dimension(component.getPreferredSize().width, height));
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    public static void setButtonHeight(AbstractButton button, int height) {
        Dimension preferred = button.getPreferredSize();
        button.setMinimumSize(new Dimension(160, height));
        button.setPreferredSize(new Dimension(Math.max(preferred.width, 160), height));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    public static JScrollPane createScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UITheme.CARD);
        scrollPane.setBackground(UITheme.CARD);
        return scrollPane;
    }

    public static void styleTable(JTable table) {
        table.setFont(UITheme.BODY_FONT);
        table.setForeground(UITheme.TEXT_PRIMARY);
        table.setBackground(UITheme.CARD);
        table.setSelectionBackground(new Color(215, 233, 230));
        table.setSelectionForeground(UITheme.TEXT_PRIMARY);
        table.setGridColor(UITheme.BORDER);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setRowHeight(32);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(UITheme.SURFACE_MUTED);
        header.setForeground(UITheme.TEXT_PRIMARY);
        header.setFont(UITheme.LABEL_FONT);
        header.setReorderingAllowed(false);
    }

    public static JPanel createStatCard(String label, String value, String note, Color accent) {
        JPanel panel = createSurfacePanel(new BorderLayout(0, 18));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel labelView = createBodyLabel(label);
        labelView.setForeground(UITheme.TEXT_SECONDARY);

        JPanel accentDot = new JPanel();
        accentDot.setOpaque(true);
        accentDot.setBackground(accent);
        accentDot.setPreferredSize(new Dimension(14, 14));
        accentDot.setBorder(BorderFactory.createLineBorder(accent, 1, true));

        top.add(labelView, BorderLayout.WEST);
        top.add(accentDot, BorderLayout.EAST);

        JLabel valueView = new JLabel(value);
        valueView.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueView.setForeground(UITheme.TEXT_PRIMARY);

        JLabel noteView = createBodyLabel(note);

        panel.add(top, BorderLayout.NORTH);
        panel.add(valueView, BorderLayout.CENTER);
        panel.add(noteView, BorderLayout.SOUTH);
        return panel;
    }

    public static JPanel createInfoCard(String title, String body) {
        JPanel panel = createMutedPanel(new BorderLayout(0, 8));
        JLabel titleView = createSectionLabel(title);
        JTextArea bodyView = createWrappedBodyText(body);

        panel.add(titleView, BorderLayout.NORTH);
        panel.add(bodyView, BorderLayout.CENTER);
        return panel;
    }

    public static JLabel createPillLabel(String text, Color background, Color foreground) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(background);
        label.setForeground(foreground);
        label.setFont(UITheme.LABEL_FONT);
        label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return label;
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        );
    }

    public static Border createSoftBorder(int top, int left, int bottom, int right) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(top, left, bottom, right)
        );
    }

    private static JButton createButton(String text, Color background, Color foreground, Color borderColor) {
        JButton button = new PaintedButton(text);
        button.setUI(new BasicButtonUI());
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setRolloverEnabled(false);
        button.setMargin(new Insets(10, 16, 10, 16));
        button.putClientProperty("buttonBorderColor", borderColor);
        return button;
    }

    private static void styleInput(JTextComponent input, String label) {
        input.setFont(UITheme.BODY_FONT);
        input.setForeground(UITheme.TEXT_PRIMARY);
        input.setBackground(UITheme.CARD);
        input.setCaretColor(UITheme.TEXT_PRIMARY);
        input.setBorder(createInputBorder(label));
        input.setMargin(new Insets(10, 12, 10, 12));
    }

    private static Border createInputBorder(String label) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                label
        );
        titledBorder.setTitleFont(UITheme.LABEL_FONT);
        titledBorder.setTitleColor(UITheme.TEXT_SECONDARY);
        return titledBorder;
    }

    private static JTextArea createWrappedText(String text, Font font, Color color) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFocusable(false);
        area.setOpaque(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(font);
        area.setForeground(color);
        area.setBorder(BorderFactory.createEmptyBorder());
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        return area;
    }

    private static class PaintedButton extends JButton {
        private static final int ARC = 14;

        private PaintedButton(String text) {
            super(text);
        }

        @Override
        public void updateUI() {
            setUI(new BasicButtonUI());
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill = getBackground();
            if (!isEnabled()) {
                fill = UITheme.BUTTON_DISABLED;
            } else if (getModel().isPressed()) {
                fill = fill.darker();
            }
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);

            Insets insets = getInsets();
            Rectangle viewRect = new Rectangle(
                    insets.left,
                    insets.top,
                    getWidth() - insets.left - insets.right,
                    getHeight() - insets.top - insets.bottom
            );
            Rectangle iconRect = new Rectangle();
            Rectangle textRect = new Rectangle();

            FontMetrics fontMetrics = g2.getFontMetrics(getFont());
            String clippedText = SwingUtilities.layoutCompoundLabel(
                    this,
                    fontMetrics,
                    getText(),
                    getIcon(),
                    getVerticalAlignment(),
                    getHorizontalAlignment(),
                    getVerticalTextPosition(),
                    getHorizontalTextPosition(),
                    viewRect,
                    iconRect,
                    textRect,
                    getIconTextGap()
            );

            if (getIcon() != null) {
                getIcon().paintIcon(this, g2, iconRect.x, iconRect.y);
            }

            if (clippedText != null) {
                g2.setFont(getFont());
                g2.setColor(isEnabled() ? getForeground() : UITheme.TEXT_DISABLED_LIGHT);
                BasicGraphicsUtils.drawStringUnderlineCharAt(
                        g2,
                        clippedText,
                        getDisplayedMnemonicIndex(),
                        textRect.x,
                        textRect.y + fontMetrics.getAscent()
                );
            }

            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Object value = getClientProperty("buttonBorderColor");
            Color borderColor = value instanceof Color ? (Color) value : getBackground();
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
            g2.dispose();
        }
    }
}
