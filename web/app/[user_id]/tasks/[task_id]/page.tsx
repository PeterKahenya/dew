"use client"
import TaskForm from "@/components/TaskForm"
import { useTasks } from "@/contexts/tasks"
import { notifications } from "@mantine/notifications"
import { Task, TaskUpdate } from "lib/types"
import { useRouter } from "next/navigation"
import { use } from "react"

export default function Page({ params, }: { params: Promise<{ task_id: string }> }) {
    const { task_id } = use(params)
    const { tasks, filterTasks, updateTask } = useTasks()
    const router = useRouter()
    const task = tasks.data.filter((task: Task) => task.id === task_id)[0]

    async function handleSave(taskUpdate: TaskUpdate) {
        const response = await updateTask(task.id, taskUpdate)
        if (response.status === 200) {
            notifications.show({
                title: "Hooray!",
                message: "Updated Successfully!",
                color: "green",
            })
            setInterval(() => router.back(), 1000)
        } else {
            notifications.show({
                title: "Oh No!",
                message: "Something went wrong with the update",
                color: "red",
            })
        }
    }

    return <div>
        <TaskForm handleSave={handleSave} currentTask={task} />
    </div>
}