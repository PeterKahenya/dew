import { useAuth } from "@/contexts/auth";
import { useTasks } from "@/contexts/tasks";
import { Task, User } from "lib/types";


export default function TaskItem({ task, user, handleDelete }: { task: Task, user: User, handleDelete: (task_id: string) => void }) {

    return <div>
        <a href={`/${user.id}/tasks/${task.id}`}>
            <strong>{task.title}</strong>
            <p>{task.description}</p>
        </a>
        <button onClick={() => handleDelete(task.id)}>delete</button>
    </div>
}