package com.example.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class AppViewModel : ViewModel() {

    // Localization Map & UI Theme Controls
    var isBangla by mutableStateOf(false)
        private set

    var darkThemeOverride by mutableStateOf(true) // Dynamic light/dark state, default dark
        private set

    // User Session Profile State
    private val _currentUser = MutableStateFlow(User(isLogged = false))
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Transport Schedule Core States
    private val _schedules = MutableStateFlow(initialSchedules)
    val schedules: StateFlow<List<TransportSchedule>> = _schedules.asStateFlow()

    // Booking Registries
    private val _activeBookings = MutableStateFlow<List<BkgTicket>>(emptyList())
    val activeBookings: StateFlow<List<BkgTicket>> = _activeBookings.asStateFlow()

    // Rides & Rentals State
    private val _rideShares = MutableStateFlow(initialRides)
    val rideShares: StateFlow<List<RideShare>> = _rideShares.asStateFlow()

    private val _vehicles = MutableStateFlow(initialVehicles)
    val vehicles: StateFlow<List<RentalVehicle>> = _vehicles.asStateFlow()

    // Parcel Dispatch Logs
    private val _parcelRequests = MutableStateFlow<List<ParcelRequest>>(emptyList())
    val parcelRequests: StateFlow<List<ParcelRequest>> = _parcelRequests.asStateFlow()

    // Notification Feed
    private val _notifications = MutableStateFlow(initialNotifications)
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Admin Metrics
    var systemRevenue by mutableStateOf(145800.0)
        private set
    var totalRidersBooked by mutableStateOf(485)
        private set

    // Simple Form Search State
    var searchFrom by mutableStateOf("")
    var searchTo by mutableStateOf("")
    var selectedCategory by mutableStateOf(TransportType.BUS)

    fun toggleLanguage() {
        isBangla = !isBangla
    }

    fun toggleTheme() {
        darkThemeOverride = !darkThemeOverride
    }

    fun translate(key: String): String {
        return TranslationRegistry.translate(key, isBangla)
    }

    // Authentication Actions
    fun loginUser(name: String, email: String, phone: String): Boolean {
        if (name.isBlank() || email.isBlank() || phone.isBlank()) return false
        _currentUser.update {
            it.copy(
                name = name,
                email = email,
                phone = phone,
                isLogged = true
            )
        }
        addSystemNotification(
            title = if (isBangla) "সাফল্যের সাথে লগইন হয়েছে!" else "Access Logged In!",
            description = if (isBangla) "স্বাগতম ${name}, আপনার চলোবিডি ড্যাশবোর্ড প্রস্তুত।" else "Welcome $name, your CholoBD account has signed in.",
            category = PromoCategory.BOOKING
        )
        return true
    }

    fun logoutUser() {
        _currentUser.update { User(isLogged = false) }
    }

    fun depositFunds(amount: Double) {
        _currentUser.update {
            it.copy(walletBalance = it.walletBalance + amount)
        }
    }

    // Ticket Booking Engine (Deducts Taka from Wallet balance, increments admin metrics)
    fun bookTicket(schedule: TransportSchedule, passengerName: String, phone: String): String? {
        val totalCost = schedule.price
        val activeUser = _currentUser.value

        if (activeUser.walletBalance < totalCost) {
            return if (isBangla) "আপনার ওয়ালেটে পর্যাপ্ত টাকা নেই!" else "Insufficient wallet funds!"
        }

        // Deduct money
        _currentUser.update {
            it.copy(walletBalance = it.walletBalance - totalCost)
        }

        val boardCode = "CBD-" + Random.nextInt(100000, 999999).toString()
        val seatNumber = "A" + Random.nextInt(1, 10).toString()

        val ticket = BkgTicket(
            companyName = schedule.companyName,
            transportType = schedule.type,
            from = schedule.routeFrom,
            to = schedule.routeTo,
            time = schedule.timeStart,
            seatNo = seatNumber,
            passengerName = passengerName,
            phone = phone,
            price = totalCost,
            boardingCode = boardCode
        )

        _activeBookings.update { listOf(ticket) + it }
        
        // Update admin stats
        systemRevenue += totalCost
        totalRidersBooked += 1

        // Notify
        addSystemNotification(
            title = if (isBangla) "টিকিট নিশ্চিত হয়েছে!" else "Journey Confirmed!",
            description = if (isBangla) "${schedule.companyName} (${schedule.type}) সিট: ${seatNumber}, কোড: ${boardCode}" 
                          else "${schedule.companyName} (${schedule.type}) Seat: $seatNumber, Code: $boardCode",
            category = PromoCategory.BOOKING
        )
        return null // success
    }

    // Ride Sharing Requests
    fun postRideShare(from: String, to: String, driver: String, price: Double, isBike: Boolean, phone: String) {
        val newRide = RideShare(
            driverName = driver,
            routeFrom = from,
            routeTo = to,
            isBike = isBike,
            price = price,
            rating = 5.0,
            timeAvailable = "Leaving shortly",
            phoneNumber = phone
        )
        _rideShares.update { listOf(newRide) + it }
        
        addSystemNotification(
            title = if (isBangla) "নতুন রাইড শেয়ার পোস্ট করা হয়েছে" else "New Ride Share Posted!",
            description = if (isBangla) "${from} থেকে ${to} (৳${price})" else "From $from to $to (৳$price)",
            category = PromoCategory.PROMO
        )
    }

    // Rental Booking
    fun bookRental(vehicle: RentalVehicle, days: Int): String? {
        val cost = vehicle.pricePerDay * days
        val activeUser = _currentUser.value

        if (activeUser.walletBalance < cost) {
            return if (isBangla) "ভাড়া বুকিংয়ের জন্য পর্যাপ্ত ব্যালেন্স নেই!" else "Insufficient balance for rental!"
        }

        _currentUser.update {
            it.copy(walletBalance = it.walletBalance - cost)
        }

        systemRevenue += cost

        addSystemNotification(
            title = if (isBangla) "গাড়ি ভাড়া সফল!" else "Rental Confirmed!",
            description = if (isBangla) "${vehicle.name} বুকিং হয়েছে ${days} দিনের জন্য।" 
                          else "Successfully requested ${vehicle.name} for $days days.",
            category = PromoCategory.BOOKING
        )
        return null
    }

    // Parcel Delivery Requests
    fun submitParcelDelivery(
        sender: String, senderPhone: String, from: String,
        recipient: String, recipientPhone: String, to: String,
        weight: Double, cargoType: String, sizeCategory: String, cost: Double
    ): String? {
        val activeUser = _currentUser.value
        if (activeUser.walletBalance < cost) {
            return if (isBangla) "পার্সেল পাঠানোর জন্য অবশিষ্টাংশ নেই!" else "Insufficient balance for parcel delivery!"
        }

        _currentUser.update {
            it.copy(walletBalance = it.walletBalance - cost)
        }

        val request = ParcelRequest(
            senderName = sender,
            senderPhone = senderPhone,
            senderLoc = from,
            recipientName = recipient,
            recipientPhone = recipientPhone,
            recipientLoc = to,
            weightKg = weight,
            sizeCategory = sizeCategory,
            cargoType = cargoType,
            estimatedCost = cost
        )

        _parcelRequests.update { listOf(request) + it }
        systemRevenue += cost

        addSystemNotification(
            title = if (isBangla) "ডেলিভারি যাত্রা শুরু হয়েছে" else "Parcel Despatched!",
            description = if (isBangla) "${from} থেকে ${to} (ওজন: ${weight} কেজি)" else "From $from to $to (Weight: $weight Kg)",
            category = PromoCategory.BOOKING
        )
        return null
    }

    // Admin Dashboard Schedule Injections
    fun addCustomSchedule(
        type: TransportType, company: String, from: String, to: String,
        start: String, end: String, fare: Double, seats: Int, code: String
    ) {
        val newSchedule = TransportSchedule(
            type = type,
            companyName = company,
            routeFrom = from,
            routeTo = to,
            timeStart = start,
            timeEnd = end,
            price = fare,
            availableSeats = seats,
            rating = 5.0,
            vehicleNumber = code
        )
        _schedules.update { list -> list + newSchedule }

        addSystemNotification(
            title = if (isBangla) "নতুন ট্রিপ চালু হয়েছে!" else "New Transit Route Launched!",
            description = if (isBangla) "${company} (${type}) ${from} - ${to} চালু করা হয়েছে।" 
                          else "Registered ${company} (${type}) for transit from $from to $to.",
            category = PromoCategory.ALERT
        )
    }

    // Notification Feed Utilities
    fun markNotificationsAsRead() {
        _notifications.update { items ->
            items.map { it.copy(read = true) }
        }
    }

    fun dismissNotification(id: String) {
        _notifications.update { items ->
            items.filter { it.id != id }
        }
    }

    fun addSystemNotification(title: String, description: String, category: PromoCategory) {
        val notify = NotificationItem(
            title = title,
            description = description,
            timestamp = "Just Now",
            category = category
        )
        _notifications.update { listOf(notify) + it }
    }
}
