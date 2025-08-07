// In src/main/resources/static/js/script.js
document.addEventListener('DOMContentLoaded', () => {
    const API_URL = 'http://localhost:8080/api';
    const roomSelect = document.getElementById('rooms');
    const bookingForm = document.getElementById('bookingForm');
    const bookingList = document.getElementById('bookingList');

    // 1. Fetch available rooms and populate the dropdown
    async function fetchAvailableRooms() {
        try {
            const response = await fetch(`${API_URL}/rooms/available`);
            const rooms = await response.json();
            roomSelect.innerHTML = rooms.map(room =>
                `<option value="${room.id}">${room.type} - Room ${room.roomNumber} (₹${room.price})</option>`
            ).join('');
        } catch (error) {
            console.error('Failed to fetch rooms:', error);
        }
    }

    // 2. Fetch all bookings and display them
    async function fetchBookings() {
        try {
            const response = await fetch(`${API_URL}/bookings`);
            const bookings = await response.json();
            bookingList.innerHTML = bookings.map(b =>
                `<p>${b.guestName} has booked Room ${b.room.roomNumber} from ${b.checkInDate} to ${b.checkOutDate}</p>`
            ).join('');
        } catch (error) {
            console.error('Failed to fetch bookings:', error);
        }
    }

    // 3. Handle the booking form submission
    bookingForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const bookingData = {
            guestName: document.getElementById('guestName').value,
            checkInDate: document.getElementById('checkIn').value,
            checkOutDate: document.getElementById('checkOut').value,
            room: { id: roomSelect.value }
        };

        try {
            const response = await fetch(`${API_URL}/bookings`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(bookingData),
            });

            if (response.ok) {
                alert('Booking successful!');
                bookingForm.reset();
                // Refresh both lists after a successful booking
                fetchAvailableRooms();
                fetchBookings();
            } else {
                alert('Booking failed. The room might have just been taken.');
            }
        } catch (error) {
            console.error('Error creating booking:', error);
        }
    });

    // Initial data load
    fetchAvailableRooms();
    fetchBookings();
});