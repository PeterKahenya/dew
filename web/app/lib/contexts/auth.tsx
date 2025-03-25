"use client"

import { createContext, useContext, useEffect, useState } from "react";
import { User, UserSignup, Login } from "lib/types";
import axios from "axios";

const AuthContext = createContext(null)
const API_BASE = "http://localhost:8000/api"

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState<User>(null)

    useEffect(() => {
        getProfile()
    }, [])

    const getProfile = async () => {
        return axios.get(`${API_BASE}/me`, {
            withCredentials: true
        }).then(response => {
            setUser(response.data)
            return response.data
        }).catch(error => {
            console.log(error);
        });
    }

    const signup = async (UserSignup: UserSignup) => {
        const response = axios.post(`${API_BASE}/signup`, UserSignup)
            .then(response => response.data)
            .catch(error => {
                console.log(error)
            })
        return response
    }

    const login = async (userLogin: Login) => {
        const response = axios.post(`${API_BASE}/login`, userLogin, { withCredentials: true })
            .then(response => response.data)
            .catch(error => {
                console.log(error.response.data)
            })
        return response
    }

    const logout = async () => {
        const response = axios.post(`${API_BASE}/logout`, null, { withCredentials: true })
            .then(response => response.data)
            .catch(error => {
                console.log(error.response.data)
            })
        return response
    }

    return <AuthContext.Provider value={{ user, signup, login, getProfile, logout }}>
        {children}
    </AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)