import axios from "axios";
import { useMemo } from "react";
import { useCookies } from 'react-cookie';

const API_URL = process.env.REACT_APP_API_URL;

export const endpoints = {
    'login': 'login',
    'users': 'users',
    'user': (userId) => `users/${userId}`,
    'current-user': 'current-user',
    'rooms': 'rooms',
    'room': (roomId) => `rooms/${roomId}`,
    'room-types': 'room-types',
    'bookings': 'bookings',
    'my-booking': (bookingId) => `bookings/my-bookings/${bookingId}`,
    'my-bookings': 'bookings/my-bookings',
    'vnpay-payment': 'payments/vnpay',
    'vnpay-return': 'payments/vnpay-return',
}

export const useAuthAPI = () => {
    const [cookies] = useCookies(['access-token']);

    return useMemo(() => axios.create({
        baseURL: API_URL,
        headers: {
            Authorization: `Bearer ${cookies['access-token'] || ""}`,
        },
    }), [cookies]);
};

export default axios.create({
    baseURL: API_URL
});