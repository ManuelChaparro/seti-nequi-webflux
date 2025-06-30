package com.seti.webflux_test.domain.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.seti.webflux_test.domain.model.Product;
import com.seti.webflux_test.domain.port.ProductRepository;
import com.seti.webflux_test.infraestructure.entrypoint.web.exception.CustomException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductUseCase {

    private final Logger logger = LoggerFactory.getLogger(ProductUseCase.class);

    private final ProductRepository productRepository;

    public Mono<Product> createProduct(Product product) {

        // Cumplimiento punto 4. OnError / DoOnError
        return productRepository.save(product)
                .doOnError(err -> 
                    // Podriamos "Simular" que en caso de un error al agregar un producto
                    // Se le envíe una notificación al usuario vía correo
                    logger.info(
                            "SIMULACIÓN DE CORREO: No fue posible crear el producto {} con stock {}, debido a: {}",
                            product.getName(),
                            product.getStock(), err.getMessage())
                );
    }

    public Mono<Product> updateProduct(Product product) {
        return productRepository.findById(product.getId())
                .switchIfEmpty(Mono.error(new CustomException("El producto que desea actualizar no existe.")))
                .flatMap(existingProduct -> {
                    Product updatedProduct = existingProduct.applyUpdates(product);
                    return productRepository.save(updatedProduct);
                })
                .doOnNext(updatedItem -> 
                    // Cumplimiento punto 4. OnNext / DoOnNext
                    // Simulamos el envío de un correo al usuario notificando que
                    // la información del producto fue actualizada exitosamente
                    logger.info("Producto actualizado: {}", updatedItem)
                );
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
                })
                .doOnError(err -> 
                    // Cumplimiento punto 4. OnError / DoOnError
                    // Simulamos el envío de un correo al usuario notificando
                    // Que no fue posible actualizar el stock del producto
                    logger.info("SIMULACIÓN DE CORREO: No fue posible actualizar el stock del producto, razón: {}", err.getMessage())
                )
                .doOnSuccess(updatedProduct -> 
                    // Cumplimiento punto 4. OnSuccess / DoOnSuccess
                    // Simulamos el envío de un correo al usuario notificando
                    // Que el stock del producto fue actualizado
                    logger.info("SIMULACIÓN DE CORREO: Se actualizo el stock del producto {} a {} unidades.", updatedProduct.getName(), updatedProduct.getStock())
                );
    }

    public Flux<Product> findAll() {
        return productRepository.findAll();
    }
}