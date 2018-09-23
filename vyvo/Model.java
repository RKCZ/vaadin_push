/**
 * 
 */
package vyvo;

import java.time.LocalDate;

/**
 * @author kaliv
 *
 */
public class Model {
	private Long id;
	
	private String name, lastName, Note;
	
	private LocalDate age;
	
	private Sex sex;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the age
	 */
	public LocalDate getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(LocalDate age) {
		this.age = age;
	}

	/**
	 * @return the sex
	 */
	public Sex getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getNote() {
		return Note;
	}

	public void setNote(String note) {
		Note = note;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
