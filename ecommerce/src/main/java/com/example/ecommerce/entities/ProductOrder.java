package com.example.ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "product_order" , schema = "order")
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "related_order")
    @JsonIgnore
    @ToString.Exclude
    private Order order;

    @Basic
    @Column(name = "quantity", nullable = true)
    private int quantity;

    @Basic
    @Column(name= "price_at_purchase", nullable = true)
    private BigDecimal priceAtPurchase;


    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "product")
    private Product product;


}
