package com.example.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO implements Serializable {

    @NotNull(message = "Id không được null")
    private Long id;

    @NotNull(message = "UserId không được null")
    private Long userId;

    @NotBlank(message = "Product không được để trống")
    private String product;

    @NotNull(message = "Quantity không được null")
    @Min(value = 1, message = "Quantity phải lớn hơn hoặc bằng 1")
    private Integer quantity;

    @NotNull(message = "Unit price không được null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price phải lớn hơn 0")
    private BigDecimal unitPrice;

    @NotNull(message = "Total amount không được null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount phải lớn hơn 0")
    private BigDecimal totalAmount;

    @NotNull(message = "Status không được null")
    private OrderStatus status;

    private LocalDateTime createdAt;

    @Builder.Default
    private OrderStatus defaultStatus = OrderStatus.PENDING;

    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }
}
