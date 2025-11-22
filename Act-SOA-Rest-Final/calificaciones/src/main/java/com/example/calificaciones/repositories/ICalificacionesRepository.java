package com.example.calificaciones.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.calificaciones.models.CalificacionesModel;
@Repository
public interface ICalificacionesRepository extends JpaRepository<CalificacionesModel, Long>{

}


