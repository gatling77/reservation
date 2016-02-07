package ch.corner.envres.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Appl.
 */
@Entity
@Table(name = "appl")
@Document(indexName = "appl")
public class Appl implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 3, max = 40)
    @Column(name = "appl_name", length = 40, nullable = false)
    private String applName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "appl_compatible_environments",
               joinColumns = @JoinColumn(name="appls_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="compatible_environmentss_id", referencedColumnName="ID"))
    private Set<Environment> compatibleEnvironmentss = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplName() {
        return applName;
    }

    public void setApplName(String applName) {
        this.applName = applName;
    }

    public Set<Environment> getCompatibleEnvironmentss() {
        return compatibleEnvironmentss;
    }

    public void setCompatibleEnvironmentss(Set<Environment> environments) {
        this.compatibleEnvironmentss = environments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Appl appl = (Appl) o;
        if(appl.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, appl.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Appl{" +
            "id=" + id +
            ", applName='" + applName + "'" +
            '}';
    }
}
