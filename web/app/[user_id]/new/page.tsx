"use client"
import { useTasks } from "@/contexts/tasks"
import { useRouter } from "next/navigation"
import { useState } from "react"

export default function Page() {
    const [task, setTask] = useState({
        title: "",
        description: "",
        isComplete: false
    })
    const { createTask } = useTasks()
    const router = useRouter()

    async function handleSave(e) {
        e.preventDefault()
        const resp = await createTask(task)
        console.log(resp, resp.status, resp.data)
        if (resp.status === 201) {
            console.log("Task creation succeeded, redirect back")
            router.back()
        } else {
            console.log("An error occurred ", resp)
        }
    }
    return <div>
        <h1>New Task Page</h1>
        <form>
            <input type="text" value={task.title} onChange={(e) => setTask({ ...task, title: e.target.value })} placeholder="Task Title" />
            <textarea value={task.description} onChange={(e) => setTask({ ...task, description: e.target.value })} placeholder="Task Description" />
            <input type="checkbox" checked={task.isComplete} onChange={(e) => setTask({ ...task, isComplete: e.target.checked })} />
            <button onClick={handleSave}>Save</button>
        </form>
    </div>
}