package com.seti.webflux_test.domain.usecase;

import org.springframework.stereotype.Service;

import com.seti.webflux_test.domain.model.Product;
import com.seti.webflux_test.domain.port.ProductRepository;
import com.seti.webflux_test.infraestructure.entrypoint.web.exception.CustomException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductRepository productRepository;

    public Mono<Product> createProduct(Product product) {
        return productRepository.save(product);
    }

    public Mono<Product> updateProduct(Product product) {
        return productRepository.findById(product.getId())
                .switchIfEmpty(Mono.error(new CustomException("El producto que desea actualizar no existe.")))
                .flatMap(existingProduct -> {
                    Product updatedProduct = existingProduct.applyUpdates(product);
                    return productRepository.save(updatedProduct);
                });
    }

    public Mono<Product> updateStock(String id, String quantity) {

        Long productId;
        Long quantityDifference;

        try {
            productId = Long.valueOf(id);
            quantityDifference = Long.valueOf(quantity);
        } catch (NumberFormatException e) {
            return Mono.error(new CustomException("Error en el formulario."));
        }

        return productRepository.findById(productId)
                .switchIfEmpty(
                        Mono.error(new CustomException("El producto al cual desea actualizar el stock no existe.")))
                .flatMap(existingProduct -> {

                    Long newStock = existingProduct.getStock() + quantityDifference;

                    if (newStock < 0)
                        return Mono.error(new CustomException("Error -> La cantidad resultante de stock es negativa."));

                    Product updatedProduct = existingProduct.applyStockChanges(quantityDifference);
                    return productRepository.save(updatedProduct);
                });
    }
}