package net.gnfe.util.ddd;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class Entity implements Serializable, Comparable<Entity> {

	public abstract Long getId();

	public boolean equals(Object other) {

		if (!getClass().isInstance(other)) {
			return false;
		}

		Long id = getId();

		if (id == null) {
			return super.equals(other);
		}

		Entity castOther = (Entity) other;
		Long castOtherId = castOther.getId();

		EqualsBuilder equalsBuilder = new EqualsBuilder();
		EqualsBuilder append = equalsBuilder.append(id, castOtherId);
		boolean equals = append.isEquals();
		return equals;
	}

	public int hashCode() {

		Long id = getId();

		if (id == null) {
			return super.hashCode();
		}

		return new HashCodeBuilder().append(id).toHashCode();
	}

	public int compareTo(Entity o) {

		Long oId = o.getId();
		if (oId == null) {
			return 1;
		}

		Long thisId = getId();
		if (thisId == null) {
			return -1;
		}

		int compare = thisId.compareTo(oId);
		return compare;
	}

	@Override
	public String toString() {
		return getClass().getName() + "#" + getId();
	}
}