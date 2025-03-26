"use client"
import { useTasks } from "@/contexts/tasks"
import { Task, TaskUpdate } from "lib/types"
import { use, useState } from "react"

export default function Page({ params, }: { params: Promise<{ task_id: string }> }) {
    const { task_id } = use(params)
    const { tasks, updateTask } = useTasks()
    const task = tasks.data.filter((task: Task) => task.id === task_id)[0]
    const [currentTask, setCurrentTask] = useState<TaskUpdate>({
        title: task.title,
        description: task.description,
        isComplete: task.is_complete,
        completedAt: null
    })

    async function handleSave(e: { preventDefault: () => void }) {
        e.preventDefault()
        const response = await updateTask(task.id, currentTask)
        if (response.status === 200) {
            console.log(response)
        }
    }

    return <div>
        <form>
            <input type="text" value={currentTask.title} onChange={(e) => setCurrentTask({ ...currentTask, title: e.target.value })} placeholder="Task Title" />
            <textarea value={currentTask.description} onChange={(e) => setCurrentTask({ ...currentTask, description: e.target.value })} placeholder="Task Description" />
            <input type="checkbox" checked={currentTask.isComplete}
                onChange={(e) => {
                    const today = new Date().toISOString()
                    setCurrentTask({ ...currentTask, isComplete: e.target.checked, completedAt: e.target.checked ? today : null })
                }} />
            <button onClick={handleSave}>Save</button>
        </form>
    </div>
}