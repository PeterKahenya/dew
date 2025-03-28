import { Box, Button, Checkbox, Divider, Flex, Text, Textarea, TextInput } from "@mantine/core";
import { IconPencil } from "@tabler/icons-react";
import { Task, TaskCreate, TaskUpdate } from "lib/types";
import { useRouter } from "next/navigation";
import { useState } from "react";

export default function TaskForm({ currentTask, handleSave }: { currentTask?: Task, handleSave: (taskChange: TaskCreate | TaskUpdate) => void }) {
    const [task, setTask] = useState({
        title: currentTask ? currentTask.title : "",
        description: currentTask ? currentTask.description : "",
        is_complete: currentTask ? currentTask.is_complete : false
    })
    const router = useRouter()
    return <form>
        <TextInput
            variant="unstyled"
            placeholder="Heading..."
            value={task.title}
            size="xl"
            fw={700}
            onChange={(e) => setTask({ ...task, title: e.target.value })}
        />
        <Divider
            my="xs"
            variant="dashed"
            c={"gray"}
            labelPosition="center"
            label={
                <>
                    <IconPencil size={12} />
                    <Box ml={5}>Enter details below</Box>
                </>
            }
        />
        <Textarea
            size="xl"
            variant="unstyled"
            placeholder="Details"
            value={task.description}
            autosize
            minRows={30}
            onChange={(e) => setTask({ ...task, description: e.target.value })}
        />
        <Flex direction={{ sm: "row" }} justify={{ sm: "space-between" }}>
            <Checkbox
                checked={task.is_complete}
                onChange={(e) => setTask({ ...task, is_complete: e.target.checked })}
                label={<Text c="green">Is Task Complete?</Text>}
            />
            <Flex>
                <Button variant="transparent" onClick={() => router.back()} mx={"sm"}>Discard</Button>
                <Button px={"xl"} onClick={(e) => {
                    e.preventDefault()
                    handleSave(task)
                }}>Save</Button>
            </Flex>
        </Flex>
    </form>
}