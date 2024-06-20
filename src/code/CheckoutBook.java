package code;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class CheckoutBook extends JFrame {
    private JPanel panel;
    private JLabel lbPatron;
    private JComboBox<String> selectPatron;
    private JLabel lbBook;
    private JComboBox<String> selectBook;
    private JLabel lbCheckoutDate;
    private JLabel checkoutDate;
    private JLabel lbDueDate;
    private JFormattedTextField dueDate;
    private JButton checkoutButton;
    private Connection connection=null;
    private Statement statement=null;
    public CheckoutBook() throws SQLException, ParseException {
        connectDB();
        setTitle("Checkout book");

        setSize(400, 400);
        setLocationRelativeTo(null);
        //installing components

        lbPatron= new JLabel("Select a patron");
        String [] patrons = patronList();
        selectPatron= new JComboBox<>(patrons);
        lbBook= new JLabel("Select book ");
        String [] books = bookList();
        selectBook= new JComboBox<>(books);

        MaskFormatter maskFormatter = new MaskFormatter("##/##/####");
        maskFormatter.setAllowsInvalid(false);
        lbDueDate= new JLabel("Enter due date: ");
        dueDate = new JFormattedTextField(maskFormatter);
        checkoutButton= new JButton("Check-out");
        checkoutButton.setPreferredSize(new Dimension(60, 40));
        checkoutButton.setLayout(new FlowLayout());

        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmTransaction();
            }
        });

        lbCheckoutDate= new JLabel("Check-out date:");
        checkoutDate= new JLabel();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);
        checkoutDate.setText(formattedDate);
        //title of window
        JLabel windowTitle= new JLabel("CHECK-OUT BOOK");
        windowTitle.setBackground(Color.WHITE);
        windowTitle.setOpaque(true);
        add(windowTitle,BorderLayout.NORTH);

        //adding to panel
        panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(lbPatron);
        panel.add(selectPatron);
        panel.add(lbBook);
        panel.add(selectBook);
        panel.add(lbCheckoutDate);
        panel.add(checkoutDate);
        panel.add(lbDueDate);
        panel.add(dueDate);
        add(checkoutButton,BorderLayout.SOUTH);
        add(panel,BorderLayout.CENTER);

        setVisible(true);
    }
    private String[] patronList() throws SQLException {
        ArrayList<String> list = new ArrayList<>();
        ResultSet rs = statement.executeQuery("SELECT * FROM patron ");
        while (rs.next()) {
            list.add(rs.getString("name"));
        }
        return list.toArray(new String[0]);
    }

    private String[] bookList() throws SQLException {
        ArrayList<String> list= new ArrayList<>();
        ResultSet rs = statement.executeQuery("SELECT*FROM book");
        while (rs.next()){
            list.add(rs.getString("title"));
        }
        return list.toArray(new String[0]);
    }
    public void connectDB(){
        try{
            connection= DriverManager.getConnection("jdbc:sqlite:library.db");
            statement= connection.createStatement();
            System.out.println("Connected to database");
        } catch (SQLException e) {
            shutDown();
            throw new RuntimeException(e);
        }
    }
    private void shutDown() {
        if (connection!=null){
            try{
                connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private int getBookIDByTitle(String title) throws SQLException {
        String sql = "SELECT id FROM book WHERE title = ?";
        int bookID=0;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, title);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    bookID = resultSet.getInt("id");
                }
            }
        }
        return bookID;
    }

    private int getPatronIDByName(String name) throws SQLException {
        String sql = "SELECT id FROM patron WHERE name = ?";
        int patronID=0;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    patronID = resultSet.getInt("id");
                }
            }
        }
        return patronID;
    }

    /**
     * @overview reduce 1 number of copy of a book (when user checkout)
     * @param bookTitle
     */
    public void decrementNumCopiesAvailable(String bookTitle) {
        String sql = "UPDATE 'book' SET numCopiesAvailable = numCopiesAvailable - 1 WHERE title = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:library.db");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, bookTitle);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Updated successfully");
            } else {
                System.out.println("Book not found or numCopiesAvailable is already low");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertTransaction() throws SQLException
    {
        int book_id=  getBookIDByTitle(Objects.requireNonNull(selectBook.getSelectedItem()).toString());
        int patron_id= getPatronIDByName(Objects.requireNonNull(selectPatron.getSelectedItem()).toString());
        String bookTitle= selectBook.getSelectedItem().toString();

        System.out.println(book_id);
        System.out.println(patron_id);
        try{
            statement.execute("INSERT INTO 'transaction' (book_id, patron_id, checkoutDate ,dueDate) VALUES ('" + book_id+ "', '"+patron_id+"','"+ checkoutDate.getText()
            +"','" + dueDate.getText()+ "')");
            decrementNumCopiesAvailable(bookTitle);
            JOptionPane.showMessageDialog(this, "SUCCESS ! ", "ADMIN",JOptionPane.INFORMATION_MESSAGE );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private JFrame confirmTransaction()
    {
        JFrame miniWindow = new JFrame();
        miniWindow.setLayout(new BorderLayout());
        JPanel miniPanel = new JPanel(new GridLayout(5, 2, 11, 11));
        JPanel buttonPanel = new JPanel();
        JLabel leftLabel1 = new JLabel("Patron: ");
        JLabel leftLabel2 = new JLabel("Book: ");
        JLabel leftLabel3 = new JLabel("Check-out date: ");
        JLabel leftLabel4 = new JLabel("Due date:");
        JButton confirm = new JButton("OK");


        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    insertTransaction();
                    miniWindow.dispose();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton cancel= new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               miniWindow.dispose();
            }
        });
        JLabel rightLabel1= new JLabel();
        rightLabel1.setText(selectPatron.getSelectedItem().toString());
        JLabel rightLabel2= new JLabel();
        rightLabel2.setText(selectBook.getSelectedItem().toString());
        JLabel rightLabel3= new JLabel();
        rightLabel3.setText(lbCheckoutDate.getText());
        JLabel rightLabel4= new JLabel();
        rightLabel4.setText(dueDate.getText());

        miniPanel.add(leftLabel1);
        miniPanel.add(rightLabel1);
        miniPanel.add(leftLabel2);
        miniPanel.add(rightLabel2);
        miniPanel.add(leftLabel3);
        miniPanel.add(rightLabel3);
        miniPanel.add(leftLabel4);
        miniPanel.add(rightLabel4);

        buttonPanel.add(confirm);
        buttonPanel.add(cancel);
        buttonPanel.setLayout(new FlowLayout());
        miniWindow.add(miniPanel,BorderLayout.CENTER);
        miniWindow.add(buttonPanel,BorderLayout.SOUTH);
        miniWindow.setSize(300,200);


        JLabel titleLabel = new JLabel("CONFIRM TRANSACTION ");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        miniWindow.add(titleLabel, BorderLayout.NORTH);
        miniWindow.setLocationRelativeTo(null);

        miniWindow.setVisible(true);
        return miniWindow;
    }
}
