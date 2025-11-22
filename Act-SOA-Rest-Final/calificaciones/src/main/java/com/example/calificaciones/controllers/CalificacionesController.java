package com.example.calificaciones.controllers;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.calificaciones.models.CalificacionesModel;
import com.example.calificaciones.services.CalificacionesService;

@RestController
@RequestMapping("/calificaciones")
public class CalificacionesController {
    @Autowired
    private CalificacionesService calificacionesService;

    @GetMapping
    public ArrayList<CalificacionesModel> getCalificaciones(){
        return calificacionesService.getCalificaciones();
    }

    @PostMapping
    public CalificacionesModel saveCalificacion(@RequestBody CalificacionesModel calificacionesModel){
        return this.calificacionesService.saveCalificacion(calificacionesModel);
    }
    
    @GetMapping(path = "/{id}")
    public Optional<CalificacionesModel> getCalificacionById(@PathVariable Long id){
        return this.calificacionesService.getCalificacionById(id);
    }
    
    @PutMapping(path = "/{id}")
    public CalificacionesModel updateCalificacionById(@RequestBody CalificacionesModel request, @PathVariable Long id){
        return this.calificacionesService.updateCalificacionById(request, id);
    }

    @DeleteMapping(path = "/{id}")
    public String deleteCalificacionById(@PathVariable("id") Long id){
        boolean ok = this.calificacionesService.deleteCalificacionById(id);
        if(ok){
            return "Calificación eliminada";
        }else{
            return "No se pudo eliminar la calificación";
        }
    }
    
}
