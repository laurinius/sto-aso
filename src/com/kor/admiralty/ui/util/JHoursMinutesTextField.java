package com.kor.admiralty.ui.util;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.awt.event.KeyEvent.*;

public class JHoursMinutesTextField extends JTextField {
    private static final Set<Integer> controlKeys = new HashSet<>(Arrays.asList(VK_UP, VK_DOWN, VK_LEFT, VK_RIGHT, VK_HOME, VK_END));
    private static final Set<Character> delimiterKeys = new HashSet<>(Arrays.asList(':', ',', '.'));
    private int maxLength;

    public JHoursMinutesTextField(final int maxLength) {
        super();
        this.maxLength = maxLength;
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        if (isValidKey(e)) {
            super.processKeyEvent(e);
        }
        e.consume();
    }

    private boolean isValidKey(final KeyEvent e) {
        if (Character.isISOControl(e.getKeyChar()) || controlKeys.contains(e.getKeyCode())) {
            return true;
        }
        final String text = this.getText();
        if (this.getText().length() < maxLength) {
            if (Character.isDigit(e.getKeyChar())) {
                return true;
            }
            if (delimiterKeys.contains(e.getKeyChar())) {
                for (Character c : text.toCharArray()) {
                    if (delimiterKeys.contains(c)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public Integer getMinutes() {
        String text = getText();
        if (text == null) {
            return null;
        }
        String[] split = text.split("[:,.]", -1);
        if (split.length > 1) {
            Integer hours = getNumber(split[0].trim());
            Integer minutes = getNumber(split[1].trim());
            Integer resultMinutes = null;
            if (hours != null) {
                resultMinutes = hours * 60;
            }
            if (minutes != null) {
                if (resultMinutes != null) {
                    resultMinutes += minutes;
                } else {
                    resultMinutes = minutes;
                }
            }
            return resultMinutes;
        }
        return getNumber(split[0].trim());
    }

    private Integer getNumber(String text) {
        if (text != null && !text.isEmpty()) {
            try {
                return Integer.valueOf(text);
            } catch (NumberFormatException ignore) {}
        }
        return null;
    }
}
