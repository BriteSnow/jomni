package org.jomni.test.app;

import java.util.Optional;

public class User extends BaseEntity<Long> {

	public enum Role {
		visitor, user, admin;
	}

	private Role role;
	private Company company;
	private String username;
	private Integer since;

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getSince() {
		return since;
	}

	public void setSince(Integer since) {
		this.since = since;
	}
}
