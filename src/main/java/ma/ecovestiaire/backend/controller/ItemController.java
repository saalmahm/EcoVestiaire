package ma.ecovestiaire.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import ma.ecovestiaire.backend.dto.CommentResponse;
import ma.ecovestiaire.backend.dto.CreateCommentRequest;
import ma.ecovestiaire.backend.dto.ItemRequest;
import ma.ecovestiaire.backend.dto.ItemResponse;
import ma.ecovestiaire.backend.service.CommentService;
import ma.ecovestiaire.backend.service.FavoriteService;
import ma.ecovestiaire.backend.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final FavoriteService favoriteService;
    private final CommentService commentService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String UPLOAD_DIR = "uploads/items";
    public ItemController(ItemService itemService,
                          FavoriteService favoriteService,
                          CommentService commentService) {
        this.itemService = itemService;
        this.favoriteService = favoriteService;
        this.commentService = commentService;
    }

    private List<String> savePhotos(List<MultipartFile> photos) throws IOException {
        List<String> paths = new ArrayList<>();
        if (photos == null) {
            return paths;
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        for (MultipartFile file : photos) {
            if (file.isEmpty()) {
                continue;
            }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            paths.add(UPLOAD_DIR + "/" + fileName);
        }

        return paths;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponse> createItem(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos
    ) throws IOException {

        ItemRequest request = objectMapper.readValue(dataJson, ItemRequest.class);
        List<String> photoPaths = savePhotos(photos);

        ItemResponse response = itemService.createItem(request, photoPaths);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable Long id,
            @RequestPart("data") String dataJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos
    ) throws IOException {

        ItemRequest request = objectMapper.readValue(dataJson, ItemRequest.class);
        List<String> photoPaths = savePhotos(photos);

        ItemResponse response = itemService.updateItem(id, request, photoPaths);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        ItemResponse response = itemService.getItemById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> searchItems(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String conditionLabel,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, name = "q") String text,
            @RequestParam(defaultValue = "false") boolean includeSold,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10", name = "pageSize") int sizePage
    ) {
        List<ItemResponse> items = itemService.searchItems(
                categoryId,
                size,
                conditionLabel,
                minPrice,
                maxPrice,
                text,
                includeSold,
                page,
                sizePage
        );
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> likeItem(@PathVariable("id") Long itemId) {
        favoriteService.likeItem(itemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> unlikeItem(@PathVariable("id") Long itemId) {
        favoriteService.unlikeItem(itemId);
        return ResponseEntity.noContent().build();
    }

        @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable("id") Long itemId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        CommentResponse response = commentService.addCommentToItem(itemId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable("id") Long itemId
    ) {
        List<CommentResponse> comments = commentService.getCommentsForItem(itemId);
        return ResponseEntity.ok(comments);
    }
}