package com.maplewood.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "semesters")
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "order_in_year", nullable = false)
    private int orderInYear;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    @Column(name = "is_active")
    private int isActive;

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getYear() { return year; }
    public int getOrderInYear() { return orderInYear; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public boolean isActive() { return isActive == 1; }
}
