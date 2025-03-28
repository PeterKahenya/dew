"use client"
import TaskItem from "@/components/TaskItem"
import { useAuth } from "@/contexts/auth"
import { useTasks } from "@/contexts/tasks"
import { Box, Button, Divider, Flex, Title } from "@mantine/core"
import { IconPlus } from "@tabler/icons-react"
import { Task } from "lib/types"
import { useRouter } from "next/navigation"
import { useEffect, useState } from "react"

function getTodaysDateISO() {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return today.toISOString()
}

export default function TodayPage() {
    const router = useRouter()
    const { logout, user } = useAuth()
    const { tasks, filterTasks, deleteTask } = useTasks()
    const [filters, setFilters] = useState({
        sort_by: "created_at,desc",
        created_at__gte: getTodaysDateISO()
    })

    useEffect(() => {
        console.log("TodayPage useEffect, filtering for only today's tasks")
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
        <div>
            <Title>Welcome back <br /><strong>{user.name.toUpperCase()}</strong></Title>
            <Flex direction={{ base: 'column', sm: 'row' }} justify={{ sm: 'flex-end' }} gap={{ base: 'xl', sm: 'xl' }} m={10}>
                <Button onClick={() => router.push("new")} leftSection={<IconPlus size={14} />} >New Task</Button>
            </Flex>
            <Divider
                my="xs"
                variant="dashed"
                labelPosition="center"
                label={
                    <>
                        {"🙌"}
                        <Box ml={5}>Your tasks for today</Box>
                    </>
                }
            />
            <Box>
                {tasks && tasks.data && tasks.data.length > 0 ? (
                    tasks.data.map((task: Task) => <TaskItem key={task.id} task={task} user={user} handleDelete={handleDelete} />)
                ) : (
                    <p>No tasks for today</p>
                )}
            </Box>
        </div>
    </div>
}