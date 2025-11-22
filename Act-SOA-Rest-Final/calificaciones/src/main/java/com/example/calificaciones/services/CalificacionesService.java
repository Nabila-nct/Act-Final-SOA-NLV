package com.example.calificaciones.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.calificaciones.models.CalificacionesModel;
import com.example.calificaciones.repositories.ICalificacionesRepository;

@Service
public class CalificacionesService {
    
    @Autowired
    private ICalificacionesRepository calificacionesRepository;

    public ArrayList<CalificacionesModel> getCalificaciones(){
        return (ArrayList<CalificacionesModel>) calificacionesRepository.findAll();
    }

    public CalificacionesModel saveCalificacion(CalificacionesModel calificacion){
        return calificacionesRepository.save(calificacion);
    }

    public Optional<CalificacionesModel> getCalificacionById(Long id){
        return calificacionesRepository.findById(id);
    }

    public CalificacionesModel updateCalificacionById(CalificacionesModel request, Long id){
        CalificacionesModel calificacion = calificacionesRepository.findById(id).get();
        calificacion.setNombre_alumno(request.getNombre_alumno());
        calificacion.setAsignatura(request.getAsignatura());
        calificacion.setNota(request.getNota());
        return calificacionesRepository.save(calificacion);
    }

    public boolean deleteCalificacionById(Long id){
        try{
            calificacionesRepository.deleteById(id);
            return true;
        } catch(Exception e){
            return false;
        }
    }
}
