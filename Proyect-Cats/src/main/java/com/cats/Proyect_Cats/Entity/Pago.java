package com.cats.Proyect_Cats.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    @Id

    // @GenerateValue (strategy = GenertionType.IDENTITY)
    private String id;

    //private LocalDate nombre;
    private String raza;
    private String edad;
    @Column(unique = true)
    private String codigo;

}
