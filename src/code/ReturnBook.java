package code;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author  nguyen minh vu 2101040008
 *
 */
public class ReturnBook  extends JFrame {
    private JPanel panel;
    private JLabel lbPatron;
    private JComboBox<String> selectPatron;
    private JLabel lbBook;
    private JComboBox<String> booksBorrowed;
    private JLabel lbReturnDate;
    private JFormattedTextField returnDate;
    private JButton returnButton;
    private Connection connection;
    private Statement statement;
    public ReturnBook() throws SQLException, ParseException {
        connectDB();
        setTitle("Return book");
        setSize(500,400);
        setLocationRelativeTo(null);
        //installing components
        JLabel windowTitle= new JLabel("RETURN BOOK");
        windowTitle.setBackground(Color.WHITE);
        windowTitle.setOpaque(true);
        add(windowTitle,BorderLayout.NORTH);


        panel = new JPanel(new GridLayout(5, 2, 10, 10));
        lbPatron= new JLabel("Select patron");
        String [] patrons = patronList();

        selectPatron= new JComboBox<>(patrons);
        lbBook= new JLabel("Select book");
        booksBorrowed= new JComboBox<>();
        selectPatron.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (String patron : patrons) {
                    if (Objects.requireNonNull(selectPatron.getSelectedItem()).toString().equals(patron)) {
                        booksBorrowed.removeAllItems();
                        showBookItem(patron);
                        break;
                    }
                }
            }
        });

        MaskFormatter maskFormatter = new MaskFormatter("##/##/####");
        maskFormatter.setAllowsInvalid(false);
        lbReturnDate= new JLabel("Enter return date: ");
        returnDate = new JFormattedTextField(maskFormatter);
        returnButton= new JButton("Return");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String []options= {"Yes", "No"};
                int select= JOptionPane.showOptionDialog(null, "Do you want to return book? :", "ADMIN",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                if (select==0){
                    try {
                        returnBook();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        panel.add(lbPatron);
        panel.add(selectPatron);
        panel.add(lbBook);
        panel.add(booksBorrowed);
        panel.add(lbReturnDate);
        panel.add(returnDate);

        add(returnButton,BorderLayout.SOUTH);
        add(panel,BorderLayout.CENTER);
        setVisible(true);
    }
    private void showBookItem(String patron){
        ArrayList<Integer> bookIds;
        int patronID;
        String getTitleSql = "SELECT title FROM \"book\" WHERE id = ?";
        ArrayList<String> books= new ArrayList<>();
        try {
            patronID=(getPatronIDByName(patron));
            bookIds= getBookIDByPatronID(patronID);
            for (Integer id : bookIds){
                try (PreparedStatement getTitleStmt = connection.prepareStatement(getTitleSql)) {
                    getTitleStmt.setInt(1, id);
                    ResultSet titleResultSet = getTitleStmt.executeQuery();

                    if (titleResultSet.next()) {
                        String title = titleResultSet.getString("title");
                        books.add(title);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        for (String book: books){
            booksBorrowed.addItem(book);
        }
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
    private String[] patronList() throws SQLException {
        ArrayList<String> list = new ArrayList<>();
        ResultSet rs = statement.executeQuery("SELECT * FROM patron ");
        while (rs.next()) {
            list.add(rs.getString("name"));
        }
        return list.toArray(new String[0]);
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
    private ArrayList<Integer>  getBookIDByPatronID(int id) throws SQLException {
        String sql = "SELECT book_id FROM \"transaction\" WHERE patron_id = ?";
        int[] array;
        ArrayList<Integer> bookID= new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
               while (resultSet.next()){
                   bookID.add(resultSet.getInt("book_id"));
               }
            }
        }
        return bookID;
    }
    public void returnBook() throws SQLException {
        //delete the transaction from the table
        String sql= "DELETE from \"transaction\" WHERE id= ? ";
        int patronID= getPatronIDByName(Objects.requireNonNull(selectPatron.getSelectedItem()).toString());
        int bookID= getBookIDByTitle(booksBorrowed.getSelectedItem().toString());
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, getTransID(bookID,patronID));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //update the numCOpiesAvailable
        incrementNumCopiesAvailable(booksBorrowed.getSelectedItem().toString());
        JOptionPane.showMessageDialog(this, "Return success!", "ADMIN",JOptionPane.INFORMATION_MESSAGE );
    }
    public int getTransID(int bookID, int patronID){
        int id = 0;
        String sql = "SELECT id FROM \"transaction\" WHERE book_id = ? AND patron_id = ? LIMIT 1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1,bookID);
            preparedStatement.setInt(2,patronID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    id = resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }
    public void incrementNumCopiesAvailable(String bookTitle) {
        String sql = "UPDATE 'book' SET numCopiesAvailable = numCopiesAvailable + 1 WHERE title = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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

}
