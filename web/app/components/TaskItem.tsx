import { useAuth } from "@/contexts/auth";
import { useTasks } from "@/contexts/tasks";
import { ActionIcon, Anchor, Checkbox, Flex, Paper, Text, Title } from "@mantine/core";
import { IconArrowsVertical, IconListTree, IconTrashFilled } from "@tabler/icons-react";
import { Task, User } from "lib/types";


export default function TaskItem({ task, user, handleDelete }: { task: Task, user: User, handleDelete: (task_id: string) => void }) {

    return (
        <Paper shadow="xs" p="md" m={"md"}>
            <Checkbox
                checked={task.is_complete}
                readOnly
                label="Complete?"
            />
            <Title size={20} my={"md"}>{task.title}</Title>
            <Text size="sm">{task.description}</Text>
            <Flex justify={{ sm: "flex-end" }}>
                <Anchor href={`/${user.id}/tasks/${task.id}`} mx={5}>
                    <ActionIcon variant="light" >
                        <IconArrowsVertical size={20} />
                    </ActionIcon>
                </Anchor>
                <ActionIcon variant="light" onClick={() => handleDelete(task.id)}>
                    <IconTrashFilled size={12} />
                </ActionIcon>
            </Flex>
        </Paper>)
}