"use client"
import TaskItem from "@/components/TaskItem"
import { useAuth } from "@/contexts/auth"
import { useTasks } from "@/contexts/tasks"
import { ActionIcon, Button, Chip, ChipGroup, Collapse, Flex, Paper, Text, TextInput } from "@mantine/core"
import { IconAdjustmentsHorizontal, IconArrowRight, IconSearch } from "@tabler/icons-react"
import { Task } from "lib/types"
import { useEffect, useState } from "react"
import { DatePickerInput } from '@mantine/dates';

type FiltersState = {
    sort_by: string
    q: string
    created_at?: Date
    completed_at?: Date
    status: string
}

export default function AllTasksPage() {
    const { tasks, filterTasks, deleteTask, isLoading } = useTasks()
    const { user } = useAuth()
    const [filters, setFilters] = useState<FiltersState>({
        sort_by: "created_at,desc",
        q: "",
        status: "all",
        completed_at: null,
        created_at: null
    })
    const [filtersOpen, setFiltersOpen] = useState(false)

    async function handleDelete(task_id: string) {
        const resp = await deleteTask(task_id)
        if (resp.status === 204) {
            console.log("Successfully Deleted")
        } else {
            console.log("Error deleting task", resp)
        }
    }

    async function applyFilters(e) {
        if (filters.completed_at || filters.created_at || filters.status !== "all") {
            const panelFilters = {
                ...(filters.completed_at && { completed_at__gte: filters.completed_at.toISOString() }),
                ...(filters.created_at && { created_at__gte: filters.completed_at.toISOString() }),
                ...(filters.status !== "all" && { is_complete: filters.status === "completed" }),
                sort_by: "created_at,desc"
            }
            console.log("Applying filters ", panelFilters)
            await filterTasks(panelFilters)
        }
    }

    async function handleSearch(event) {
        if (event.key === 'Enter') {
            event.preventDefault();
            event.stopPropagation();
            const searchKeyFilter = { q: filters.q }
            console.log("Searching with filters ", searchKeyFilter)
            await filterTasks(searchKeyFilter)
        }
    }

    return <>
        <Paper shadow="sm" radius={"xl"}>
            <form onSubmit={(e) => e.preventDefault()}>
                <TextInput
                    radius="xl"
                    variant="filled"
                    size="lg"
                    placeholder="Search tasks"
                    value={filters.q}
                    onChange={(e) => setFilters({ ...filters, q: e.target.value })}
                    onKeyDown={handleSearch}
                    leftSection={
                        <ActionIcon onClick={() => setFiltersOpen(!filtersOpen)} size={"input-md"} radius="xl" color="green" variant="light">
                            <IconAdjustmentsHorizontal size={25} stroke={1.5} />
                        </ActionIcon>
                    }
                    rightSection={
                        <IconSearch size={25} stroke={1.5} />
                    }
                />
            </form>
            <Collapse in={filtersOpen} p={"lg"}>
                <Flex justify="flex-start" align={"center"}>
                    <DatePickerInput
                        mx={"md"}
                        placeholder="Created At"
                        value={filters.created_at}
                        onChange={(e) => setFilters({ ...filters, created_at: new Date(e.getTime()) })}
                    />
                    <DatePickerInput
                        mx={"md"}
                        placeholder="Completed At"
                        value={filters.completed_at}
                        onChange={(e) => setFilters({ ...filters, completed_at: new Date(e.getTime()) })}
                    />
                    <ChipGroup multiple={false} onChange={(e) => {
                        setFilters({ ...filters, status: e })
                    }}>
                        <Chip mx={"md"} value="completed" >Completed</Chip>
                        <Chip mx={"md"} value="pending">Pending</Chip>
                    </ChipGroup>
                    <Button onClick={applyFilters}>Apply</Button>
                </Flex>
                <Button variant="light" m={"md"} onClick={() => setFilters({ ...filters, status: "all", completed_at: null, created_at: null })}>Clear Filters</Button>
            </Collapse>
        </Paper>
        <Paper>
            {
                tasks && tasks.data && tasks.data.length > 0 ? (
                    tasks.data.map((task: Task) => <TaskItem key={task.id} task={task} user={user} handleDelete={handleDelete} />)
                ) : (
                    <p>No tasks match that criteria!</p>
                )
            }
        </Paper>
    </>
}