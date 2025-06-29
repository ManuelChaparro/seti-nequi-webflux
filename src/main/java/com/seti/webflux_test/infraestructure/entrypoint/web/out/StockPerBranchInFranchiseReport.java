package com.seti.webflux_test.infraestructure.entrypoint.web.out;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StockPerBranchInFranchiseReport {

    String franchiseName;
    List<ProductStockReport> productStockReport;
}