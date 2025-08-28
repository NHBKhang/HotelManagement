import React, { createContext, useReducer, useContext, useEffect } from 'react';
import { useCookies } from 'react-cookie';

const UserContext = createContext();

const userReducer = (state, action) => {
    switch (action.type) {
        case 'SET_USER':
            return { ...state, currentUser: action.payload };
        case 'LOGOUT':
            return { ...state, currentUser: null };
        default:
            return state;
    }
};

export const UserProvider = ({ children }) => {
    const [cookies, setCookie, removeCookie] = useCookies(['user']);
    const [state, dispatch] = useReducer(userReducer, { currentUser: cookies.user || null });

    useEffect(() => {
        if (state.currentUser) {
            setCookie('user', state.currentUser, { path: '/' });
        } else {
            removeCookie('user');
        }
    }, [state.currentUser, setCookie, removeCookie]);

    const logout = () => {
        removeCookie('user');
        removeCookie('access-token');
        dispatch({ type: 'LOGOUT' });
    };

    const saveToken = async (token) => {
        await setCookie('access-token', token, {
            path: '/',
            maxAge: 86400,
            sameSite: 'strict',
        });
    };

    return (
        <UserContext.Provider value={{ state, dispatch, logout, saveToken }}>
            {children}
        </UserContext.Provider>
    );
};

export const useUserContext = () => {
    return useContext(UserContext);
};