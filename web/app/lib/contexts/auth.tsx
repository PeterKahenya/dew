"use client"

import { createContext, useContext, useEffect, useState } from "react";
import { User, UserSignup, Login } from "lib/types";
import axios from "axios";

const AuthContext = createContext(null)
const API_BASE = "http://localhost:8000/api"

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState<User>(null)
    const [isLoading, setIsLoading] = useState(true)

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
            console.log("Error occurred during profile fetch: ", error)
        }).finally(() => {
            setIsLoading(false)
        })
    }

    const signup = async (userSignup: UserSignup) => {
        const signupData = {
            name: userSignup.name,
            email: userSignup.email,
            password: userSignup.password,
            confirm_password: userSignup.confirmPassword,
        }
        const response = axios.post(`${API_BASE}/signup`, signupData)
            .then(response => response)
            .catch(error => {
                console.log(error)
                return error.response.data
            })
        return response
    }

    const login = async (userLogin: Login) => {
        const response = axios.post(`${API_BASE}/login`, userLogin, { withCredentials: true })
            .then(response => {
                return response.data
            })
            .catch(error => {
                return error.response.data
            })
        return response
    }

    const logout = async () => {
        const response = axios.post(`${API_BASE}/logout`, null, { withCredentials: true })
            .then(response => {
                return response.data
            })
            .catch(error => {
                console.log(error.response.data)
            })
        return response
    }
    return <AuthContext.Provider value={{ user, isLoading, signup, login, getProfile, logout }}>
        {!isLoading ? children : <p>Loading</p>}
    </AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)