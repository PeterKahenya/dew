"use client"
import { useAuth } from "@/contexts/auth"
import Link from "next/link"
import { useRouter } from "next/navigation"
import { useEffect } from "react"

export default function Page() {
    const { user } = useAuth()
    const router = useRouter()

    useEffect(() => {
        if (user) {
            router.push(`/${user.id}/today`)
        } else {
            router.push("/login")
        }
    }, [user])


    return <div>
        <h1>Welcome to dew!</h1>
        <Link href={"/login"}>Login</Link>
    </div>
}