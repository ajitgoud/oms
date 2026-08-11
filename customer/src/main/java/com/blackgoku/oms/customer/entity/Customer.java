package com.blackgoku.oms.customer.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_customer_code",
                        columnNames = "customer_code"
                ),
                @UniqueConstraint(
                        name = "uk_customer_email",
                        columnNames = "email"
                )
        }
)
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "customer_code",
            nullable = false,
            length = 50
    )
    private String customerCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status;

    @Version
    @Column(nullable = false)
    private Long version;
}