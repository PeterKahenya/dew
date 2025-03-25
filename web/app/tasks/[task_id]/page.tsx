import { use } from "react"

export default function Page({ params, }: { params: Promise<{ task_id: string }> }) {
    const { task_id } = use(params)
    return <div>
        <h1>Task Details</h1>
        <h1>Task {task_id}</h1>
    </div>
}