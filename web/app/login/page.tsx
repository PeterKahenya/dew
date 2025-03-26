"use client"

import { useAuth } from "@/contexts/auth"
import { useRouter } from "next/navigation"
import { useState } from "react"

export default function Page() {
    const { login, user, getProfile } = useAuth()
    const router = useRouter()
    const [userLogin, setUserLogin] = useState({
        email: "",
        password: ""
    })

    async function handleLogin(e) {
        e.preventDefault()
        const response = await login(userLogin)
        if (response.message === "Logged In") {
            const profile = await getProfile()
            router.push(`/${profile.id}/today`)
        }
    }

    return <div>
        <h1>Login Page</h1>
        <form>
            <input type="email" placeholder="Email" value={userLogin.email} onChange={(e) => setUserLogin({ ...userLogin, email: e.target.value })} />
            <input type="password" placeholder="Password" value={userLogin.password} onChange={(e) => setUserLogin({ ...userLogin, password: e.target.value })} />
            <button onClick={handleLogin}>Login</button>
        </form>
    </div>
}