package com.company.controller;

import com.company.dto.ProductDTO;
import com.company.dto.ResponseDTO;
import com.company.entity.Category;
import com.company.entity.Product;
import com.company.entity.Supplier;
import com.company.service.CategoryService;
import com.company.service.ProductService;
import com.company.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;
    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService, SupplierService supplierService){
        this.productService=productService;
        this.categoryService=categoryService;
        this.supplierService=supplierService;
    }

    @GetMapping("/products")
    public ResponseEntity<ResponseDTO> getAllProducts(){
        try {
            List<Product> products = productService.getProducts();
            List<ProductDTO> productDTOS = new ArrayList<>();
            for (Product product : products) {
                productDTOS.add(new ProductDTO(product));
            }
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.of(productDTOS, "success"));
        }catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.of("Error while getting suppliers"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.of("Bad Request"));
        }
    }
    @GetMapping("/product")
    public ResponseEntity<ResponseDTO> getProduct(@RequestParam("id") long id){
        try {
            Product product = productService.getProductByID(id);
            return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.of(new ProductDTO(product), "success"));
        }catch (DataAccessException ex) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.of("Error while getting suppliers"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.of("Bad Request"));
        }
    }
    @PostMapping("/product")
    public ResponseEntity<ResponseDTO> addProduct(@RequestBody ProductDTO productDTO) {
        try {
            productService.addOrUpdateProduct(convert(productDTO));
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.of(productDTO,"success"));
        }catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.of("Error while adding supplier"));
        } catch (MethodArgumentNotValidException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.of("Validation failed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.of("Bad Request"));
        }
    }
    @PutMapping("/product")
    public ResponseEntity<ResponseDTO> updateProduct(@RequestBody ProductDTO productDTO){
        try {
            Product product = convert(productDTO);
            product.setId(productDTO.getId());
            productService.addOrUpdateProduct(product);

            return ResponseEntity.status(HttpStatus.OK).body(ResponseDTO.of(new ProductDTO(productService.getProductByID(productDTO.getId())),"success"));
        }catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.of("Error while adding supplier"));
        } catch (MethodArgumentNotValidException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.of("Validation failed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.of("Bad Request"));
        }
    }
    @DeleteMapping("/product")
    public ResponseEntity<String> deleteProduct(@RequestParam("id") long id){
        try{
        Product product=productService.deleteProduct(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("success");
        }
        catch (DataAccessException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(("Error while getting suppliers"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(("Bad Request"));
        }
    }


    private Product convert(ProductDTO productDTO){
        Product product=new Product();
        product.setDescription(productDTO.getDescription());
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        Supplier supplier=supplierService.getById(productDTO.getSupplierDTO().getId());
        Category category=categoryService.getById(productDTO.getCategoryDTO().getId());

        product.setCategoryByCategoryId(category);
        product.setSupplierBySupplierId(supplier);
        return product;
    }
}
