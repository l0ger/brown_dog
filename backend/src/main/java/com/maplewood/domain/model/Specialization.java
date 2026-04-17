package com.maplewood.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "specializations")
public class Specialization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
