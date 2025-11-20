package com.example.racketmanager.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "racket_orders")
public class RacketOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stringType;

    @Column(name = "string_material")
    private String stringMaterial;

    private String tensionMain;
    private String tensionCross;
    private LocalDate requestDate;
    private LocalDate dueDate;
    private String status;

    // 顧客との外部キー
    @ManyToOne(optional = true)
    @JoinColumn(name = "customer_id")
    private User customer;

    // 削除後も残すための表示名
    @Column(name = "customer_name")
    private String customerName;

    private Integer price;
    private String stringerName;

    // ===== Getter / Setter =====
    public Long getId() { return id; }

    public String getStringType() { return stringType; }
    public void setStringType(String stringType) { this.stringType = stringType; }

    public String getStringMaterial() { return stringMaterial; }
    public void setStringMaterial(String stringMaterial) { this.stringMaterial = stringMaterial; }

    public String getTensionMain() { return tensionMain; }
    public void setTensionMain(String tensionMain) { this.tensionMain = tensionMain; }

    public String getTensionCross() { return tensionCross; }
    public void setTensionCross(String tensionCross) { this.tensionCross = tensionCross; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public String getStringerName() { return stringerName; }
    public void setStringerName(String stringerName) { this.stringerName = stringerName; }
}
