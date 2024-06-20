package code;

import common.DateUtils;
import common.PatronType;

import java.util.*;

public class LibraryManager {
    private List<Book> books=new ArrayList<>();
    private List<LibraryTransaction> transactions=new ArrayList<>();
    public LibraryManager()
    {

    }

    public void addBook(Book book)
    {
        books.add(book);
    }

    public List<Book> getBooks() {
        return books;
    }


    public List<LibraryTransaction> getCheckedOutBooks(Patron patron)
    {
        List<LibraryTransaction> checkedOutBook= new ArrayList<>();
        for (LibraryTransaction trans : transactions){
            if (trans.getPatron().equals(patron)){
                checkedOutBook.add(trans);
            }
        }
        return checkedOutBook;
    }

    public List<LibraryTransaction> getTransactions() {
        return transactions;
    }

    public void checkoutBook(Patron patron, Book book, Date checkoutDate, Date duedate)
    {
        int maxLimit = 0;
        if (patron.getPatronType()==PatronType.REGULAR){
            maxLimit=3;
        }
        if (patron.getPatronType().equals(PatronType.PREMIUM)){
            maxLimit=5;
        }
        List<LibraryTransaction> numCheckout= getCheckedOutBooks(patron);
        if (numCheckout.size()< maxLimit){
            LibraryTransaction transaction= new LibraryTransaction(patron,book,checkoutDate,duedate);
            transactions.add(transaction);
            book.setNumCopies(book.getNumCopies()-1);
            System.out.println("Checkout success");
        }
        else {
            System.out.println("You have checkout maximum limit of book");
        }
    }

    public void returnBook(LibraryTransaction transaction, Date returnDate)
    {
        transaction.getBook().setNumCopies(transaction.getBook().getNumCopies()+1);
        transaction.setReturnDate(returnDate);
        List<LibraryTransaction> numCheckout= getCheckedOutBooks(transaction.getPatron());
        numCheckout.remove(transaction);
        if (returnDate.after(transaction.getDueDate())){
            double fine =transaction.calculateFine();
        }
        System.out.println("Return book success");
    }

    public List<LibraryTransaction> getOverdueBooks()
    {
        List<LibraryTransaction> overdue= new ArrayList<>();
        DateUtils date =new DateUtils();
        for (LibraryTransaction trans : transactions){
            if (trans.getReturnDate()==null && trans.getDueDate().before(date.getCurrentDate())){
                overdue.add(trans);
            }
        }

        return overdue;
    }
    public void sort() {
        // Sort the patrons based on their patronID
        Collections.sort(transactions, new Comparator<LibraryTransaction>() {
            @Override
            public int compare(LibraryTransaction t1, LibraryTransaction t2) {
                return t1.getPatron().getPatronID().compareTo(t2.getPatron().getPatronID());
            }
        });
    }

}
