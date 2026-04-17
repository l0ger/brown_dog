package com.maplewood.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "classrooms")
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "floor")
    private Integer floor;

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public Integer getFloor() { return floor; }
}
