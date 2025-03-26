"use client"
import SideBar from "@/components/SideBar";
import { useAuth } from "@/contexts/auth";
import { TasksProvider } from "@/contexts/tasks";
import { usePathname, useRouter } from "next/navigation";
import { use, useEffect, useState } from "react";
import "./layout.css"; // Ensure you have global styles

export default function UserHomeLayout({ children, params }: { children: React.ReactNode, params: Promise<{ user_id: string }> }) {
    const { user_id } = use(params)
    const { user, isLoading } = useAuth()
    const pathname = usePathname().split("/")[2]
    const [activeTab, setActiveTab] = useState(pathname)
    const router = useRouter()


    useEffect(() => {
        if (!isLoading && !user) {
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
        <div className="layout">
            <SideBar active={activeTab} setActive={(tab) => setActiveTab(tab)} />
            <main className="content">
                {children}
            </main>
        </div>
    </TasksProvider>
}