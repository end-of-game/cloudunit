package fr.treeptik.cloudunit.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Map;

@Entity
public class ModuleConfiguration {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ElementCollection(fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.FALSE)
	private Map<String, String> properties;

	private String name;

	private String path;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
