package tech.kayys.erp.catalog.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.catalog.application.api.ProductCommandService;
import tech.kayys.erp.catalog.application.api.ProductQueryService;
import tech.kayys.erp.catalog.application.api.command.CreateProductCommand;
import tech.kayys.erp.catalog.application.api.command.UpdateProductCommand;
import tech.kayys.erp.catalog.application.api.command.ActivateProductCommand;
import tech.kayys.erp.catalog.application.api.command.DeactivateProductCommand;
import tech.kayys.erp.catalog.application.api.command.AdjustStockCommand;
import tech.kayys.erp.catalog.application.api.query.GetProductQuery;
import tech.kayys.erp.catalog.application.api.query.ProductView;
import tech.kayys.erp.catalog.application.api.query.SearchProductsQuery;
import tech.kayys.erp.catalog.domain.identifier.ProductId;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for Product operations.
 * This is a primary adapter in Hexagonal Architecture.
 */
@Path("/api/v1/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Product API", description = "Product management endpoints")
public class ProductResource {

    @Inject
    ProductCommandService productCommandService;

    @Inject
    ProductQueryService productQueryService;

    @POST
    @Operation(summary = "Create a new product")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Product created successfully"),
        @APIResponse(responseCode = "400", description = "Invalid input"),
        @APIResponse(responseCode = "409", description = "Product with SKU already exists")
    })
    public CompletionStage<Response> createProduct(@Valid CreateProductRequest request) {
        CreateProductCommand command = CreateProductCommand.builder()
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .currencyCode(request.getCurrencyCode())
            .sku(request.getSku())
            .build();

        return productCommandService.createProduct(command)
            .thenApply(productId -> Response
                .created(URI.create("/api/v1/products/" + productId.getValue()))
                .entity(new CreateProductResponse(productId))
                .build()
            );
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get product by ID")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Product found"),
        @APIResponse(responseCode = "404", description = "Product not found")
    })
    public CompletionStage<Response> getProduct(@PathParam("id") UUID id) {
        ProductId productId = ProductId.of(id);
        GetProductQuery query = new GetProductQuery(productId);
        
        return productQueryService.getProduct(query)
            .thenApply(productView -> Response.ok(productView).build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @GET
    @Operation(summary = "Search products")
    public CompletionStage<Response> searchProducts(
            @QueryParam("name") String nameContains,
            @QueryParam("sku") String skuStartsWith,
            @QueryParam("minPrice") BigDecimal minPrice,
            @QueryParam("maxPrice") BigDecimal maxPrice,
            @QueryParam("currency") String currencyCode,
            @QueryParam("activeOnly") @DefaultValue("true") Boolean activeOnly,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sort") @DefaultValue("NAME_ASC") String sortBy
    ) {
        SearchProductsQuery.SortBy sortByEnum;
        try {
            sortByEnum = SearchProductsQuery.SortBy.valueOf(sortBy);
        } catch (IllegalArgumentException e) {
            sortByEnum = SearchProductsQuery.SortBy.NAME_ASC;
        }

        SearchProductsQuery query = new SearchProductsQuery(
            nameContains,
            skuStartsWith,
            minPrice != null ? minPrice.doubleValue() : null,
            maxPrice != null ? maxPrice.doubleValue() : null,
            currencyCode,
            activeOnly,
            page,
            size,
            sortByEnum
        );

        return productQueryService.searchProducts(query)
            .thenApply(products -> Response.ok(products).build());
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a product")
    public CompletionStage<Response> updateProduct(
            @PathParam("id") UUID id,
            @Valid UpdateProductRequest request) {
        ProductId productId = ProductId.of(id);
        UpdateProductCommand command = new UpdateProductCommand(
            productId,
            request.getName(),
            request.getDescription(),
            request.getPrice(),
            request.getCurrencyCode()
        );

        return productCommandService.updateProduct(command)
            .thenApply(productId1 -> Response.ok(new UpdateProductResponse(productId1)).build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @POST
    @Path("/{id}/activate")
    @Operation(summary = "Activate a product")
    public CompletionStage<Response> activateProduct(@PathParam("id") UUID id) {
        ProductId productId = ProductId.of(id);
        ActivateProductCommand command = new ActivateProductCommand(productId);

        return productCommandService.activateProduct(command)
            .thenApply(productId1 -> Response.ok().build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalStateException) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @POST
    @Path("/{id}/deactivate")
    @Operation(summary = "Deactivate a product")
    public CompletionStage<Response> deactivateProduct(@PathParam("id") UUID id) {
        ProductId productId = ProductId.of(id);
        DeactivateProductCommand command = new DeactivateProductCommand(productId);

        return productCommandService.deactivateProduct(command)
            .thenApply(productId1 -> Response.ok().build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalStateException) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @POST
    @Path("/{id}/stock")
    @Operation(summary = "Adjust product stock")
    public CompletionStage<Response> adjustStock(
            @PathParam("id") UUID id,
            @Valid AdjustStockRequest request) {
        ProductId productId = ProductId.of(id);
        AdjustStockCommand command = new AdjustStockCommand(
            productId,
            request.getQuantity()
        );

        return productCommandService.adjustStock(command)
            .thenApply(productId1 -> Response.ok().build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    // =========================================================================
    // Request/Response DTOs
    // =========================================================================

    public static class CreateProductRequest {
        @NotNull
        @NotBlank
        private String name;
        
        @NotNull
        @NotBlank
        private String description;
        
        @NotNull
        @Positive
        private BigDecimal price;
        
        @NotNull
        @NotBlank
        @Size(min = 3, max = 3)
        private String currencyCode;
        
        @NotNull
        @NotBlank
        private String sku;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
    }

    public static class UpdateProductRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private String currencyCode;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    }

    public static class AdjustStockRequest {
        @NotNull
        @Min(Integer.MIN_VALUE)
        @Max(Integer.MAX_VALUE)
        private int quantity;

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class CreateProductResponse {
        private final ProductId productId;

        public CreateProductResponse(ProductId productId) {
            this.productId = productId;
        }

        public UUID getProductId() {
            return productId.getValue();
        }
    }

    public static class UpdateProductResponse {
        private final ProductId productId;

        public UpdateProductResponse(ProductId productId) {
            this.productId = productId;
        }

        public UUID getProductId() {
            return productId.getValue();
        }
    }
}