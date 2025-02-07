package chat.app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.net.*;
import java.io.*;
import java.sql.*;


public class Client implements ActionListener {

    JTextField text;
    static JPanel a1;
    static Box vertical = Box.createVerticalBox();
    static JScrollPane scrollPane;
    static DataOutputStream dout;
    static JFrame f = new JFrame();
    static Connection con;
    Client(){

        f.setLayout(null);

        JPanel p1 = new JPanel();
        p1.setBackground(new Color(7,94,84));
        p1.setBounds(0,0,450,70);
        p1.setLayout(null);
        f.add(p1);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/3.png"));
        Image i2 = i1.getImage().getScaledInstance(25,25,Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel back = new JLabel(i3);
        back.setBounds(5,20,25,25);
        p1.add(back);

        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ae){
                System.exit(0);
            }
        });

        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("icons/blank.jpg"));
        Image i5 = i4.getImage().getScaledInstance(50,50,Image.SCALE_DEFAULT);
        ImageIcon i6 = new ImageIcon(i5);
        JLabel profile = new JLabel(i6);
        profile.setBounds(40,10,50,50);
        p1.add(profile);

        ImageIcon i7 = new ImageIcon(ClassLoader.getSystemResource("icons/video.png"));
        Image i8 = i7.getImage().getScaledInstance(30,30,Image.SCALE_DEFAULT);
        ImageIcon i9 = new ImageIcon(i8);
        JLabel video = new JLabel(i9);
        video.setBounds(300,20,30,30);
        p1.add(video);

        ImageIcon i10 = new ImageIcon(ClassLoader.getSystemResource("icons/phone.png"));
        Image i11 = i10.getImage().getScaledInstance(35,30,Image.SCALE_DEFAULT);
        ImageIcon i12 = new ImageIcon(i11);
        JLabel phone = new JLabel(i12);
        phone.setBounds(360,20,35,30);
        p1.add(phone);

        ImageIcon i13 = new ImageIcon(ClassLoader.getSystemResource("icons/3icon.png"));
        Image i14 = i13.getImage().getScaledInstance(10,25,Image.SCALE_DEFAULT);
        ImageIcon i15 = new ImageIcon(i14);
        JLabel morevert = new JLabel(i15);
        morevert.setBounds(415,20,10,25);
        p1.add(morevert);

        JLabel name = new JLabel("Client");
        name.setBounds(110,15,100,18);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN_SERIF",Font.BOLD,18));
        p1.add(name);

        JLabel status = new JLabel("Active Now");
        status.setBounds(110,35,100,18);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SAN_SERIF",Font.BOLD,14));
        p1.add(status);

        a1 = new JPanel();
        a1.setLayout(new BoxLayout(a1, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(a1);
        scrollPane.setBounds(5, 75, 440, 570);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        f.add(scrollPane);

        text = new JTextField();
        text.setBounds(5,655,310,40);
        text.setFont(new Font("SAN_SERIF",Font.PLAIN,16));
        f.add(text);

        JButton send = new JButton("Send");
        send.setBounds(320,655,123,40);
        send.setBackground(new Color(7,94,84));
        send.setForeground(Color.WHITE);
        send.setFont(new Font("SAN_SERIF",Font.BOLD,16));
        send.addActionListener(this);
        f.add(send);

        f.setSize(450,700);
        f.setLocation(750,50);
        f.setUndecorated(true);
        f.getContentPane().setBackground(Color.WHITE);

        f.setVisible(true);
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ChatApp", "root", "vaibhav");
            loadPreviousChats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveChatToDatabase(String sender, String message) {
        try {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO messages (sender, message) VALUES (?, ?)");
            pstmt.setString(1, sender);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPreviousChats() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT sender, message, timestamp FROM messages ORDER BY timestamp");

            while (rs.next()) {
                String sender = rs.getString("sender");
                String message = rs.getString("message");
                Timestamp timest = rs.getTimestamp("timestamp");


                JPanel panel = formatLabel2(message);

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String ftime = sdf.format(timest);
                JLabel time = new JLabel(ftime);

                panel.add(time);


                if (sender.equals("Client")) {
                    JPanel leftPanel = new JPanel(new BorderLayout());
                    leftPanel.add(panel, BorderLayout.LINE_START);
                    vertical.add(leftPanel);
                } else {
                    JPanel rightPanel = new JPanel(new BorderLayout());
                    rightPanel.add(panel, BorderLayout.LINE_END);
                    vertical.add(rightPanel);
                }

                vertical.add(Box.createVerticalStrut(15));  // Add space between messages
            }

            // After adding all messages, update the scroll panel and the UI
            a1.add(vertical);
            a1.revalidate();  // Ensure the layout is updated
            a1.repaint();     // Repaint the panel to reflect the changes
            f.revalidate();   // Revalidate the frame to apply the changes
            f.repaint();      // Repaint the frame to display the messages

            // Ensure the scroll bar is at the bottom
            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JPanel formatLabel2(String out){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        JLabel output = new JLabel("<html><p style=\"width: 150px\">"+ out +"</p></html>");
        output.setFont(new Font("SAN_SERIF",Font.PLAIN,16));
        output.setBackground(new Color(37,211,102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15,15,15,50));
        panel.add(output);

        return panel;
    }


    public void actionPerformed(ActionEvent ae){
        try {
            String out = text.getText();

            JPanel p2 = formatLabel(out);

            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));

            a1.add(vertical,BorderLayout.PAGE_END);
            dout.writeUTF(out);
            text.setText("");

            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            });

            f.repaint();
            f.invalidate();
            f.validate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static JPanel formatLabel(String out){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        JLabel output = new JLabel("<html><p style=\"width: 150px\">"+ out +"</p></html>");
        output.setFont(new Font("SAN_SERIF",Font.PLAIN,16));
        output.setBackground(new Color(37,211,102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15,15,15,50));
        panel.add(output);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        JLabel time = new JLabel();
        time.setText(sdf.format(cal.getTime()));
        panel.add(time);

        return panel;
    }

    public static void main(String[] args){
        new Client();
        try{
            Socket s = new Socket("127.0.0.1",8001);
            DataInputStream din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());
            while(true){
                String msg = din.readUTF();
                saveChatToDatabase("Client", msg);
                JPanel panel = formatLabel(msg);
                JPanel left = new JPanel(new BorderLayout());
                left.add(panel,BorderLayout.LINE_START);
                vertical.add(left);
                a1.add(vertical,BorderLayout.PAGE_END);
                SwingUtilities.invokeLater(() -> {
                    JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                    verticalScrollBar.setValue(verticalScrollBar.getMaximum());
                });
                f.validate();

            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
