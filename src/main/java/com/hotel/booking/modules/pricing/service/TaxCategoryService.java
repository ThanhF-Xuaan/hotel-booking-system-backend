package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.dto.request.TaxCategoryCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.TaxCategoryUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.TaxCategoryResponse;

import java.util.List;

public interface TaxCategoryService {
    TaxCategoryResponse createTaxCategory(TaxCategoryCreateRequest request);
    TaxCategoryResponse getTaxCategoryById(Integer id);
    List<TaxCategoryResponse> getAllTaxCategories();
    TaxCategoryResponse updateTaxCategory(Integer id, TaxCategoryUpdateRequest request);
    void deleteTaxCategory(Integer id);
}
