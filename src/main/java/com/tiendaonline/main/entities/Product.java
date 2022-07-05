package com.tiendaonline.main.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "product")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String url_image;
    private float price;
    private int discount;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name="category", nullable=true, referencedColumnName = "id")
    private Category category;
}
