"use client"
import TaskItem from "@/components/TaskItem"
import { useAuth } from "@/contexts/auth"
import { useTasks } from "@/contexts/tasks"
import { Task } from "lib/types"
import { useRouter } from "next/navigation"
import { useEffect } from "react"

export default function TodayPage() {
    console.log("Rendering TodayPage")
    const router = useRouter()
    const { logout, user } = useAuth()
    const { tasks, filterTasks } = useTasks()

    const handleLogout = async () => {
        const response = await logout()
        if (response.message === "Logged Out") {
            router.push("/login")
        }
    }

    return <div>
        <h1>Today View</h1>
        <h2>Welcome back {user.name}</h2>
        <button onClick={() => router.push("new")}>New Task</button>
        <ul>
            <li><a href="/today">Today</a></li>
            <li><a href="tasks">All Tasks</a></li>
            <li><a href="/chat">Chat</a></li>
            <button onClick={handleLogout}>Logout</button>
        </ul>
        <div>
            {tasks && tasks.data && tasks.data.length > 0 ? (
                tasks.data.map((task: Task) => <TaskItem key={task.id} task={task} />)
            ) : (
                <p>No tasks for today</p>
            )}
        </div>
    </div>
}