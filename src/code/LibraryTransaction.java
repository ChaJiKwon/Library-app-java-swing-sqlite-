package code;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LibraryTransaction {
    private Patron patron;
    private Book book;
    private Date checkoutDate;
    private Date dueDate;
    private Date returnDate;
    private double fine;

    public LibraryTransaction(Patron patron, Book book,
                              Date checkoutDate,
         Date dueDate)
    {
        this.patron=patron;
        this.book=book;
        this.checkoutDate=checkoutDate;
        this.dueDate=dueDate;
        this.fine=calculateFine();
    }

    public Patron getPatron() {
        return patron;
    }

    public void setPatron(Patron patron) {
        this.patron = patron;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Date getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public double getFine()
    {
        return this.fine;
    }

    public double calculateFine()
    {
        double overdueFine = 0;
        if (returnDate!=null){
            long overDateMillis = this.returnDate.getTime() - this.dueDate.getTime();
            long overDate = TimeUnit.DAYS.convert(overDateMillis, TimeUnit.MILLISECONDS);
            if (overDate>=1 && overDate<=7){
                overdueFine=1.00*overDate;
            } else if (overDate>=8 && overDate<=14) {
                overdueFine=2*overDate;
            }
            if (overDate>14){
                overdueFine=3* overDate;
            }
        }
        this.fine=overdueFine;
        return this.fine;
    }

    public String getDescription()
    {
        String s1="--------------------------------------\n";
        String s2= "-------------------------------------";
        SimpleDateFormat simp = new SimpleDateFormat("E, MMM dd yyyy");
        String patronID = getPatron().getPatronID();
        String bookISBN = getBook().getISBN();
        String checkoutDate = simp.format(getCheckoutDate());
        String dueDate = simp.format(getDueDate());
        String returnDate;
        if (getReturnDate()==null) {
             returnDate = "Book haven't returned yet.";
        } else if (getReturnDate().before(getDueDate())|| getReturnDate().equals(getDueDate())) {
            returnDate=simp.format(getReturnDate()) + " (Book returned on time )";
        } else {
            returnDate= simp.format(getReturnDate());
        }
        String formattedTransaction = String.format("Transaction Details:\n" +
                        "  Patron ID: %s\n" +
                        "  Book ISBN: %s\n" +
                        "  Checkout Date: %s\n" +
                        "  Due Date: %s\n" +
                        "  Return Date: %s\n" +
                        "  Fine Amount: $%,.2f",
                patronID, bookISBN, checkoutDate, dueDate, returnDate, fine );
            return s1+ formattedTransaction+ "\n" + s2;
    }
  /*  public static void main(String[] args)
    {
        Date dueDate =new Date(2023-1900, Calendar.JANUARY,1);
        Date checkout= new Date(2022-1900, Calendar.JANUARY,1);
        Date returnDate= new Date(2023-1900, Calendar.JANUARY,10);

        Book book = new Book("ahaha", "John Doe", Genre.ROMANCE, 2022,4);

        Patron p1 = new Patron("John Doe", new Date(2001, 0, 1),
                "john@example.com", "555-123-4567", PatronType.PREMIUM);
        Patron p2 = new Patron("vu", new Date(2003, 1, 2),
                "jeje@example.com", "555-987-6543", PatronType.REGULAR);
        LibraryTransaction trans = new LibraryTransaction(p1,
               book,checkout,dueDate );
        System.out.println(trans.getFine());
        System.out.println(trans.getDescription());
    }*/
}
