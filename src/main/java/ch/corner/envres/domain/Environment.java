package ch.corner.envres.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Environment.
 */
@Entity
@Table(name = "environment")
@Document(indexName = "environment")
public class Environment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 3, max = 20)
    @Column(name = "environment_name", length = 20, nullable = false)
    private String environmentName;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private DTAP level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public String getEnvironmentDescription() {
    	if(environmentName != null && level != null)
    		return environmentName+" - "+level.getLevelName();
    	else
    		return environmentName + " - " + "undefined lvl";
    }


    public DTAP getLevel() {
        return level;
    }

    public void setLevel(DTAP dTAP) {
        this.level = dTAP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Environment environment = (Environment) o;
        if(environment.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, environment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Environment{" +
            "id=" + id +
            ", environmentName='" + environmentName + "'" +
            '}';
    }
}
