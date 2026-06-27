package com.hotel.booking.modules.pricing.service;

import com.hotel.booking.core.enums.ActiveStatus;
import com.hotel.booking.core.exception.AppException;
import com.hotel.booking.core.exception.ErrorCode;
import com.hotel.booking.modules.inventory.entity.Hotel;
import com.hotel.booking.modules.inventory.repository.HotelRepository;
import com.hotel.booking.modules.pricing.dto.request.CampaignCreateRequest;
import com.hotel.booking.modules.pricing.dto.request.CampaignUpdateRequest;
import com.hotel.booking.modules.pricing.dto.response.CampaignResponse;
import com.hotel.booking.modules.pricing.entity.Campaign;
import com.hotel.booking.modules.pricing.mapper.CampaignMapper;
import com.hotel.booking.modules.pricing.repository.CampaignRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class CampaignServiceImpl implements CampaignService {

    CampaignRepository campaignRepository;
    HotelRepository hotelRepository;
    CampaignMapper campaignMapper;

    @Override
    @Transactional
    public CampaignResponse createCampaign(CampaignCreateRequest request) {
        log.info("Creating new campaign for hotel ID: {}", request.getHotelId());

        Hotel hotel = validateAndGetHotel(request.getHotelId());
        validateCampaignDates(request.getStartDate(), request.getEndDate());

        if (campaignRepository.existsOverlappingName(request.getHotelId(), request.getName(), request.getStartDate(), request.getEndDate(), null)) {
            throw new AppException(ErrorCode.CAMPAIGN_NAME_OVERLAPPING);
        }

        Campaign campaign = campaignMapper.toEntity(request);
        campaign.setHotel(hotel);
        campaign.setIsDeleted(false);

        if (campaign.getStatus() == null) {
            campaign.setStatus(ActiveStatus.ACTIVE);
        }

        Campaign saved = campaignRepository.save(campaign);
        log.info("Campaign created successfully with ID: {}", saved.getId());
        return campaignMapper.toResponse(saved);
    }

    @Override
    public CampaignResponse getCampaignById(Integer id) {
        log.info("Fetching campaign by ID: {}", id);
        Campaign campaign = campaignRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_FOUND));
        return campaignMapper.toResponse(campaign);
    }

    @Override
    public List<CampaignResponse> getCampaigns(Short hotelId) {
        if (hotelId != null) {
            log.info("Fetching all active campaigns for hotelId: {}", hotelId);
            validateAndGetHotel(hotelId);
            return campaignRepository.findAllByHotelIdAndIsDeletedFalse(hotelId).stream()
                    .map(campaignMapper::toResponse)
                    .toList();
        } else {
            log.info("Fetching all active campaigns");
            return campaignRepository.findAllByIsDeletedFalse().stream()
                    .map(campaignMapper::toResponse)
                    .toList();
        }
    }

    @Override
    @Transactional
    public CampaignResponse updateCampaign(Integer id, CampaignUpdateRequest request) {
        log.info("Updating campaign with ID: {}", id);

        Campaign campaign = campaignRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_FOUND));

        Hotel hotel = validateAndGetHotel(request.getHotelId());
        validateCampaignDates(request.getStartDate(), request.getEndDate());

        if (campaignRepository.existsOverlappingName(request.getHotelId(), request.getName(), request.getStartDate(), request.getEndDate(), id)) {
            throw new AppException(ErrorCode.CAMPAIGN_NAME_OVERLAPPING);
        }

        campaignMapper.updateEntity(request, campaign);
        campaign.setHotel(hotel);

        Campaign updated = campaignRepository.save(campaign);
        log.info("Campaign updated successfully with ID: {}", updated.getId());
        return campaignMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCampaign(Integer id) {
        log.info("Soft-deleting campaign with ID: {}", id);
        Campaign campaign = campaignRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CAMPAIGN_NOT_FOUND));

        campaign.setIsDeleted(true);
        campaignRepository.save(campaign);
        log.info("Campaign soft-deleted successfully with ID: {}", id);
    }

    private Hotel validateAndGetHotel(Short hotelId) {
        if (hotelId == null) {
            throw new AppException(ErrorCode.CAMPAIGN_HOTEL_ID_NOT_NULL);
        }
        return hotelRepository.findByIdAndIsDeletedFalse(hotelId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
    }

    private void validateCampaignDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new AppException(ErrorCode.CAMPAIGN_START_DATE_NOT_NULL);
        }
        if (endDate == null) {
            throw new AppException(ErrorCode.CAMPAIGN_END_DATE_NOT_NULL);
        }
        if (startDate.isAfter(endDate)) {
            throw new AppException(ErrorCode.CAMPAIGN_INVALID_DATE_RANGE);
        }
    }
}
