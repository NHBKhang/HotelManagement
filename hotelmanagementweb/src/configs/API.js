import axios from "axios";
import { useCookies } from 'react-cookie';

const API_URL = process.env.REACT_APP_API_URL;

export const endpoints = {
    'login': 'login',
    'users': 'users',
    'user': (userId) => `users/${userId}`,
    'current-user': 'current-user',
    'rooms': 'rooms',
    'room': (roomId) => `rooms/${roomId}`,
}

export const useAuthAPI = () => {
    const [cookies] = useCookies(['access-token']);

    return axios.create({
        baseURL: API_URL,
        headers: {
            Authorization: `Bearer ${cookies['access-token'] || ""}`,
        },
    });
};

export default axios.create({
    baseURL: API_URL
});