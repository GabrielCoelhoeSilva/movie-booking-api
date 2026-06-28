package com.gabriel.moviebooking.service.impl;

import com.gabriel.moviebooking.dto.booking.BookingCreateRequestDTO;
import com.gabriel.moviebooking.dto.booking.BookingResponseDTO;
import com.gabriel.moviebooking.entity.Booking;
import com.gabriel.moviebooking.entity.Seat;
import com.gabriel.moviebooking.entity.Session;
import com.gabriel.moviebooking.entity.User;
import com.gabriel.moviebooking.enums.BookingStatus;
import com.gabriel.moviebooking.exception.BusinessException;
import com.gabriel.moviebooking.exception.ResourceNotFoundException;
import com.gabriel.moviebooking.mapper.BookingMapper;
import com.gabriel.moviebooking.repository.BookingRepository;
import com.gabriel.moviebooking.repository.SeatRepository;
import com.gabriel.moviebooking.repository.SessionRepository;
import com.gabriel.moviebooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private static final int EXPIRATION_MINUTES = 10;

    private final BookingRepository bookingRepository;
    private final SessionRepository sessionRepository;
    private final SeatRepository seatRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponseDTO create(BookingCreateRequestDTO dto, Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Session session = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found with id: " + dto.getSessionId()));

        List<Seat> seats = seatRepository.findAllById(dto.getSeatIds());

        if (seats.size() != dto.getSeatIds().size()) {
            throw new ResourceNotFoundException("One or more seats not found");
        }

        for (Seat seat : seats) {
            if (!seat.getSession().getId().equals(session.getId())) {
                throw new BusinessException(
                        "Seat " + seat.getRow() + seat.getNumber() + " does not belong to this session");
            }
            if (!seat.isAvailable()) {
                throw new BusinessException(
                        "Seat " + seat.getRow() + seat.getNumber() + " is not available");
            }
        }

        seats.forEach(seat -> seat.setAvailable(false));
        seatRepository.saveAll(seats);

        BigDecimal totalPrice = session.getPrice().multiply(BigDecimal.valueOf(seats.size()));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setSession(session);
        booking.setSeats(seats);
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(totalPrice);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));

        Booking saved = bookingRepository.save(booking);

        return bookingMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public BookingResponseDTO confirm(Long id, Authentication authentication) {

        Booking booking = findEntityById(id);
        validateOwnership(booking, authentication);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Only pending bookings can be confirmed");
        }

        if (booking.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Booking has expired");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);

        return bookingMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public BookingResponseDTO cancel(Long id, Authentication authentication) {

        Booking booking = findEntityById(id);
        validateOwnership(booking, authentication);

        if (booking.getStatus() != BookingStatus.PENDING
                && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessException("Booking cannot be cancelled in its current status");
        }

        releaseSeats(booking);
        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);

        return bookingMapper.toResponseDTO(saved);
    }

    @Override
    public BookingResponseDTO findById(Long id) {
        return bookingMapper.toResponseDTO(findEntityById(id));
    }

    @Override
    public List<BookingResponseDTO> findMyBookings(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return bookingRepository.findByUserId(user.getId())
                .stream()
                .map(bookingMapper::toResponseDTO)
                .toList();
    }

    private void releaseSeats(Booking booking) {
        booking.getSeats().forEach(seat -> seat.setAvailable(true));
        seatRepository.saveAll(booking.getSeats());
    }

    private void validateOwnership(Booking booking, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean isAdmin = user.getRole().name().equals("ADMIN");

        if (!isAdmin && !booking.getUser().getId().equals(user.getId())) {
            throw new BusinessException("You are not allowed to access this booking");
        }
    }

    private Booking findEntityById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + id));
    }
}