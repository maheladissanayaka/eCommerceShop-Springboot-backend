package com.shop.eCommerceShop.model;

import jakarta.persistence.Column;


public class PaymentInformation {
	
	@Column(name = "cardholder_name")
	private String cardholderName;
	
	@Column(name = "card_number")
	private String cardNumber;
	
	@Column(name = "expiration_date")
	private String expirationDate;
	
	@Column(name = "cvv")
	private String cvv;
}

//package com.shop.eCommerceShop.model;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "payment_information")
//public class PaymentInformation {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id; // Primary key
//
//    @Column(name = "cardholder_name")
//    private String cardholderName;
//
//    @Column(name = "card_number")
//    private String cardNumber;
//
//    @Column(name = "expiration_date")
//    private String expirationDate;
//
//    @Column(name = "cvv")
//    private String cvv;
//
//    // Constructors
//    public PaymentInformation() {}
//
//    public PaymentInformation(String cardholderName, String cardNumber, String expirationDate, String cvv) {
//        this.cardholderName = cardholderName;
//        this.cardNumber = cardNumber;
//        this.expirationDate = expirationDate;
//        this.cvv = cvv;
//    }
//
//    // Getters and setters
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getCardholderName() {
//        return cardholderName;
//    }
//
//    public void setCardholderName(String cardholderName) {
//        this.cardholderName = cardholderName;
//    }
//
//    public String getCardNumber() {
//        return cardNumber;
//    }
//
//    public void setCardNumber(String cardNumber) {
//        this.cardNumber = cardNumber;
//    }
//
//    public String getExpirationDate() {
//        return expirationDate;
//    }
//
//    public void setExpirationDate(String expirationDate) {
//        this.expirationDate = expirationDate;
//    }
//
//    public String getCvv() {
//        return cvv;
//    }
//
//    public void setCvv(String cvv) {
//        this.cvv = cvv;
//    }
//}
//
