package com.etms.util;

import com.etms.theme.ThemeManager;
import javax.swing.*;
import java.awt.*;

/**
 * Creates custom vector icons for the ETMS UI.
 * All icons are drawn using Java2D and adapt to the current theme colour.
 * Professional icons – no emoji, no Unicode symbols.
 */
public final class IconHelper {

    private IconHelper() {}

    // ===== NAVIGATION ICONS =====

    public static Icon getDashboardIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // House
                g2.fillPolygon(new int[]{2, 10, 18}, new int[]{8, 0, 8}, 3);
                g2.fillRect(3, 8, 14, 10);
                g2.drawLine(8, 8, 8, 13);
                g2.drawLine(12, 8, 12, 13);
                g2.dispose();
            }
        };
    }

    public static Icon getAnalyticsIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Bar chart
                g2.drawLine(2, 17, 18, 17);
                g2.fillRect(3, 12, 3, 5);
                g2.fillRect(8, 7, 3, 10);
                g2.fillRect(13, 2, 3, 15);
                g2.dispose();
            }
        };
    }

    public static Icon getTournamentsIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Trophy
                g2.fillRect(7, 2, 6, 6);
                g2.fillOval(3, 8, 14, 4);
                g2.fillRect(9, 12, 2, 4);
                g2.dispose();
            }
        };
    }

    public static Icon getTeamsIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Two people
                g2.drawOval(2, 1, 5, 5);
                g2.drawLine(4, 6, 4, 13);
                g2.drawLine(4, 8, 0, 11);
                g2.drawLine(4, 8, 8, 11);
                g2.drawLine(4, 13, 2, 17);
                g2.drawLine(4, 13, 6, 17);
                g2.drawOval(13, 1, 5, 5);
                g2.drawLine(15, 6, 15, 13);
                g2.drawLine(15, 8, 11, 11);
                g2.drawLine(15, 8, 19, 11);
                g2.drawLine(15, 13, 13, 17);
                g2.drawLine(15, 13, 17, 17);
                g2.dispose();
            }
        };
    }

    public static Icon getPlayersIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Single person
                g2.drawOval(7, 1, 6, 6);
                g2.drawLine(10, 7, 10, 15);
                g2.drawLine(10, 9, 5, 12);
                g2.drawLine(10, 9, 15, 12);
                g2.drawLine(10, 15, 7, 19);
                g2.drawLine(10, 15, 13, 19);
                g2.dispose();
            }
        };
    }

    public static Icon getMatchesIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Crossed swords / match
                g2.drawLine(2, 2, 18, 18);
                g2.drawLine(18, 2, 2, 18);
                g2.dispose();
            }
        };
    }

    public static Icon getBracketsIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Bracket
                g2.drawLine(2, 2, 2, 18);
                g2.drawLine(18, 2, 18, 18);
                g2.drawLine(2, 10, 18, 10);
                g2.dispose();
            }
        };
    }

    public static Icon getStandingsIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Rankings / podium
                g2.fillRect(1, 14, 5, 5);
                g2.fillRect(7, 10, 5, 9);
                g2.fillRect(14, 6, 5, 13);
                g2.dispose();
            }
        };
    }

    public static Icon getVenuesIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Pin / location marker
                g2.fillOval(5, 2, 10, 10);
                g2.fillPolygon(new int[]{7, 13, 10}, new int[]{13, 13, 19}, 3);
                g2.dispose();
            }
        };
    }

    public static Icon getSponsorsIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Handshake / deal
                g2.drawArc(2, 4, 8, 10, 0, 180);
                g2.drawArc(10, 4, 8, 10, 0, 180);
                g2.drawLine(6, 9, 14, 9);
                g2.dispose();
            }
        };
    }

    public static Icon getPrizePoolsIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Coin / money
                g2.drawOval(2, 2, 16, 16);
                g2.drawString("$", 7, 14);
                g2.dispose();
            }
        };
    }

    public static Icon getRefereesIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Whistle
                g2.drawOval(2, 6, 12, 8);
                g2.drawLine(14, 10, 18, 10);
                g2.dispose();
            }
        };
    }

    public static Icon getEquipmentIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Wrench
                g2.drawOval(2, 4, 10, 8);
                g2.drawLine(10, 6, 18, 10);
                g2.dispose();
            }
        };
    }

    public static Icon getCoachesIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Clipboard / coaching board
                g2.drawRect(2, 2, 16, 16);
                g2.drawLine(4, 6, 16, 6);
                g2.drawLine(4, 9, 12, 9);
                g2.drawLine(4, 12, 16, 12);
                g2.dispose();
            }
        };
    }

    public static Icon getStaffIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Three staff/people
                g2.drawOval(1, 2, 4, 4);
                g2.drawLine(3, 6, 3, 12);
                g2.drawLine(3, 8, 0, 10);
                g2.drawLine(3, 8, 6, 10);
                g2.drawOval(8, 1, 4, 4);
                g2.drawLine(10, 5, 10, 12);
                g2.drawLine(10, 7, 7, 9);
                g2.drawLine(10, 7, 13, 9);
                g2.drawOval(15, 2, 4, 4);
                g2.drawLine(17, 6, 17, 12);
                g2.drawLine(17, 8, 14, 10);
                g2.drawLine(17, 8, 20, 10);
                g2.dispose();
            }
        };
    }

    public static Icon getReportsIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Document with lines
                g2.drawRect(2, 2, 16, 16);
                g2.drawLine(5, 6, 15, 6);
                g2.drawLine(5, 9, 12, 9);
                g2.drawLine(5, 12, 15, 12);
                g2.dispose();
            }
        };
    }

    public static Icon getAuditIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Magnifying glass
                g2.drawOval(2, 2, 12, 12);
                g2.drawLine(12, 12, 18, 18);
                g2.dispose();
            }
        };
    }

    public static Icon getUsersIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Three users
                g2.drawOval(0, 2, 4, 4);
                g2.drawLine(2, 6, 2, 12);
                g2.drawOval(8, 1, 4, 4);
                g2.drawLine(10, 5, 10, 12);
                g2.drawOval(16, 2, 4, 4);
                g2.drawLine(18, 6, 18, 12);
                g2.dispose();
            }
        };
    }

    public static Icon getSettingsIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Gear
                g2.drawOval(6, 6, 8, 8);
                g2.fillOval(8, 8, 4, 4);
                g2.dispose();
            }
        };
    }

    public static Icon getLogoutIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Door with arrow
                g2.drawRect(2, 2, 14, 16);
                g2.drawLine(10, 6, 16, 6);
                g2.drawLine(14, 4, 16, 6);
                g2.drawLine(14, 8, 16, 6);
                g2.dispose();
            }
        };
    }

    // ===== TOP BAR ICONS =====

    public static Icon getHamburgerIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(2, 4, 18, 4);
                g2.drawLine(2, 10, 18, 10);
                g2.drawLine(2, 16, 18, 16);
                g2.dispose();
            }
        };
    }

    public static Icon getNotificationIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Bell
                g2.drawArc(4, 2, 12, 10, 0, 360);
                g2.drawOval(9, 12, 2, 2);
                g2.drawLine(7, 14, 13, 14);
                g2.dispose();
            }
        };
    }

    public static Icon getSearchIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(2, 2, 12, 12);
                g2.drawLine(12, 12, 18, 18);
                g2.dispose();
            }
        };
    }

    public static Icon getProfileIcon(Color color) {
        return new Icon() {
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 20; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                // Person outline
                g2.drawOval(6, 1, 8, 8);
                g2.drawArc(2, 12, 16, 8, 0, 180);
                g2.dispose();
            }
        };
    }
}