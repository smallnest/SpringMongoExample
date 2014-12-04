package com.colobu.springmongo.entity;

import org.springframework.data.geo.Point;


public class Address {	
	private Point location;
	private String street;
	private String zipCode;
	
	public Address(Point location) {
		super();
		this.location = location;
	}	
	
}