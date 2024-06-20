package code;



import common.PatronType;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * @author  nguyen minh vu 2101040008
 *
 */
public class PatronWindow extends JFrame {
    private JPanel panel;
    private JTextField name;
    private JFormattedTextField dob;
    private JTextField email;
    private JTextField phone;
    private JComboBox<String> patronType;
    private Connection conn = null;
    private Statement statement=null;

    public PatronWindow(String title) throws ParseException {
        connectDB();
        setTitle(title);

        setSize(300, 200);
        setLocationRelativeTo(null);

        JLabel windowTitle= new JLabel("PATRON");
        windowTitle.setBackground(Color.YELLOW);
        windowTitle.setOpaque(true);

        name = new JTextField(15);
        MaskFormatter maskFormatter = new MaskFormatter("##/##/####");
        maskFormatter.setAllowsInvalid(false);
        dob = new JFormattedTextField(maskFormatter);

        email = new JTextField(15);
        phone = new JTextField(15);

        String[] types = {"Regular", "Premium"};
        patronType = new JComboBox<>(types);

        JPanel buttons= new JPanel();
        JButton add = getjButton();
        JButton clear= new JButton("Clear");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField[] inputs= {name,dob,email,phone};
                for (int i=0; i< inputs.length;i++){
                    inputs[i].setText("");
                }
            }
        });

        panel = new JPanel(new GridLayout(5, 2, 10, 10));

        panel.add(new JLabel("Name:"));
        panel.add(name);
        panel.add(new JLabel("Date of Birth:"));
        panel.add(dob);
        panel.add(new JLabel("Email:"));
        panel.add(email);
        panel.add(new JLabel("Phone:"));
        panel.add(phone);
        panel.add(new JLabel("Patron Type:"));
        panel.add(patronType);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(windowTitle,BorderLayout.NORTH);
        add(add,BorderLayout.SOUTH);

        buttons.add(add);
        buttons.add(clear);
        buttons.setLayout(new FlowLayout());
        add(buttons,BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    private void connectDB() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:library.db");
            statement = conn.createStatement();
            System.out.println("Connected to database");
        } catch (SQLException e) {
            shutDown();
            throw new RuntimeException(e);
        }
    }
    private void shutDown(){
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        System.exit(0);
    }
    private JButton getjButton() {
        JButton add = new JButton("Add patron");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String []options= {"Yes", "No"};
                int select= JOptionPane.showOptionDialog(null, "Do you want to add a new Patron ? :", "Add Patron",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                if (select==0){
                    try {
                        addPatron();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }
        });
        return add;
    }

    public void addPatron() throws SQLException {
        PatronType type;
        if (patronType.getSelectedItem().equals("Regular")){
            type = PatronType.REGULAR;
        } else {
            type = PatronType.PREMIUM;
        }

        Patron p = new Patron(name.getText(), new Date(dob.getText()), email.getText(), phone.getText(), type);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateOfBirth = formatter.format(p.getDob());

        try {
            statement.execute("INSERT INTO patron (name, dob, email, phone, patronType) VALUES ('" +
                    name.getText() + "', '" + dateOfBirth + "', '" + p.getEmail() + "', '" +
                    p.getPhoneNumber() + "','" + p.getPatronType().toString() + "')");
            JOptionPane.showMessageDialog(this, "Patron added successfully", "INFORMATION",JOptionPane.INFORMATION_MESSAGE );
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

