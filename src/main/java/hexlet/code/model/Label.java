package hexlet.code.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@Table(name = "labels")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Label {
    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ToString.Include
    @NotBlank
    @Column(unique = true)
    @Size(min = 3, max = 1000)
    private String name;

    @CreatedDate
    private LocalDate createdAt;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "labels", cascade = CascadeType.MERGE)
    private Set<Task> tasks;
}
