package co.com.pragma.r2dbc.entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("tipos_prestamo")
public class TipoPrestamoEntity {
    @Id
    private Long id;
    private String nombre;
}
