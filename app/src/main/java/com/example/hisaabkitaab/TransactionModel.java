package com.example.hisaabkitaab;

public class TransactionModel {
    private String id;
    private String date;
    private String transactor;
    private String description;
    private String type;
    private String amount;
    private String balance;

    public TransactionModel(String date, String transactor, String description, String type, String amount, String balance) {
        this.date = date;
        this.transactor = transactor;
        this.description = description;
        this.type = type;
        this.amount = amount;
        this.balance = balance;
    }

    public TransactionModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTransactor() {
        return transactor;
    }

    public void setTransactor(String transactor) {
        this.transactor = transactor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
