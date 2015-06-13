package org.jomni.test.app;

import java.util.Optional;

public class BaseEntity<I> {

	public I id;

	public I getId() {
		return id;
	}
	public void setId(I id) {
		this.id = id;
	}


	public Optional<I> getOptionalId() {
		return Optional.ofNullable(getId());
	}
}
