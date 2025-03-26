"use client"
import TaskItem from "@/components/TaskItem"
import { useAuth } from "@/contexts/auth"
import { useTasks } from "@/contexts/tasks"
import { Task } from "lib/types"
import { useEffect, useState } from "react"


export default function Page() {
    const { tasks, filterTasks, deleteTask } = useTasks()
    const { user } = useAuth()
    const [filters, setFilters] = useState({
        sort_by: "created_at,desc"
    })

    useEffect(() => {
        console.log("Tasks useEffect, should reset filters")
        filterTasks(filters)
    }, [filters])

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
        <h1>All tasks search/filter</h1>
        {tasks && tasks.data && tasks.data.length > 0 ? (
            tasks.data.map((task: Task) => <TaskItem key={task.id} task={task} user={user} handleDelete={handleDelete} />)
        ) : (
            <p>No tasks match that criteria!</p>
        )}
    </div>
}