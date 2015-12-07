package com.ibm.cloudoe.samples;

/**
 * Represents a book entity.
 * 
 * @author akira
 */
public class Book {
	private final String title;
	private final String author;
	
	public Book(String title, String author) {
		super();
		this.title = title;
		this.author = author;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getAuthor() {
		return author;
	}
}
