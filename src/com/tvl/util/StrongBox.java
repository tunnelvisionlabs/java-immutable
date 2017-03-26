/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvl.util;

/**
 * Holds a reference to a value.
 *
 * @param <T> The type of the value that the {@link StrongBox} references.
 */
final class StrongBox<T> {
	/**
	 * Represents the value that the {@link StrongBox} references.
	 */
	public T value;

	/**
	 * Constructs a new instance of the {@link StrongBox} class which can hold a reference to a value.
	 */
	public StrongBox() {
	}

	/**
	 * Constructs a new instance of the {@link StrongBox} class with the specified initial value.
	 */
	public StrongBox(T value) {
		this.value = value;
	}
}
