package com.example.data

import java.util.UUID

enum class TransportType { BUS, TRAIN, LAUNCH, FLIGHT }
enum class VehicleType { SEDAN, MICRO, SUV, BIKE }
enum class PromoCategory { ALERT, BOOKING, PROMO }

data class User(
    val name: String = "Sabbir Rahman",
    val email: String = "sabbir@cholobd.com",
    val phone: String = "+8801712345678",
    val walletBalance: Double = 2450.0,
    val isLogged: Boolean = false
)

data class TransportSchedule(
    val id: String = UUID.randomUUID().toString(),
    val type: TransportType,
    val companyName: String,
    val routeFrom: String,
    val routeTo: String,
    val timeStart: String,
    val timeEnd: String,
    val price: Double,
    val availableSeats: Int,
    val rating: Double,
    val vehicleNumber: String
)

data class RideShare(
    val id: String = UUID.randomUUID().toString(),
    val driverName: String,
    val routeFrom: String,
    val routeTo: String,
    val isBike: Boolean,
    val price: Double,
    val rating: Double,
    val timeAvailable: String,
    val phoneNumber: String
)

data class RentalVehicle(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: VehicleType,
    val seats: Int,
    val pricePerDay: Double,
    val fuelType: String,
    val driverIncluded: Boolean,
    val description: String
)

data class ParcelRequest(
    val id: String = UUID.randomUUID().toString(),
    val senderName: String,
    val senderPhone: String,
    val senderLoc: String,
    val recipientName: String,
    val recipientPhone: String,
    val recipientLoc: String,
    val weightKg: Double,
    val sizeCategory: String, // Light, Medium, Heavy, Cargo
    val cargoType: String, // Document, Box, Fragile
    val estimatedCost: Double,
    val trackingStatus: String = "Dispatched"
)

data class NotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val timestamp: String,
    val category: PromoCategory,
    val read: Boolean = false
)

data class BkgTicket(
    val id: String = UUID.randomUUID().toString(),
    val companyName: String,
    val transportType: TransportType,
    val from: String,
    val to: String,
    val time: String,
    val seatNo: String,
    val passengerName: String,
    val phone: String,
    val price: Double,
    val boardingCode: String,
    val status: String = "CONFIRMED"
)

object TranslationRegistry {
    val enToBn = mapOf(
        "app_title" to "চলোবিডি",
        "tagline" to "যাত্রীদের সঠিক ও স্মার্ট পরিবহন সঙ্গী",
        "home" to "হোম",
        "rides_rent" to "রাইড ও রেন্টাল",
        "delivery_track" to "পার্সেল ও ট্র্যাকিং",
        "admin" to "অ্যাডমিন প্যানেল",
        "profile" to "প্রোফাইল",
        "bus" to "বাস",
        "train" to "ট্রেন",
        "launch" to "লঞ্চ",
        "flight" to "ফ্লাইট",
        "search_hint_from" to "কোথা থেকে...",
        "search_hint_to" to "কোথায় যাবেন...",
        "find_transport" to "পরিবহন অনুসন্ধান",
        "book_now" to "এখনই বুক করুন",
        "taka_unit" to "৳",
        "avail_seats" to "আসন বাকি",
        "wallet_balance" to "ওয়ালেট ব্যালেন্স",
        "add_funds" to "টাকা যোগ করুন",
        "active_bookings" to "সক্রিয় বুকিং সমূহ",
        "no_bookings" to "কোন বুকিং নেই",
        "notifications" to "বিজ্ঞপ্তি সমূহ",
        "ride_sharing" to "রাইড শেয়ারিং",
        "vehicle_rental" to "গাড়ি ভাড়া",
        "parcel_delivery" to "পার্সেল ডেলিভারি",
        "gps_tracking" to "দূরত্ব ট্র্যাকিং",
        "booking_success" to "বুকিং সফল হয়েছে!",
        "admin_dashboard" to "মালিক ড্যাশবোর্ড",
        "live_metrics" to "লাইভ পরিসংখ্যান",
        "active_routes" to "চলতি রুটসমূহ",
        "ticket_sales" to "টিকেট বিক্রি",
        "passenger_count" to "যাত্রী সংখ্যা",
        "active_cars" to "সক্রিয় গাড়ি",
        "add_route" to "নতুন রুট যুক্ত করুন",
        "sign_in" to "লগইন করুন",
        "sign_up" to "রেজিস্ট্রেশন",
        "email" to "ইমেইল",
        "password" to "পাসওয়ার্ড",
        "phone" to "ফোন নম্বর",
        "name" to "সম্পূর্ণ নাম",
        "language" to "ভাষা (Language)",
        "theme" to "থিম পরিবর্তন",
        "booking_dialog_title" to "যাত্রী বুকিং ফর্ম",
        "confirm_bk" to "বুকিং নিশ্চিত করুন",
        "tracking_text" to "লাইভ ট্র্যাকিং এক্সপ্রেসওয়ে",
        "current_position" to "বর্তমান অবস্থান",
        "speed" to "গতি",
        "eta" to "পৌঁছানোর সময়",
        "estimated_cost" to "আনুমানিক খরচ",
        "parcel_type" to "পার্সেলের ধরন",
        "weight" to "ওজন (কেজি)",
        "calculate" to "হিসাব করুন",
        "order_parcel" to "পার্সেল পাঠান"
    )

    fun translate(key: String, isBangla: Boolean): String {
        return if (isBangla) {
            enToBn[key] ?: key
        } else {
            // Convert camelCase or snake_case key to human readable English if not found
            val direct = enToBn[key]
            if (direct != null) {
                // If we want english, return customized equivalent
                englishEquivalents[key] ?: key.replace("_", " ").capitalizeWords()
            } else {
                key
            }
        }
    }

    private val englishEquivalents = mapOf(
        "app_title" to "CholoBD",
        "tagline" to "Smart Travel & Transport Partner",
        "home" to "Home",
        "rides_rent" to "Rides & Rental",
        "delivery_track" to "Parcel & GPS",
        "admin" to "Admin Board",
        "profile" to "Profile",
        "bus" to "Bus",
        "train" to "Train",
        "launch" to "Launch",
        "flight" to "Flight",
        "search_hint_from" to "From...",
        "search_hint_to" to "To...",
        "find_transport" to "Find Transport",
        "book_now" to "Book Now",
        "taka_unit" to "৳",
        "avail_seats" to "seats left",
        "wallet_balance" to "Wallet Balance",
        "add_funds" to "Add Money",
        "active_bookings" to "Active Bookings",
        "no_bookings" to "No bookings found",
        "notifications" to "Notifications",
        "ride_sharing" to "Ride Sharing",
        "vehicle_rental" to "Vehicle Rental",
        "parcel_delivery" to "Parcel Delivery",
        "gps_tracking" to "GPS Status",
        "booking_success" to "Booking Confirmed!",
        "admin_dashboard" to "Admin Dashboard",
        "live_metrics" to "Live System Performance",
        "active_routes" to "Active Routes",
        "ticket_sales" to "Ticket Sales",
        "passenger_count" to "Total Riders",
        "active_cars" to "Online Transports",
        "add_route" to "Create Schedule",
        "sign_in" to "Sign In",
        "sign_up" to "Register",
        "email" to "Email Address",
        "password" to "Password",
        "phone" to "Mobile Number",
        "name" to "Full Name",
        "language" to "Change Language",
        "theme" to "Interface Mode",
        "booking_dialog_title" to "Passenger Entry",
        "confirm_bk" to "Confirm Reservation",
        "tracking_text" to "Expressway Speed & Progress Tracker",
        "current_position" to "Current Point",
        "speed" to "Speed",
        "eta" to "ETA",
        "estimated_cost" to "Est. Delivery Fare",
        "parcel_type" to "Parcel Type",
        "weight" to "Weight (Kg)",
        "calculate" to "Calculate Fare",
        "order_parcel" to "Request Delivery"
    )

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else it } }
}

// Global Prepopulated Mock Schedulers
val initialSchedules = listOf(
    TransportSchedule(
        type = TransportType.BUS,
        companyName = "Green Line Paribahan",
        routeFrom = "Dhaka (Gabtoli)",
        routeTo = "Cox's Bazar",
        timeStart = "08:15 AM",
        timeEnd = "05:30 PM",
        price = 1400.0,
        availableSeats = 18,
        rating = 4.8,
        vehicleNumber = "Dhaka-Metro-Ba-14-3849"
    ),
    TransportSchedule(
        type = TransportType.BUS,
        companyName = "Hanif Enterprise",
        routeFrom = "Dhaka (Sayedabad)",
        routeTo = "Chittagong",
        timeStart = "09:30 AM",
        timeEnd = "03:00 PM",
        price = 850.0,
        availableSeats = 24,
        rating = 4.5,
        vehicleNumber = "Dhaka-Metro-Ba-11-9876"
    ),
    TransportSchedule(
        type = TransportType.BUS,
        companyName = "Ena Transport",
        routeFrom = "Dhaka (Mohakhali)",
        routeTo = "Sylhet",
        timeStart = "07:00 AM",
        timeEnd = "12:30 PM",
        price = 750.0,
        availableSeats = 12,
        rating = 4.3,
        vehicleNumber = "Dhaka-Metro-Ba-15-5421"
    ),
    TransportSchedule(
        type = TransportType.TRAIN,
        companyName = "Subarna Express",
        routeFrom = "Dhaka (Kamalapur)",
        routeTo = "Chittagong",
        timeStart = "04:30 PM",
        timeEnd = "09:45 PM",
        price = 650.0,
        availableSeats = 45,
        rating = 4.7,
        vehicleNumber = "701-UP"
    ),
    TransportSchedule(
        type = TransportType.TRAIN,
        companyName = "Parabat Express",
        routeFrom = "Dhaka (Kamalapur)",
        routeTo = "Sylhet",
        timeStart = "06:20 AM",
        timeEnd = "12:40 PM",
        price = 560.0,
        availableSeats = 8,
        rating = 4.4,
        vehicleNumber = "709-UP"
    ),
    TransportSchedule(
        type = TransportType.LAUNCH,
        companyName = "M.V. Green Line 3",
        routeFrom = "Dhaka (Sadarghat)",
        routeTo = "Barishal",
        timeStart = "08:30 AM",
        timeEnd = "02:00 PM",
        price = 900.0,
        availableSeats = 50,
        rating = 4.9,
        vehicleNumber = "L-Barishal-38"
    ),
    TransportSchedule(
        type = TransportType.LAUNCH,
        companyName = "M.V. Adventure 9",
        routeFrom = "Dhaka (Sadarghat)",
        routeTo = "Bhola",
        timeStart = "09:00 PM",
        timeEnd = "05:00 AM",
        price = 450.0,
        availableSeats = 120,
        rating = 4.6,
        vehicleNumber = "L-Bhola-122"
    ),
    TransportSchedule(
        type = TransportType.FLIGHT,
        companyName = "Biman Bangladesh Airlines",
        routeFrom = "Dhaka (HSIA)",
        routeTo = "Chittagong",
        timeStart = "11:15 AM",
        timeEnd = "12:00 PM",
        price = 4500.0,
        availableSeats = 14,
        rating = 4.7,
        vehicleNumber = "Boeing-737-BG83"
    ),
    TransportSchedule(
        type = TransportType.FLIGHT,
        companyName = "US-Bangla Airlines",
        routeFrom = "Dhaka (HSIA)",
        routeTo = "Cox's Bazar",
        timeStart = "01:30 PM",
        timeEnd = "02:30 PM",
        price = 5600.0,
        availableSeats = 6,
        rating = 4.9,
        vehicleNumber = "ATR-72-US109"
    )
)

val initialRides = listOf(
    RideShare(
        driverName = "Mofizul Islam",
        routeFrom = "Dhanmondi, Dhaka",
        routeTo = "Uttara, Dhaka",
        isBike = true,
        price = 280.0,
        rating = 4.9,
        timeAvailable = "Just now",
        phoneNumber = "+8801812849382"
    ),
    RideShare(
        driverName = "Arif Ahmed",
        routeFrom = "Dhaka",
        routeTo = "Mymensingh",
        isBike = false,
        price = 1500.0,
        rating = 4.6,
        timeAvailable = "Leaving in 30 mins",
        phoneNumber = "+8801511223344"
    ),
    RideShare(
        driverName = "Jewel Rana",
        routeFrom = "Banani, Dhaka",
        routeTo = "Motijheel, Dhaka",
        isBike = true,
        price = 190.0,
        rating = 4.7,
        timeAvailable = "10 mins away",
        phoneNumber = "+8801912948573"
    ),
    RideShare(
        driverName = "Kazi Amin",
        routeFrom = "Gazipur",
        routeTo = "Dhaka Airport",
        isBike = false,
        price = 800.0,
        rating = 4.5,
        timeAvailable = "Leaving in 10 mins",
        phoneNumber = "+8801314352525"
    )
)

val initialVehicles = listOf(
    RentalVehicle(
        name = "Toyota Allion 2021",
        type = VehicleType.SEDAN,
        seats = 4,
        pricePerDay = 3500.0,
        fuelType = "CNG & Octane",
        driverIncluded = true,
        description = "Fully air-conditioned luxury sedan, perfect for corporate and family travel across Bangladesh."
    ),
    RentalVehicle(
        name = "Toyota Noah Esquire 2019",
        type = VehicleType.MICRO,
        seats = 7,
        pricePerDay = 5200.0,
        fuelType = "Octane & LPG",
        driverIncluded = true,
        description = "Large, comfortable premium microbus with high roof and executive comfort, perfect for longer tours."
    ),
    RentalVehicle(
        name = "Mitsubishi Pajero Sport",
        type = VehicleType.SUV,
        seats = 7,
        pricePerDay = 8500.0,
        fuelType = "Diesel",
        driverIncluded = true,
        description = "Powerful premium 4X4 SUV, ideal for hill treks in Sylhet or Chittagong Hill Tracts."
    ),
    RentalVehicle(
        name = "Yamaha FZS V3 (Motorbike)",
        type = VehicleType.BIKE,
        seats = 1,
        pricePerDay = 1200.0,
        fuelType = "Petrol",
        driverIncluded = false,
        description = "Smooth 150cc travel bike for lone travelers or adventure seekers wanting independent tours."
    )
)

val initialNotifications = listOf(
    NotificationItem(
        title = "শুভ যাত্রা (Welcome to CholoBD!)",
        description = "Get flat 200৳ discount on your first bus ticket booking with coupon: CHOLO200",
        timestamp = "Just Now",
        category = PromoCategory.PROMO,
        read = false
    ),
    NotificationItem(
        title = "Expressway Route Alert!",
        description = "Slight congestion at Padma Bridge Toll Plaza. Plan an extra 15 minutes of travel time.",
        timestamp = "2 hours ago",
        category = PromoCategory.ALERT,
        read = false
    ),
    NotificationItem(
        title = "Subarna Express Seat Alert",
        description = "Seats for Train Dhaka-Chittagong on 18th June are filling up rapidly! Lock yours now.",
        timestamp = "Yesterday",
        category = PromoCategory.ALERT,
        read = true
    )
)
