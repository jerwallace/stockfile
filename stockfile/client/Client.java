/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockfile.client;

/**
 * Class describing a Client object
 * @author MrAtheist
 */
public class Client {
	
	private String type;
	private String description;
	private String manufacturer;
	private String modelNo;

	public Client() {
		
	}

	public Client(String type, String description, String manufacturer, String modelNo) {

		this.type = type; 
		this.description = description; 
		this.manufacturer = manufacturer; 
		this.modelNo = modelNo; 
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the manufacturer
	 */
	public String getManufacturer() {
		return manufacturer;
	}

	/**
	 * @param manufacturer the manufacturer to set
	 */
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	/**
	 * @return the modelNo
	 */
	public String getModelNo() {
		return modelNo;
	}

	/**
	 * @param modelNo the modelNo to set
	 */
	public void setModelNo(String modelNo) {
		this.modelNo = modelNo;
	}
}
