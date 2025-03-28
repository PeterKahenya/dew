"use client"
import TaskForm from "@/components/TaskForm"
import { useTasks } from "@/contexts/tasks"
import { Box, Button, Checkbox, Container, Divider, Flex, Text, Textarea, TextInput } from "@mantine/core"
import { IconPencil } from "@tabler/icons-react"
import { TaskCreate } from "lib/types"
import { useRouter } from "next/navigation"
import { useState } from "react"

export default function Page() {
    const { createTask } = useTasks()
    const router = useRouter()

    async function handleSave(task: TaskCreate) {
        const resp = await createTask(task)
        console.log(resp, resp.status, resp.data)
        if (resp.status === 201) {
            console.log("Task creation succeeded, redirect back")
            router.back()
        } else {
            console.log("An error occurred ", resp)
        }
    }
    return <>
        <TaskForm handleSave={handleSave} />
    </>
}