"use client"
import { useAuth } from "@/contexts/auth";
import { TasksProvider } from "@/contexts/tasks";
import { useRouter } from "next/navigation";
import { use, useEffect } from "react";

export default function UserHomeLayout({ children, params }: { children: React.ReactNode, params: Promise<{ user_id: string }> }) {
    const { user_id } = use(params)
    const { user, isLoading } = useAuth()
    const router = useRouter()

    useEffect(() => {
        // console.log("Securepage layout useEffect", { user, isLoading })
        if (!isLoading && !user) {
            // console.log("No user found redirecting to login");
            router.push("/login");
        }
    }, [user, isLoading, router])
    if (isLoading) {
        return <div>Loading...</div>;
    }
    if (!user) {
        return null;
    }

    return <TasksProvider user_id={user_id}>
        {children}
    </TasksProvider>
}