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
    const { tasks, filterTasks, deleteTask } = useTasks()

    const handleLogout = async () => {
        const response = await logout()
        if (response.message === "Logged Out") {
            router.push("/login")
        }
    }

    async function handleDelete(task_id: string) {
        const resp = await deleteTask(task_id)
        console.log(resp)
        if (resp.status === 204) {
            console.log("Successfully Deleted")
        } else {
            console.log("Error deleting task", resp)
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
                tasks.data.map((task: Task) => <TaskItem key={task.id} task={task} user={user} handleDelete={handleDelete} />)
            ) : (
                <p>No tasks for today</p>
            )}
        </div>
    </div>
}