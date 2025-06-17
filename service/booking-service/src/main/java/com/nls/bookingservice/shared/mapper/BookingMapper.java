package com.nls.bookingservice.shared.mapper;

import com.nls.bookingservice.api.dto.request.CreateBookingReq;
import com.nls.bookingservice.domain.entity.Booking;
import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.response.BookingDetailsRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking convertCreateBookingToBooking(CreateBookingReq request);

    @Mapping(target = "bookingId", source = "id")
    CreatePaymentReq convertCreateBookingToCreatePaymentReq(Booking request);

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "roomType", source = "property.roomType")
    BookingDetailsRes convertBookingToBookingDetailsRes(Booking request);

}
