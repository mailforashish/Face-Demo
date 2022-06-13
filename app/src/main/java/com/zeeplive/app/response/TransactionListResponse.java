package com.zeeplive.app.response;

public class TransactionListResponse {

    String name, description, amount, date;
    int status;

    public TransactionListResponse(int status, String name, String description, String amount, String date) {
        this.status = status;
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }
}
