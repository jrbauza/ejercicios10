package ob.spring.ejercicios10.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Laptop {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private int autonomy;
	private String trademark;
	
	public Laptop() {}

	public Laptop(Long id, int autonomy, String trademark) {
		this(autonomy, trademark);
	}

	public Laptop(int autonomy, String trademark) {
		super();
		this.autonomy = autonomy;
		this.trademark = trademark;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getAutonomy() {
		return this.autonomy;
	}

	public void setAutonomy(int autonomy) {
		this.autonomy = autonomy;
	}

	public String getTrademark() {
		return this.trademark;
	}

	public void setTrademark(String trademark) {
		this.trademark = trademark;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Laptop laptop = (Laptop) o;
		return autonomy == laptop.autonomy && id.equals(laptop.id) && Objects.equals(trademark, laptop.trademark);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, autonomy, trademark);
	}
}