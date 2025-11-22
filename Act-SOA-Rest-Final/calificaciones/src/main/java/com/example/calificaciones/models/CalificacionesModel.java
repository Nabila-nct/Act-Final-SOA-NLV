package com.example.calificaciones.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "calificaciones")
public class CalificacionesModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_alumno")
    @jakarta.validation.constraints.NotBlank
    private String nombre_alumno;

    @jakarta.validation.constraints.NotBlank
    private String asignatura;	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.DecimalMin("0.0")
	@jakarta.validation.constraints.DecimalMax("10.0")
	private Double nota;

	private LocalDateTime fecha;

    public CalificacionesModel() {}

    public CalificacionesModel(String nombre_alumno, String asignatura, Double nota) {
        this.nombre_alumno = nombre_alumno;
        this.asignatura = asignatura;
        this.nota = nota;
    }	@PrePersist
	public void prePersist() {
		if (this.fecha == null) {
			this.fecha = LocalDateTime.now();
		}
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getNombre_alumno() { return nombre_alumno; }
	public void setNombre_alumno(String nombre_alumno) { this.nombre_alumno = nombre_alumno; }

	public String getAsignatura() { return asignatura; }
	public void setAsignatura(String asignatura) { this.asignatura = asignatura; }

	public Double getNota() { return nota; }
	public void setNota(Double nota) { this.nota = nota; }

	public LocalDateTime getFecha() { return fecha; }
	public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
