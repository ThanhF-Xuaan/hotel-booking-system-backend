package com.hotel.booking.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    ROLE_NOT_FOUND(2001, "Role not found", HttpStatus.NOT_FOUND),
    ROLE_CODE_EXISTED(2002, "Role code already existed", HttpStatus.BAD_REQUEST),
    ROLE_NAME_NOT_BLANK(2003, "Role name must not be blank", HttpStatus.BAD_REQUEST),
    ROLE_NAME_MAX_LENGTH(2004, "Role name must not exceed 100 characters", HttpStatus.BAD_REQUEST),
    ROLE_CODE_NOT_BLANK(2005, "Role code must not be blank", HttpStatus.BAD_REQUEST),
    ROLE_CODE_MAX_LENGTH(2006, "Role code must not exceed 50 characters", HttpStatus.BAD_REQUEST),
    ROLE_STATUS_NOT_NULL(2007, "Role status must not be null", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_FOUND(2008, "Permission not found", HttpStatus.NOT_FOUND),
    PERMISSION_ALREADY_EXISTS(2009, "Permission already exists", HttpStatus.BAD_REQUEST),
    PERMISSION_ACTION_NOT_BLANK(2010, "Permission action must not be blank", HttpStatus.BAD_REQUEST),
    PERMISSION_RESOURCE_NOT_BLANK(2011, "Permission resource must not be blank", HttpStatus.BAD_REQUEST),
    PERMISSION_STATUS_NOT_NULL(2012, "Permission status must not be null", HttpStatus.BAD_REQUEST),
    GUEST_NOT_FOUND(3001, "Guest not found", HttpStatus.NOT_FOUND),
    GUEST_PHONE_EXISTED(3002, "Guest phone number already existed", HttpStatus.BAD_REQUEST),
    GUEST_PHONE_NOT_BLANK(3003, "Guest phone must not be blank", HttpStatus.BAD_REQUEST),
    HOTEL_NOT_FOUND(4001, "Hotel not found", HttpStatus.NOT_FOUND),
    HOTEL_NAME_EXISTED(4002, "Hotel name already existed", HttpStatus.BAD_REQUEST),
    HOTEL_NAME_NOT_BLANK(4003, "Hotel name must not be blank", HttpStatus.BAD_REQUEST),
    HOTEL_ADDRESS_NOT_BLANK(4004, "Hotel address must not be blank", HttpStatus.BAD_REQUEST),
    HOTEL_CHECKIN_TIME_NOT_NULL(4005, "Hotel check-in time must not be null", HttpStatus.BAD_REQUEST),
    HOTEL_CHECKOUT_TIME_NOT_NULL(4006, "Hotel check-out time must not be null", HttpStatus.BAD_REQUEST),
    HOTEL_SERVICE_FEE_NOT_NULL(4007, "Hotel service fee percent must not be null", HttpStatus.BAD_REQUEST),
    HOTEL_SERVICE_FEE_MIN(4008, "Hotel service fee percent must be at least 0.0", HttpStatus.BAD_REQUEST),
    HOTEL_SERVICE_FEE_MAX(4009, "Hotel service fee percent must not exceed 100.0", HttpStatus.BAD_REQUEST),
    ROOM_TYPE_NOT_FOUND(5001, "Room type not found", HttpStatus.NOT_FOUND),
    ROOM_TYPE_CODE_EXISTED(5002, "Room type code already existed", HttpStatus.BAD_REQUEST),
    ROOM_TYPE_CODE_NOT_BLANK(5003, "Room type code must not be blank", HttpStatus.BAD_REQUEST),
    ROOM_TYPE_NAME_NOT_BLANK(5004, "Room type name must not be blank", HttpStatus.BAD_REQUEST),
    ROOM_TYPE_STATUS_NOT_NULL(5005, "Room type status must not be null", HttpStatus.BAD_REQUEST),
    ROOM_TYPE_NAME_EXISTED(5006, "Room type name already existed", HttpStatus.BAD_REQUEST),
    ROOM_BED_NOT_FOUND(6001, "Room bed not found", HttpStatus.NOT_FOUND),
    ROOM_BED_NAME_EXISTED(6002, "Room bed name already existed", HttpStatus.BAD_REQUEST),
    ROOM_BED_NAME_NOT_BLANK(6003, "Room bed name must not be blank", HttpStatus.BAD_REQUEST),
    ROOM_BED_NAME_MAX_LENGTH(6004, "Room bed name must not exceed 100 characters", HttpStatus.BAD_REQUEST),
    ROOM_BED_SIZE_MAX_LENGTH(6005, "Room bed size must not exceed 50 characters", HttpStatus.BAD_REQUEST),
    ROOM_FEATURE_NOT_FOUND(7001, "Room feature not found", HttpStatus.NOT_FOUND),
    ROOM_FEATURE_CODE_EXISTED(7002, "Room feature code already existed", HttpStatus.BAD_REQUEST),
    ROOM_FEATURE_CODE_NOT_BLANK(7003, "Room feature code must not be blank", HttpStatus.BAD_REQUEST),
    ROOM_FEATURE_NAME_NOT_BLANK(7004, "Room feature name must not be blank", HttpStatus.BAD_REQUEST),
    ROOM_FEATURE_CODE_MAX_LENGTH(7005, "Room feature code must not exceed 50 characters", HttpStatus.BAD_REQUEST),
    ROOM_FEATURE_NAME_MAX_LENGTH(7006, "Room feature name must not exceed 150 characters", HttpStatus.BAD_REQUEST),
    ROOM_FEATURE_CATEGORY_MAX_LENGTH(7007, "Room feature category must not exceed 50 characters", HttpStatus.BAD_REQUEST),
    ROOM_FEATURE_NAME_EXISTED(7008, "Room feature name already existed", HttpStatus.BAD_REQUEST),
    
    VAT_RULE_NOT_FOUND(8001, "Vat rule not found", HttpStatus.NOT_FOUND),
    VAT_RULE_CODE_EXISTED(8002, "Vat rule code already existed", HttpStatus.BAD_REQUEST),
    VAT_RULE_NAME_EXISTED(8003, "Vat rule name already existed", HttpStatus.BAD_REQUEST),
    VAT_RULE_INVALID_DATE_RANGE(8004, "Start date must be before or equal to end date", HttpStatus.BAD_REQUEST),
    VAT_RULE_CODE_NOT_BLANK(8005, "Vat rule code must not be blank", HttpStatus.BAD_REQUEST),
    VAT_RULE_NAME_NOT_BLANK(8006, "Vat rule name must not be blank", HttpStatus.BAD_REQUEST),
    VAT_RULE_PERCENT_NOT_NULL(8007, "Vat rule percent must not be null", HttpStatus.BAD_REQUEST),
    VAT_RULE_APPLIES_TO_NOT_NULL(8008, "Vat rule appliesTo must not be null", HttpStatus.BAD_REQUEST),
    VAT_RULE_CODE_MAX_LENGTH(8009, "Vat rule code must not exceed 50 characters", HttpStatus.BAD_REQUEST),
    VAT_RULE_NAME_MAX_LENGTH(8010, "Vat rule name must not exceed 150 characters", HttpStatus.BAD_REQUEST),
    VAT_RULE_PERCENT_MIN(8011, "Vat rule percent must be at least 0.00", HttpStatus.BAD_REQUEST),
    VAT_RULE_PERCENT_MAX(8012, "Vat rule percent must not exceed 100.00", HttpStatus.BAD_REQUEST),
    VAT_RULE_STATUS_NOT_NULL(8013, "Vat rule status must not be null", HttpStatus.BAD_REQUEST),
    
    HOLIDAY_NOT_FOUND(8101, "Holiday calendar not found", HttpStatus.NOT_FOUND),
    HOLIDAY_DATE_EXISTED(8102, "A holiday has already been configured for this date", HttpStatus.BAD_REQUEST),
    HOLIDAY_NAME_NOT_BLANK(8103, "Holiday name must not be blank", HttpStatus.BAD_REQUEST),
    HOLIDAY_DATE_NOT_NULL(8104, "Holiday date must not be null", HttpStatus.BAD_REQUEST),
    HOLIDAY_NAME_MAX_LENGTH(8105, "Holiday name must not exceed 150 characters", HttpStatus.BAD_REQUEST),
    HOLIDAY_STATUS_NOT_NULL(8106, "Holiday status must not be null", HttpStatus.BAD_REQUEST),
    HOLIDAY_NAME_EXISTED(8107, "Holiday name already existed", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
