package com.cibertec.apireservarestaurante.service;

import com.cibertec.apireservarestaurante.entity.Restaurante;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class RestauranteService {

    @Autowired
    private StorageClient storageClient;
    private static final String COLLECTION_NAME = "restaurante" ;
    public RestauranteService(StorageClient storageClient){
        this.storageClient = storageClient;
    }
    public String guardarRestaurante(Restaurante restaurante) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<DocumentReference> documentReference = dbFirestore.collection(COLLECTION_NAME).add(restaurante);
        String documentId = documentReference.get().getId();
        ApiFuture<WriteResult> writeResult = dbFirestore.collection(COLLECTION_NAME)
                .document(documentId)
                .update("id", documentId);
        writeResult.get();
        return documentId;
    }

    public List<Restaurante> obtenerRestaurantes() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Iterable<DocumentReference> documentReference = dbFirestore.collection(COLLECTION_NAME).listDocuments();
        Iterator<DocumentReference> iterator = documentReference.iterator();
        List<Restaurante> listaRestaurante = new ArrayList<>();
        Restaurante restaurante = null;
        while (iterator.hasNext()) {
            DocumentReference documentReference1 = iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference1.get();
            DocumentSnapshot document = future.get();
            restaurante = document.toObject(Restaurante.class);
            listaRestaurante.add(restaurante);
        }
        return listaRestaurante;

    }

    public Restaurante obtenerRestauranteById(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        Restaurante restaurante = null;
        if(document.exists()){
            restaurante = document.toObject(Restaurante.class);
            return restaurante;
        }else {
            return null;
        }
    }

    public String actualizarRestaurante(Restaurante restaurante) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection(COLLECTION_NAME).document(restaurante.getId()).set(restaurante);
        return  collectionApiFuture.get().getUpdateTime().toString();
    }

    public String eliminarRestaurante(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection(COLLECTION_NAME).document(id).delete();
        return "El documento con el Restaurante ID:"+id+"ha sido eliminado con exito" ;
    }

    public String uploadPhoto(MultipartFile file) throws IOException {
        String folderName = "restaurantes/";
        String fileName = folderName + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        Bucket bucket = storageClient.bucket("reservarestaurantes-ca155.appspot.com");
        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());

        String fileUrl = "https://firebasestorage.googleapis.com/v0/b/" +
                bucket.getName() +
                "/o/" +
                encodePath(fileName) +
                "?alt=media";

        return fileUrl;
    }
    private String encodePath(String path) {
        return path.replace("/", "%2F");
    }
}
