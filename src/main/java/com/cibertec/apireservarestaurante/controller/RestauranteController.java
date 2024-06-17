package com.cibertec.apireservarestaurante.controller;

import com.cibertec.apireservarestaurante.entity.Restaurante;
import com.cibertec.apireservarestaurante.service.RestauranteService;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;
    @PostMapping("/restaurante")
    public String guardarRestaurante (@RequestBody Restaurante restaurante) throws ExecutionException, InterruptedException {
        return restauranteService.guardarRestaurante(restaurante);
    }
    @GetMapping("/restaurantes")
    public List<Restaurante> obtenerRestaurantes () throws ExecutionException, InterruptedException {
        return restauranteService.obtenerRestaurantes();
    }
    @GetMapping("/restaurante/{name}")
    public Restaurante obtenerRestaurante (@PathVariable String name) throws ExecutionException, InterruptedException {
        return restauranteService.obtenerRestauranteById(name);
    }
    @PutMapping("/restaurante")
    public String actualizarRestaurante (@RequestBody Restaurante restaurante) throws ExecutionException, InterruptedException {
        return restauranteService.actualizarRestaurante(restaurante);
    }
    @DeleteMapping("/restaurante/{name}")
    public String eliminarRestaurante (@PathVariable String name) throws ExecutionException, InterruptedException {
        return restauranteService.eliminarRestaurante(name);
    }
    @PostMapping("/upload")
    public ResponseEntity<String> uploadPhoto(@RequestParam("foto") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file selected");
        }

        try {
            String fileUrl = restauranteService.uploadPhoto(file);
            return ResponseEntity.status(HttpStatus.OK).body(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }
}
