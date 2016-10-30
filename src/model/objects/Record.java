package model.objects;

/**
 * Created by Måns on 9/18/2016.
 *
 *
 */

public class Record {
    private int amount;
    private long date;

    /**
     *
     * @param amount
     * @param date
     */

    public Record(int amount, long date) {
        this.amount = amount;
        this.date = date;
    }

    /**
     *
     * @param newAmount
     * @return
     */

    public int setAmount(int newAmount) {
        amount = newAmount;
        return amount;
    }

    /**
     *
     * @param newDate
     * @return
     */

    public long setDate(long newDate) {
        date = newDate;
        return date;
    }

    /**
     *
     * @return
     */

    public int getAmount(){
        return amount;
    }

    /**
     *
     * @return
     */

    public long getDate() {
        return date;
    }
}
