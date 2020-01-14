package com.kor.admiralty.ui.util;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.awt.event.KeyEvent.*;

public class JNumberTextField extends JTextField {
    private Set<Integer> controlKeys = new HashSet<>(Arrays.asList(VK_UP, VK_DOWN, VK_LEFT, VK_RIGHT, VK_HOME, VK_END));
    private int maxLength;

    public JNumberTextField(int maxLength) {
        super();
        this.maxLength = maxLength;
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        if (Character.isISOControl(e.getKeyChar())
                || controlKeys.contains(e.getKeyCode())
                || (Character.isDigit(e.getKeyChar())) && this.getText().length() < maxLength) {
            super.processKeyEvent(e);
        }
        e.consume();
    }

    public Integer getInt() {
        String text = getText();
        if (text != null) {
            text = text.trim();
            if (!text.isEmpty()) {
                try {
                    return Integer.valueOf(text);
                } catch (NumberFormatException ignore) {}
            }
        }
        return null;
    }
}
