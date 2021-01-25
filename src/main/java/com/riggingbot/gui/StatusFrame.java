package com.riggingbot.gui;

import com.riggingbot.RiggingBot;
import com.riggingbot.InitThread;

import javax.swing.*;

/**
 * Created by Jacob on 7/20/2015.
 */
public class StatusFrame extends JFrame
{
    private JPanel contentPanel;
    private JTextArea statusTextArea;
    private JScrollPane statusScrollPane;
    private JSpinner depthSpinner;
    private JButton quitButton;
    private JLabel statusLabel;
    private JCheckBox autoCheckBox;
    private JCheckBox overlayCheckBox;
    private JButton startStopButton;
    private RiggingBot riggingBot;
    private InitThread initThread;

    public StatusFrame(RiggingBot riggingBot)
    {
        super("Big Rigs");
        this.riggingBot = riggingBot;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.quitButton.addActionListener(e -> dispose());

        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(4, 1, 100, 1);
        this.depthSpinner.setModel(spinnerNumberModel);

        this.autoCheckBox.addActionListener(e -> {
            if (autoCheckBox.isSelected())
            {
                overlayCheckBox.setEnabled(true);
            } else
            {
                overlayCheckBox.setSelected(true);
                overlayCheckBox.setEnabled(false);
            }
        });

        this.startStopButton.addActionListener(e -> {
            if(riggingBot.isRunning())
            {
                if(initThread.isAlive())
                {
                    riggingBot.getStatus().log("Killing init thread");
                    initThread.stop(); //this is bad... needs to be fixed... someday
                }
                riggingBot.stop();
                riggingBot.getStatus().log("Stopped");
                startStopButton.setText("Start");
            }
            else
            {
                startStopButton.setText("Stop");
                initThread = new InitThread(riggingBot, (Integer) depthSpinner.getValue(), autoCheckBox.isSelected(), overlayCheckBox.isSelected());
                initThread.start();
            }
        });

        this.statusTextArea.setFont(new JLabel().getFont());

        this.setContentPane(contentPanel);
        this.pack();
    }

    public void addMessage(String formattedMessage)
    {
        if(statusScrollPane.getVerticalScrollBar().getValue() == statusScrollPane.getVerticalScrollBar().getMaximum() || true) //TODO: fix auto scroll
        {
            //Scroll bar at bottom
            statusTextArea.append(formattedMessage);
            statusTextArea.select(statusTextArea.getText().length(), statusTextArea.getText().length());
        }
        else
        {
            //User has moved scroll bar up; don't readjust height
            statusTextArea.append(formattedMessage);
        }
    }

    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
    }

    public void setStatus(String status)
    {
        this.statusLabel.setText("Status: " + status);
    }
}
