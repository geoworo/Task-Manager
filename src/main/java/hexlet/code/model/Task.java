package hexlet.code.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@Table(name = "tasks")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Task {
    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ToString.Include
    @NotBlank
    @Size(min = 1)
    private String name;

    @ToString.Include
    private int index;

    @ToString.Include
    private String description;

    @NotNull
    @ManyToOne
    private TaskStatus taskStatus;

    @CreatedDate
    private LocalDate createdAt;
}
