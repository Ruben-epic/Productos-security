package com.rubenepic.seguridad.controller;

import com.rubenepic.seguridad.entity.Producto;
import com.rubenepic.seguridad.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    ProductoService productoService;

    @GetMapping("/lista")
    public ModelAndView list(){
       var mv = new ModelAndView();
       mv.setViewName("/producto/lista");
       var productos = productoService.findAll();
       mv.addObject("productos", productos);
       return mv;
    }

    @GetMapping("/nuevo")
    public String nuevo(){
       return "producto/nuevo";
    }

    @PostMapping("/guardar")
    public ModelAndView crear(@RequestParam String nombre, @RequestParam float precio){
       var mv = new ModelAndView();
       if(nombre.isBlank()){
           mv.setViewName("producto/nuevo");
           mv.addObject("error", "el nombre no puede estar vacio");
           return mv;
       }
        if(precio < 1){
            mv.setViewName("producto/nuevo");
            mv.addObject("error", "el precio debe ser mayor a cero");
            return mv;
        }
        if(productoService.existsByNombre(nombre)){
            mv.setViewName("producto/nuevo");
            mv.addObject("error", "el nombre ya existe");
            return mv;
        }
        var producto = new Producto(nombre, precio);
        productoService.save(producto);
        mv.setViewName("redirect:/producto/lista");
        return mv;
    }

    @GetMapping("/editar/{id}")
    public ModelAndView editar(@PathVariable int id){
        if(!productoService.existsById(id)) return new ModelAndView("redirect:/producto/lista");

        var producto  = productoService.getOne(id).get();
        var mv = new ModelAndView("producto/editar");
        mv.addObject("producto", producto);
        return mv;
    }

    @PostMapping("/actualizar")
    public ModelAndView actualizar(@RequestParam int id, @RequestParam String nombre, @RequestParam float precio){
        if(!productoService.existsById(id))
            return new ModelAndView("redirect:/producot/lista");
        var mv = new ModelAndView();
        var producto = productoService.getOne(id).get();
        if(nombre.isBlank()){
            mv.setViewName("producto/nuevo");
            mv.addObject("error", "el nombre no puede estar vacio");
            mv.addObject("producto", producto);
            return mv;
        }
        if(precio < 1){
            mv.setViewName("producto/nuevo");
            mv.addObject("error", "el precio debe ser mayor que cero");
            mv.addObject("producto", producto);
            return mv;
        }
        if(productoService.existsByNombre(nombre) && productoService.getByNombre(nombre).get().getId() != id){
            mv.setViewName("producto/editar");
            mv.addObject("error", "ese nombre ya existe");
            mv.addObject("producto", producto);
            return mv;
        }

        producto.setNombre(nombre);
        producto.setPrecio(precio);
        productoService.save(producto);
        return new ModelAndView("redirect:/producto/lista");
    }

    @GetMapping("/detalle/{id}")
    public ModelAndView detalle(@PathVariable int id){
        if(!productoService.existsById(id)) return new ModelAndView("redirect:/producto/lista");

        var producto  = productoService.getOne(id).get();
        var mv = new ModelAndView("/producto/detalle");
        mv.addObject("producto", producto);
        return mv;
    }

    @GetMapping("/borrar/{id}")
    public ModelAndView eliminar(@PathVariable int id){
        if(productoService.existsById(id)) {
            productoService.delete(id);
            return new ModelAndView("redirect:/producto/lista");
        }

        return null;
    }
}
