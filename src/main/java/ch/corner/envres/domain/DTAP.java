package ch.corner.envres.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTAP.
 */
@Entity
@Table(name = "dtap")
@Document(indexName = "dtap")
public class DTAP implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "level_name")
    private String levelName;
    
    @NotNull
    @Column(name = "level_id", nullable = false)
    private Integer levelId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLevelName() {
        return levelName;
    }
    
    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public Integer getLevelId() {
        return levelId;
    }
    
    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DTAP dTAP = (DTAP) o;
        if(dTAP.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, dTAP.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DTAP{" +
            "id=" + id +
            ", levelName='" + levelName + "'" +
            ", levelId='" + levelId + "'" +
            '}';
    }
}
