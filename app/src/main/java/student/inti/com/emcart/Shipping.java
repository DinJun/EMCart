package student.inti.com.emcart;

public class Shipping {
    private String fullname, phonenumber, dateOrdered, timeOrdered, address, orderTotalPrice, status;

    public Shipping() {

    }

    public Shipping(String fullname, String phonenumber, String dateOrdered, String timeOrdered, String address, String orderTotalPrice, String status) {
        this.fullname = fullname;
        this.phonenumber = phonenumber;
        this.dateOrdered = dateOrdered;
        this.timeOrdered = timeOrdered;
        this.address = address;
        this.orderTotalPrice = orderTotalPrice;
        this.status = status;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(String dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public String getTimeOrdered() {
        return timeOrdered;
    }

    public void setTimeOrdered(String timeOrdered) {
        this.timeOrdered = timeOrdered;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrderTotalPrice() {
        return orderTotalPrice;
    }

    public void setOrderTotalPrice(String orderTotalPrice) {
        this.orderTotalPrice = orderTotalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
