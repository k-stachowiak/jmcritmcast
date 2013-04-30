package dto;

import javax.xml.bind.annotation.XmlElement;

public class NodeDTO {

	@XmlElement(name = "id")
	private int id;

	@XmlElement(name = "x")
	private double x;

	@XmlElement(name = "y")
	private double y;

	NodeDTO() {
	}

	public NodeDTO(int id, double x, double y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}

	public final int getId() {
		return id;
	}

	public final double getX() {
		return x;
	}

	public final double getY() {
		return y;
	}

	final void setId(int id) {
		this.id = id;
	}

	final void setX(double x) {
		this.x = x;
	}

	final void setY(double y) {
		this.y = y;
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeDTO other = (NodeDTO) obj;
		if (id != other.id)
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

}
