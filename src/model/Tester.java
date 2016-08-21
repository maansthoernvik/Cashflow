package model;

import model.DateTime.CurrentDate;

import java.util.Timer;

public class Tester {

    public static void main(String[] args) {
        //Timer dateUpdater = new Timer();
        //dateUpdater.schedule(new CurrentDate(), 0, 5000);

        User user = new User(1, "Name");
        user.addAllLoans();

        Loan loan = new Loan("Name", 1, 0.1, 100, 11, 11);
        Loan loanTwo = new Loan("Nema", 2, 0.2, 200, 22, 22);

        for (Loan l : user.getLoans()) {
            System.out.println(l);
            System.out.println();
        }

        user.addLoan(loan);
        user.addLoan(loanTwo);
        user.removeLoan(loanTwo);

        for (Loan ll : user.getLoans()) {
            System.out.println(ll);
            System.out.println();
        }
    }
}
