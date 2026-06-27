package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.modules.pricing.dto.request.CampaignCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.CampaignUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.CampaignResponse;

import java.util.List;

public interface CampaignService {

    CampaignResponse createCampaign(CampaignCreateRequest request);

    CampaignResponse getCampaignById(Integer id);

    List<CampaignResponse> getCampaigns(Short hotelId);

    CampaignResponse updateCampaign(Integer id, CampaignUpdateRequest request);

    void deleteCampaign(Integer id);
}
