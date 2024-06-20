package code;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;


/**
 * @author  nguyen minh vu 2101040008
 * @notice :If you want to refresh 1 table press clear button and press get report again and re-choose  your desired table
 */
public class TransactionReport extends JFrame {
    private JPanel panel;
    private JComboBox<String> options;
    private JButton getReportButton;
    private JTable defaultTable;
    private JButton clear;
    private Connection connection;
    private Statement statement;

    private void connectDB() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:sqlite:library.db");
            statement = connection.createStatement();
            System.out.println("Connected to database!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            shutDown();
        }
    }
    private void shutDown() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        System.exit(0);
    }
    public TransactionReport() throws SQLException {
        connectDB();
        setSize(500, 500);
        setLocationRelativeTo(null);
        setTitle("Transaction");
        defaultTable = new JTable();
        panel = new JPanel();
        JPanel tablePanel = new JPanel(new BorderLayout());

        String[] optionList = {"All transactions", "All checked-out books", "Overdue books"};
        options = new JComboBox<>(optionList);
        getReportButton = new JButton("Get report");
        JScrollPane scrollPane = new JScrollPane(defaultTable);
        tablePanel.add(scrollPane,BorderLayout.CENTER);

        getReportButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        getReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Objects.requireNonNull(options.getSelectedItem()).toString().equals("All transactions")){
                    try {
                        showAllTransaction();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                } else if (Objects.requireNonNull(options.getSelectedItem()).toString().equals("All checked-out books")) {
                    try {
                        showAllCheckoutBook();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                } else if (Objects.requireNonNull(options.getSelectedItem()).toString().equals("Overdue books")) {
                    try {
                        showOverdueBooks();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        clear= new JButton("Clear");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel tm = (DefaultTableModel) defaultTable.getModel();
                tm.setDataVector(new Object[][]{}, new Object[]{});

            }
        });
        panel.add(options);
        panel.add(getReportButton);
        panel.add(clear);
        add(panel,BorderLayout.SOUTH);
        add(tablePanel);
        setVisible(true);
    }

    private void showAllTransaction() throws SQLException {
        String sql = "select*from 'transaction'";
        try {
            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData rsmd= rs.getMetaData();
            DefaultTableModel tm = (DefaultTableModel) defaultTable.getModel();

            int col= rsmd.getColumnCount();
            String[] colName = new String[col];
            for (int i=0;i<col;i++)
                colName[i]=rsmd.getColumnName(i+1);
            tm.setColumnIdentifiers(colName);
            int id, book_id, patron_id;
            String checkoutDate,dueDate;
            while (rs.next()){
                id=rs.getInt(1);
                book_id=rs.getInt(2);
                patron_id= rs.getInt(3);
                checkoutDate= rs.getString(4);
                dueDate=rs.getString(5);
                Object[] rows= {id,book_id,patron_id,checkoutDate,dueDate};
                tm.addRow(rows);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    private void showAllCheckoutBook() throws SQLException {
        ArrayList<Integer> bookIDs= new ArrayList<>();
        String sql = "SELECT book_id FROM \"transaction\"";
        ResultSet rs1 = statement.executeQuery(sql);
        while (rs1.next())
            bookIDs.add(rs1.getInt("book_id"));
        DefaultTableModel tm = (DefaultTableModel) defaultTable.getModel();
        int order=1;
        String []colName = {"No","Checkout books"};
        tm.setColumnIdentifiers(colName);

        ArrayList<String> bookTitles = new ArrayList<>();
        String getTitleSql = "SELECT title FROM \"book\" WHERE id = ?";

        for (Integer bookID : bookIDs) {
            try (PreparedStatement getTitleStmt = connection.prepareStatement(getTitleSql)) {
                getTitleStmt.setInt(1, bookID);
                ResultSet titleResultSet = getTitleStmt.executeQuery();

                if (titleResultSet.next()) {
                    String title = titleResultSet.getString("title");
                    bookTitles.add(title);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        for (String book: bookTitles){
            Object[]rows = {order,book};
            tm.addRow(rows);
            order++;
        }
    }


    private int getBookIDByDueDate(String dueDate){
        String sqlGetBookID= "SELECT book_id FROM \"transaction\" WHERE dueDate= ?;";
        int bookID=0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlGetBookID)) {
            preparedStatement.setString(1, dueDate);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    bookID = resultSet.getInt("book_id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bookID;
    }
    private void showOverdueBooks() throws SQLException {
        DefaultTableModel tm = (DefaultTableModel) defaultTable.getModel();
        int order=1;

        String []colName = {"No","Overdue books"};
        tm.setColumnIdentifiers(colName);
        ArrayList<String> dateString= new ArrayList<>();

        String sqlGetDueDate= "SELECT dueDate FROM \"transaction\"";
        ResultSet rs1 = statement.executeQuery(sqlGetDueDate);
        while (rs1.next())
            dateString.add(rs1.getString("dueDate"));

        ArrayList<Integer> bookIDs = new ArrayList<>();
        for (String dueDate: dateString){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate inputDate = LocalDate.parse(dueDate, formatter);
            LocalDate currentDate = LocalDate.now();
            if (inputDate.isBefore(currentDate)){
                bookIDs.add(getBookIDByDueDate(dueDate));
            }
        }


        String getTitleSql = "SELECT title FROM \"book\" WHERE id = ?";
        ArrayList<String> bookTitles = new ArrayList<>();
        for (Integer bookID : bookIDs) {
            try (PreparedStatement getTitleStmt = connection.prepareStatement(getTitleSql)) {
                getTitleStmt.setInt(1, bookID);
                ResultSet titleResultSet = getTitleStmt.executeQuery();

                if (titleResultSet.next()) {
                    String title = titleResultSet.getString("title");
                    bookTitles.add(title);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //add all book into table
        for (String title: bookTitles){
            Object[]rows = {order,title};
            tm.addRow(rows);
            order++;
        }
    }

}
