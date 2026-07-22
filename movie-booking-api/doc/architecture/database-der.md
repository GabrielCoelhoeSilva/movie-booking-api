```mermaid
erDiagram
    CINEMAS ||--|{ ROOMS : "possui"
    ROOMS ||--|{ SESSIONS : "sedia"
    MOVIES ||--|{ SESSIONS : "exibido em"
    USERS ||--|{ BOOKINGS : "faz"
    SESSIONS ||--|{ BOOKINGS : "tem"
    SEATS }|--|{ BOOKINGS : "reservados em"

    CINEMAS {
        Long id PK
        String name
        String location
    }
    ROOMS {
        Long id PK
        String name
        Integer capacity
    }
    SESSIONS {
        Long id PK
        LocalDateTime startTime
        BigDecimal price
    }
    MOVIES {
        Long id PK
        String title
        Integer duration
    }
    BOOKINGS {
        Long id PK
        LocalDateTime createdAt
        String status
    }
    SEATS {
        Long id PK
        String seatNumber
    }