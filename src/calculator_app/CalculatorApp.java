package calculator_app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Simple GUI Calculator using Java Swing.
 * Supports: +, -, ×, ÷, decimal numbers, clear, backspace and equals.
 */
public class CalculatorApp extends JFrame {
    private final JTextField display = new JTextField("0");
    private double left = 0.0;
    private String currentOp = null;
    private boolean startNew = true; // whether next digit press starts new number

    public CalculatorApp() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(320, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(6,6));

        display.setFont(new Font("SansSerif", Font.PLAIN, 28));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(display, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(5, 4, 6, 6));
        String[] btnOrder = {
            "CE", "C", "⌫", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "±", "0", ".", "="
        };

        for (String key : btnOrder) {
            JButton b = new JButton(key);
            b.setFont(new Font("SansSerif", Font.BOLD, 20));
            b.addActionListener(new ButtonHandler());
            buttons.add(b);
        }

        add(buttons, BorderLayout.CENTER);

        // small padding around main panel
        ((JComponent)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }

    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();

            if ("0123456789".contains(cmd)) {
                inputDigit(cmd);
            } else if (cmd.equals(".")) {
                inputDecimalPoint();
            } else if (cmd.equals("C")) {
                clearAll();
            } else if (cmd.equals("CE")) {
                clearEntry();
            } else if (cmd.equals("⌫")) {
                backspace();
            } else if (cmd.equals("±")) {
                toggleSign();
            } else if ("+-*/".contains(cmd)) {
                applyOperator(cmd);
            } else if (cmd.equals("=")) {
                calculateResult();
            }
        }
    }

    private void inputDigit(String d) {
        if (startNew || display.getText().equals("0")) {
            display.setText(d);
            startNew = false;
        } else {
            display.setText(display.getText() + d);
        }
    }

    private void inputDecimalPoint() {
        if (startNew) {
            display.setText("0.");
            startNew = false;
            return;
        }
        if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
        }
    }

    private void clearAll() {
        display.setText("0");
        left = 0.0;
        currentOp = null;
        startNew = true;
    }

    private void clearEntry() {
        display.setText("0");
        startNew = true;
    }

    private void backspace() {
        if (startNew) return;
        String s = display.getText();
        if (s.length() <= 1) {
            display.setText("0");
            startNew = true;
        } else {
            display.setText(s.substring(0, s.length() - 1));
        }
    }

    private void toggleSign() {
        String s = display.getText();
        if (s.equals("0") || s.equals("0.0")) return;
        if (s.startsWith("-")) display.setText(s.substring(1));
        else display.setText("-" + s);
    }

    private void applyOperator(String op) {
        try {
            double current = Double.parseDouble(display.getText());
            if (currentOp != null && !startNew) {
                // perform previous pending operation first (left currentOp current)
                left = compute(left, currentOp, current);
                display.setText(trimDouble(left));
            } else {
                left = current;
            }
            currentOp = op;
            startNew = true;
        } catch (NumberFormatException ex) {
            display.setText("0");
            startNew = true;
        }
    }

    private void calculateResult() {
        if (currentOp == null) return;
        try {
            double right = Double.parseDouble(display.getText());
            left = compute(left, currentOp, right);
            display.setText(trimDouble(left));
            currentOp = null;
            startNew = true;
        } catch (NumberFormatException ex) {
            display.setText("Error");
            startNew = true;
        } catch (ArithmeticException ex) {
            display.setText("Error");
            startNew = true;
        }
    }

    private double compute(double a, String op, double b) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0.0) throw new ArithmeticException("Divide by zero");
                return a / b;
            default: return b;
        }
    }

    private String trimDouble(double v) {
        if (v == (long) v) return String.format("%d", (long) v);
        return String.format("%s", v);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorApp calc = new CalculatorApp();
            calc.setVisible(true);
        });
    }
}
