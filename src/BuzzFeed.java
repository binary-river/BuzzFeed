import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BuzzFeed extends JFrame {

    private JLabel clockLabel;
    private JTextField inputTime;
    private JLabel inputTimeLabel;
    private JButton startButton;
    private JButton stopButton;
    private Timer timer;
    private DecimalFormat df;

    public BuzzFeed() {
        setTitle("BuzzFeed");
        setSize(240, 160);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        df = new DecimalFormat("00");

        Container backContainer = getContentPane();
        backContainer.setLayout(new BorderLayout());


        clockLabel = new JLabel("00:00 left");
        clockLabel.setFont(new Font(clockLabel.getFont().getName(), clockLabel.getFont().getStyle(), 20));
        inputTime = new JTextField(5);
        inputTimeLabel = new JLabel("min");
        startButton = new JButton("START");
        stopButton = new JButton("STOP");

        startButton.addActionListener(new OnClickStart());
        stopButton.addActionListener(new onClickStop());

        JPanel mainPanel = new JPanel(new GridLayout(0, 1));


        JPanel panel1 = new JPanel();
        panel1.add(clockLabel);

        JPanel panel2 = new JPanel();
        panel2.add(inputTime);
        panel2.add(inputTimeLabel);

        JPanel panel3 = new JPanel();
        panel3.add(startButton);
        panel3.add(stopButton);

        mainPanel.add(panel1);
        mainPanel.add(panel2);
        mainPanel.add(panel3);

        backContainer.add(mainPanel, BorderLayout.CENTER);

        JPanel topMargin = new JPanel();
        topMargin.setPreferredSize(new Dimension(10,10));
        JPanel bottomMargin = new JPanel();
        bottomMargin.setPreferredSize(new Dimension(10,10));
        backContainer.add(topMargin, BorderLayout.NORTH);
        backContainer.add(bottomMargin, BorderLayout.SOUTH);
    }

    private void lockInput() {
        inputTime.setEditable(false);
    }

    private void unlockInput() {
        inputTime.setEditable(true);
    }

    private void setClock(long mills) {
        long min = mills / 1000 / 60;
        long sec = (mills / 1000) % 60;
        clockLabel.setText(df.format(min) + ":" + df.format(sec) + ( mills <= 0 ? " left" : " lefts"));
    }

    private boolean isTimerRunning() {
        return (timer != null && timer.isRunning());
    }

    private void startTimer(long mills) {
        timer = new Timer(1000, new StartTimerListener(mills));
        lockInput();
        timer.start();
    }

    private void stopTimer(){
        timer.stop();
        setClock(0);
        unlockInput();
    }

    private void buzz() {
        int originalX = (int)getLocation().getX();
        int originalY = (int)getLocation().getY();
        requestFocus();
        toFront();
        try{
            for (int i = 0; i < 30; i++) {
                Thread.sleep(10);
                setLocation(originalX+5, originalY+5);
                Thread.sleep(10);
                setLocation(originalX-5, originalY-5);
            }
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, "오류 발생");
        }
    }

    private class StartTimerListener implements ActionListener {
        private long mills;

        public StartTimerListener(long mills){
            this.mills = mills;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mills -= 1000;
            if( mills < 0 ) {
                stopTimer();
                buzz();
                String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss"));
                JOptionPane.showMessageDialog(null, "타이머가 완료되었습니다! (" + currentTime + ")");
                return ;
            }
            setClock(mills);
        }
    }

    private class OnClickStart implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if( isTimerRunning() ) {
                JOptionPane.showMessageDialog(null, "타이머가 진행 중입니다.");
                return ;
            }

            try {
                long mills = Integer.parseInt(inputTime.getText()) * 60 * 1000;
                startTimer(mills);
            } catch (NumberFormatException error) {
                JOptionPane.showMessageDialog(null, "숫자만 입력 가능합니다.");
            }
        }
    }

    private class onClickStop implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if( !isTimerRunning() ) {
                JOptionPane.showMessageDialog(null, "타이머가 실행 중이 아닙니다.");
                return ;
            }

            stopTimer();
        }
    }

}